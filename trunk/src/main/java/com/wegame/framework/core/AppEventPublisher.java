package com.wegame.framework.core;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @Author xiongjie
 * @Date 2023/07/03 22:27
 **/
public class AppEventPublisher {
    private static ApplicationEventPublisher applicationEventPublisher;
    public static void setPublisher(ApplicationEventPublisher publisher){
        applicationEventPublisher = publisher;
    }
    public void publish(ApplicationEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
