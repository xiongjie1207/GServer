package com.gserver.core;
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
 * Created by xiongjie on 2016/12/22.
 */

import com.gserver.aop.Interceptor;
import com.gserver.aop.InterceptorBuilder;
import com.gserver.aop.annotation.Before;
import com.gserver.aop.annotation.Ignore;
import com.gserver.core.annotation.ActionKey;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;

public class ActionCommanderBuilder {
    Logger logger = Logger.getLogger(ActionCommanderBuilder.class);

    public void buildCommanderAction(Commander commander) {

        try {
            Method[] methods = commander.getClass().getDeclaredMethods();
            for (Method method : methods) {
                ActionKey actionKeyAnnotation = method.getAnnotation(ActionKey.class);
                if (actionKeyAnnotation != null) {
                    Interceptor[] beforeInterceptors;
                    Ignore ignoreAnnotation = method.getAnnotation(Ignore.class);
                    if (ignoreAnnotation == null) {
                        Before beforeAnnotation = commander.getClass().getAnnotation(Before.class);
                        if (method.getAnnotation(Before.class) != null) {
                            beforeAnnotation = method.getAnnotation(Before.class);
                        }
                        beforeInterceptors = InterceptorBuilder.build(beforeAnnotation);
                    } else {
                        beforeInterceptors = InterceptorBuilder.build(null);
                    }
                    Action action = new Action(actionKeyAnnotation.value(), commander, method, beforeInterceptors);
                    ActionMapping.getInstance().addAction(action);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("", e);
        }

    }
}
