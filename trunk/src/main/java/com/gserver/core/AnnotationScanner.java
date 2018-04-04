package com.gserver.core;

import com.gserver.aop.Interceptor;
import com.gserver.aop.InterceptorBuilder;
import com.gserver.aop.annotation.Before;
import com.gserver.aop.annotation.Commander;
import com.gserver.aop.annotation.Ignore;
import com.gserver.core.annotation.ActionKey;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.lang.reflect.Method;

public class AnnotationScanner implements BeanPostProcessor, PriorityOrdered {
    Logger logger = Logger.getLogger(this.getClass());

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        try {
            Commander commander = bean.getClass().getAnnotation(Commander.class);
            if (commander == null) {
                return bean;
            }
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method method : methods) {
                ActionKey actionKeyAnnotation = method.getAnnotation(ActionKey.class);
                if (actionKeyAnnotation != null) {
                    Interceptor[] beforeInterceptors;
                    Ignore ignoreAnnotation = method.getAnnotation(Ignore.class);
                    if (ignoreAnnotation == null) {
                        Before beforeAnnotation = bean.getClass().getAnnotation(Before.class);
                        if (method.getAnnotation(Before.class) != null) {
                            beforeAnnotation = method.getAnnotation(Before.class);
                        }
                        beforeInterceptors = InterceptorBuilder.build(beforeAnnotation);
                    } else {
                        beforeInterceptors = InterceptorBuilder.build(null);
                    }
                    Action action = new Action(actionKeyAnnotation.value(), bean, method, beforeInterceptors);
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