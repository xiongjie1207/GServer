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

import java.lang.reflect.Method;

public class Action {
    private Object commander;
    private Method method;
    private Integer actionKey;
    private Packet packet;
    private final Interceptor[] beforeInterceptors;

    public Action(Integer actionKey, Object commander, Method method, Interceptor[] beforeInterceptors) {
        this.setActionKey(actionKey);
        this.setCommander(commander);
        this.setMethod(method);
        this.beforeInterceptors = beforeInterceptors;
    }

    public Interceptor[] getBeforeInterceptors() {
        return beforeInterceptors;
    }

    public Object getCommander() {
        return commander;
    }


    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    private void setCommander(Object commander) {
        this.commander = commander;
    }

    public Method getMethod() {
        return method;
    }

    private void setMethod(Method method) {
        this.method = method;
    }

    public Integer getActionKey() {
        return actionKey;
    }

    private void setActionKey(Integer actionKey) {
        this.actionKey = actionKey;
    }

    @Override
    public String toString() {
        return this.commander.getClass().getSimpleName() + ":" + this.method.getName();
    }
}
