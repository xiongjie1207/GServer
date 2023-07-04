package com.wegame.framework.core;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.wegame.aop.annotation.Ignore;
import com.wegame.core.annotation.ActionKey;
import com.wegame.framework.aop.Interceptor;
import com.wegame.framework.aop.InterceptorBuilder;
import com.wegame.framework.aop.annotation.Around;
import com.wegame.framework.component.IComponent;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameComponentScanner implements BeanPostProcessor, PriorityOrdered {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
        throws BeansException {
        try {

            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method method : methods) {
                ActionKey actionKeyAnnotation = method.getAnnotation(ActionKey.class);
                if (actionKeyAnnotation != null) {
                    List<Interceptor> beforeInterceptors = new ArrayList<>();
                    Ignore ignoreAnnotation = method.getAnnotation(Ignore.class);
                    if (ignoreAnnotation == null) {
                        Around aroundAnnotation = bean.getClass().getAnnotation(Around.class);
                        if (aroundAnnotation != null) {
                            Interceptor[] interceptors = InterceptorBuilder.build(aroundAnnotation);
                            Collections.addAll(beforeInterceptors, interceptors);
                        }

                    }
                    Around aroundAnnotation = method.getAnnotation(Around.class);
                    if (aroundAnnotation != null) {

                        Interceptor[] interceptors = InterceptorBuilder.build(aroundAnnotation);
                        Collections.addAll(beforeInterceptors, interceptors);
                    }

                    if (beforeInterceptors.size() > 1) {
                        for (int i = beforeInterceptors.size() - 1; i > 0; i--) {
                            beforeInterceptors.get(i - 1).setNext(beforeInterceptors.get(i));
                        }
                    }
                    Interceptor interceptor = null;
                    if (beforeInterceptors.size() > 0) {
                        interceptor = beforeInterceptors.get(0);
                    }
                    MethodAccess methodAccess = MethodAccess.get(bean.getClass());
                    int index = methodAccess.getIndex(method.getName());
                    Action action =
                        new Action(actionKeyAnnotation.module(),actionKeyAnnotation.pid(), methodAccess, bean, index,
                            interceptor);
                    ActionMapping.getInstance().addAction(action);
                }
            }
            if (IComponent.class.isAssignableFrom(bean.getClass())) {
                ComponentManager.getInstance().getComponents().add((IComponent) bean);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LoggerFactory.getLogger(this.getClass()).error("", e);
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
        throws BeansException {
        return bean;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}