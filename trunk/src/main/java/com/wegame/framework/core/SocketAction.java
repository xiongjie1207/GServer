package com.wegame.framework.core;

import com.wegame.framework.session.ISession;

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
 * Created by xiongjie on 2016/12/23.
 */
public class SocketAction<T> extends Action {
    private ISession<T> session;

    public SocketAction(Action action) {
        super(action.getModule(), action.getPid(), action.getMethodAccess(), action.getCommander(),
                action.getMethodIndex(), action.getBeforeInterceptor());
    }

    public ISession<T> getSession() {
        return session;
    }

    public void setSession(ISession<T> session) {
        this.session = session;
    }
}