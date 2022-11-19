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
 * Created by xiongjie on 2017/3/4.
 */

package com.wegame.framework.aop;


import com.wegame.framework.core.Action;

/**
 * Invocation is used to invoke the interceptors and the target method
 */
@SuppressWarnings("unchecked")
public class Invocation {

    private final Action action;


    public Invocation(Action action) {
        this.action = action;
    }


    public void invoke() {
        if (action.getBeforeInterceptor() != null) {
            action.getBeforeInterceptor().intercept(action);
        } else {
            action.invoke();
        }

    }

}
