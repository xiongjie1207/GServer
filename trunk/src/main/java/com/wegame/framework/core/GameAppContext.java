package com.wegame.framework.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;

/**
 * Copyright (c) 2015-2016, James Xiong 熊杰 (xiongjie.cn@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by xiongjie on 2017/1/2.
 */
public class GameAppContext {
    private static ApplicationContext applicationContext;
    private static DefaultListableBeanFactory defaultListableBeanFactory;
    public static ApplicationContext getApplicationContext() {
        return GameAppContext.applicationContext;
    }
    public static  DefaultListableBeanFactory getDefaultListableBeanFactory(){return GameAppContext.defaultListableBeanFactory;}

    public static void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException {
        GameAppContext.applicationContext = applicationContext;
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        GameAppContext.defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
    }
    public static Resource getResource(String path){
        return GameAppContext.applicationContext.getResource(path);
    }

    public static <T> T getBean(Class<T> clazz) {
        return GameAppContext.applicationContext.getBean(clazz);
    }
    public static boolean isExist(Class<?> clazz){
        try {
            GameAppContext.applicationContext.getBean(clazz);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public static <T> T getBean(String beanName) {
        return (T) GameAppContext.applicationContext.getBean(beanName);
    }

}