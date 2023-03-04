package com.wegame.framework.commands;
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

import com.wegame.framework.core.Action;
import com.wegame.framework.core.HttpAction;
import com.wegame.framework.core.ServerContext;
import com.wegame.framework.core.SocketAction;
import com.wegame.framework.session.ISession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class Commander {

    private <T extends Action> T getAction() {
        return ServerContext.getContext().getAction();
    }

    protected byte[] getData() {
        return getAction().getPacket().getData();
    }

    public HttpServletResponse getHttpServletResponse() {
        return this.getHttpAction().getResponse();
    }

    protected HttpServletRequest getHttpServletRequest() {
        return this.getHttpAction().getRequest();
    }

    protected HttpAction getHttpAction() {
        return this.getAction();
    }

    protected SocketAction getSocketAction() {
        return this.getAction();
    }

    protected HttpSession getHttpSession() {
        return getHttpAction().getRequest().getSession();
    }

    protected ISession getSocketSession() {
        return getSocketAction().getSession();
    }
}
