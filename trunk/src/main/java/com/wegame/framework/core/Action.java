package com.wegame.framework.core;
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
import com.wegame.framework.aop.Interceptor;
import com.wegame.framework.packet.IPacket;
import java.text.MessageFormat;
import lombok.Getter;
import lombok.Setter;

public class Action {
    @Getter
    private final Interceptor beforeInterceptor;
    @Getter
    private final Object commander;
    @Getter
    private final Short pid;
    @Getter
    private final Short module;
    @Getter
    private final int methodIndex;
    @Getter
    private final MethodAccess methodAccess;
    @Getter
    @Setter
    private IPacket packet;

    public Action(Short module, Short pid, MethodAccess methodAccess, Object commander, int methodIndex,
                  Interceptor beforeInterceptor) {
        this.module = module;
        this.pid = pid;
        this.commander = commander;
        this.methodAccess = methodAccess;
        this.methodIndex = methodIndex;
        this.beforeInterceptor = beforeInterceptor;
    }
    public void invoke() {
        methodAccess.invoke(commander, methodIndex);
    }

    @Override
    public String toString() {
        return MessageFormat.format("class:{0} module:{1} key:{2}",this.commander.getClass().getSimpleName(),this.module,this.pid);
    }
}
