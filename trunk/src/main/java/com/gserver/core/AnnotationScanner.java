package com.gserver.core;

import com.gserver.aop.Interceptor;
import com.gserver.aop.InterceptorBuilder;
import com.gserver.aop.annotation.Before;
import com.gserver.aop.annotation.Ignore;
import com.gserver.core.annotation.ActionKey;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotationScanner implements BeanPostProcessor, PriorityOrdered {
    Logger logger = Logger.getLogger(this.getClass());

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
                        Before beforeAnnotation = bean.getClass().getAnnotation(Before.class);
                        if (beforeAnnotation != null) {
                            Interceptor[] interceptors = InterceptorBuilder.build(beforeAnnotation);
                            for (Interceptor interceptor : interceptors) {
                                beforeInterceptors.add(interceptor);
                            }
                        }

                    }
                    Before beforeAnnotation = method.getAnnotation(Before.class);
                    if (beforeAnnotation != null) {

                        Interceptor[] interceptors = InterceptorBuilder.build(beforeAnnotation);
                        for (Interceptor interceptor : interceptors) {
                            beforeInterceptors.add(interceptor);
                        }
                    }
                    Interceptor interceptor = null;
                    if (beforeInterceptors.size() > 1) {
                        for (int i = beforeInterceptors.size() - 1; i > 0; i--) {
                            beforeInterceptors.get(i - 1).setNext(beforeInterceptors.get(i));
                        }
                    }
                    if (beforeInterceptors.size() > 0) {
                        interceptor = beforeInterceptors.get(0);
                    }
                    Action action = new Action(actionKeyAnnotation.value(), bean, method, interceptor);
                    ActionMapping.getInstance().addAction(action);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("", e);
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