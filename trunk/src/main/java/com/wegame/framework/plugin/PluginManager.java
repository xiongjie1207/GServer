package com.wegame.framework.plugin;

import com.wegame.framework.core.GameAppContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.springframework.util.StringUtils.stripFilenameExtension;

@Slf4j
public class PluginManager {
    private static final String REQUEST_MAPPING_HANDLER_MAPPING="requestMappingHandlerMapping";
    private PluginManager(){}
    private static PluginManager instance;

    public static PluginManager getInstance(){
        if(instance==null){
            instance = new PluginManager();
        }
        return instance;
    }
    private final Map<String, PluginClassLoader> myClassLoaderCenter = new ConcurrentHashMap<>();

    /**
     *
     * @param name 插件名称
     * @param path jar包路径
     */
    public void load(String name, String path) {
        try {
            URL url = new URL("jar:file:" + path + "!/");
            // 创建自定义类加载器，并加到map中方便管理
            PluginClassLoader myClassloader = new PluginClassLoader(new URL[]{url});
            myClassLoaderCenter.put(name, myClassloader);
            URLConnection urlConnection = url.openConnection();
            JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
            // 获取jar文件
            JarFile jarFile = jarURLConnection.getJarFile();
            Enumeration<JarEntry> entries = jarFile.entries();
            Set<Class<?>> initBeanClass = new HashSet<>(jarFile.size());
            // 遍历文件
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    // 1. 加载类到jvm中
                    // 获取类的全路径名
                    String className = stripFilenameExtension(jarEntry.getName()).replace('/', '.');
                    // 1.1进行反射获取
                    myClassloader.loadClass(className);
                }
            }
            List<IPlugin> plugins = new ArrayList<>();
            for (Map.Entry<String, Class<?>> entry : myClassloader.getLoadedClasses().entrySet()) {
                String className = entry.getKey();
                Class<?> clazz = entry.getValue();
                // 2. 将有@spring注解的类交给spring管理
                // 2.1 判断是否注入spring
                boolean flag = SpringAnnotationUtils.hasSpringAnnotation(clazz);
                if (flag) {
                    registerBean(className,clazz);
                    if(SpringAnnotationUtils.hasControllerAnnotation(clazz)){
                        registerController(className);
                    }
                    initBeanClass.add(clazz);
                }
                if (IPlugin.class.isAssignableFrom(clazz)) {
                    IPlugin plugin;
                    if(flag){
                        String beanName = beanName(className);
                        plugin = GameAppContext.getBean(beanName);
                    }else{
                        plugin = (IPlugin) clazz.getDeclaredConstructor().newInstance();
                    }
                    plugins.add(plugin);
                }
            }
            // spring bean实际注册
            initBeanClass.forEach(GameAppContext.getDefaultListableBeanFactory()::getBean);
            plugins.sort(Comparator.comparingInt(IPlugin::order));
            for (IPlugin plugin : plugins) {
                Method method = plugin.getClass().getDeclaredMethod("install");
                method.invoke(plugin);
                myClassloader.addPlugin(plugin);
            }
        } catch (Exception e) {
            log.error("读取{} 文件异常", name);
            log.error(e.getMessage(), e);
        }
    }
    private String beanName(String className){
        String packageName = className.substring(0, className.lastIndexOf(".") + 1);
        String beanName = StringUtils.unqualify(className);
        beanName = packageName + StringUtils.uncapitalize(beanName);
        return beanName;
    }
    private void registerBean(String className,Class<?> clazz){
        // 2.2交给spring管理
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        // 此处beanName使用全路径名是为了防止beanName重复
        String beanName = beanName(className);
        // 2.3注册到spring的beanFactory中
        GameAppContext.getDefaultListableBeanFactory().registerBeanDefinition(beanName, beanDefinition);
        // 2.4允许注入和反向注入
        GameAppContext.getDefaultListableBeanFactory().autowireBean(clazz);
        GameAppContext.getDefaultListableBeanFactory().initializeBean(clazz, beanName);
    }
    /**
     * 注册controller
     */
    private void registerController(String className) throws Exception {

        RequestMappingHandlerMapping requestMappingHandlerMapping =
                GameAppContext.getBean(REQUEST_MAPPING_HANDLER_MAPPING);
        String beanName = beanName(className);
        //注册Controller
        Method method = requestMappingHandlerMapping.getClass().getSuperclass().getSuperclass().
                getDeclaredMethod("detectHandlerMethods", Object.class);
        //将private改为可使用
        method.setAccessible(true);
        method.invoke(requestMappingHandlerMapping, beanName);
    }


    /**
     * 卸载controller
     * @param className
     */
    private void unregisterController(String className) {
        final RequestMappingHandlerMapping requestMappingHandlerMapping = GameAppContext.getBean(REQUEST_MAPPING_HANDLER_MAPPING);

        String handler = beanName(className);
        Object controller = GameAppContext.getBean(handler);
        final Class<?> targetClass = controller.getClass();
        ReflectionUtils.doWithMethods(targetClass, method -> {
            Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
            try {
                Method createMappingMethod = RequestMappingHandlerMapping.class.
                        getDeclaredMethod("getMappingForMethod", Method.class, Class.class);
                createMappingMethod.setAccessible(true);
                RequestMappingInfo requestMappingInfo = (RequestMappingInfo)
                        createMappingMethod.invoke(requestMappingHandlerMapping, specificMethod, targetClass);
                if (requestMappingInfo != null) {
                    requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ReflectionUtils.USER_DECLARED_METHODS);
    }
    /**
     * 插件名称
     * @param name
     */
    public void unload(String name) {
        try {
            try (PluginClassLoader pluginClassLoader = myClassLoaderCenter.remove(name)) {
                // 获取beanFactory，准备从spring中卸载
                Set<String> beanNames = new HashSet<>();
                for (Map.Entry<String, Class<?>> entry : pluginClassLoader.getLoadedClasses().entrySet()) {
                    // 1.1 截取beanName
                    String key = entry.getKey();
                    String beanName = beanName(key);

                    // 获取bean，如果获取失败，表名这个类没有加到spring容器中，则跳出本次循环
                    Object bean;
                    try {
                        bean = GameAppContext.getBean(beanName);
                    } catch (Exception e) {
                        // 异常说明spring中没有这个bean
                        continue;
                    }
                    if(SpringAnnotationUtils.hasControllerAnnotation(entry.getValue())){
                        unregisterController(key);
                    }
                    // 2.0从spring中移除，这里的移除是仅仅移除的bean，并未移除bean定义
                    beanNames.add(beanName);
                    GameAppContext.getDefaultListableBeanFactory().destroyBean(beanName, bean);
                }
                // 移除bean定义
                Field mergedBeanDefinitions = GameAppContext.getDefaultListableBeanFactory().getClass()
                        .getSuperclass()
                        .getSuperclass().getDeclaredField("mergedBeanDefinitions");
                mergedBeanDefinitions.setAccessible(true);
                Map<String, RootBeanDefinition> rootBeanDefinitionMap = ((Map<String, RootBeanDefinition>) mergedBeanDefinitions.get(GameAppContext.getDefaultListableBeanFactory()));
                for (String beanName : beanNames) {
                    GameAppContext.getDefaultListableBeanFactory().removeBeanDefinition(beanName);
                    // 父类bean定义去除
                    rootBeanDefinitionMap.remove(beanName);
                }
                // 卸载类加载器
                pluginClassLoader.unload();
            }
        } catch (Exception e) {
            log.error("动态卸载{}，从类加载器中卸载失败", name);
            log.error(e.getMessage(), e);
        }
    }
}
