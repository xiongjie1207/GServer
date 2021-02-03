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

import com.esotericsoftware.reflectasm.MethodAccess;
import com.gserver.aop.Interceptor;
import com.gserver.components.net.packet.IPacket;

public class Action {
    private Object commander;
    private Integer actionKey;
    private IPacket packet;
    private int methodIndex;
    private MethodAccess methodAccess;
    private final Interceptor beforeInterceptor;
    public Action(Integer actionKey, MethodAccess methodAccess, Object commander, int methodIndex, Interceptor beforeInterceptor) {
        this.actionKey = actionKey;
        this.commander = commander;
        this.methodAccess = methodAccess;
        this.methodIndex = methodIndex;
        this.beforeInterceptor = beforeInterceptor;
    }
    public void invoke(){
        methodAccess.invoke(commander, methodIndex);
    }
    public Interceptor getBeforeInterceptor() {
        return beforeInterceptor;
    }

    public Object getCommander() {
        return commander;
    }

    public int getMethodIndex() {
        return methodIndex;
    }

    public MethodAccess getMethodAccess() {
        return methodAccess;
    }

    public void setPacket(IPacket packet) {
        this.packet = packet;
    }

    public IPacket getPacket() {
        return this.packet;
    }


    public Integer getActionKey() {
        return actionKey;
    }



    @Override
    public String toString() {
        return this.commander.getClass().getSimpleName() + ":" + this.actionKey;
    }
}
