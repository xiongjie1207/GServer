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

import com.gserver.aop.Invocation;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Commanders {
    private boolean isRunning = true;
    private Logger logger = Logger.getLogger(this.getClass());
    private static Commanders instance = new Commanders();

    public static Commanders getInstance() {
        return instance;
    }

    private Commanders() {
        this.setRunning(true);

    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void dispatch(Packet packet, Channel channel) {
        try {
            if (isRunning) {
                Action action = ActionMapping.getInstance().getAction(packet.getProtocoleId());
                if (action != null) {
                    SocketAction socketAction = new SocketAction(action.getActionKey(),action.getCommander(),action.getMethod(),action.getBeforeInterceptors());
                    socketAction.setPacket(packet);
                    socketAction.setChannel(channel);
                    ServerContext.getContext().setAction(socketAction);
                    new Invocation(socketAction).invoke();
                    ServerContext.getContext().reset();
                }else {
                    logger.warn("No mapping found for protocol:"+packet.getProtocoleId());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("exception", e);
        }


    }

    public void dispatch(Packet packet, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (isRunning) {
                Action action = ActionMapping.getInstance().getAction(packet.getProtocoleId());
                if (action != null) {
                    HttpAction httpAction = new HttpAction(action.getActionKey(),action.getCommander(),action.getMethod(),action.getBeforeInterceptors());
                    httpAction.setPacket(packet);
                    httpAction.setRequest(request);
                    httpAction.setResponse(response);
                    ServerContext.getContext().setAction(httpAction);
                    new Invocation(httpAction).invoke();
                    ServerContext.getContext().reset();
                }else{
                    logger.warn("No mapping found for protocol:"+packet.getProtocoleId());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("exception", e);
        }
    }
}
