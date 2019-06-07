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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class CommanderGroup implements Runnable {
    private boolean isRunning = true;
    private Logger logger = Logger.getLogger(this.getClass());
    private static CommanderGroup instance = new CommanderGroup();
    private BlockingQueue<Action> actions;

    public static CommanderGroup getInstance() {
        return instance;
    }

    private CommanderGroup() {
        this.setRunning(true);
        actions = new LinkedBlockingQueue<>();
        Executors.newSingleThreadExecutor().execute(this);
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void dispatch(Packet packet, Channel channel) {
        try {
            if (isRunning) {
                Action action = ActionMapping.getInstance().getAction(packet.getProtocoleId());
                if (action != null) {
                    SocketAction socketAction = new SocketAction(action.getActionKey(), action.getCommander(), action.getMethod(), action.getBeforeInterceptors());
                    socketAction.setPacket(packet);
                    socketAction.setChannel(channel);
                    actions.add(socketAction);
                } else {
                    logger.warn("No mapping found for protocol:" + packet.getProtocoleId());
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
                    HttpAction httpAction = new HttpAction(action.getActionKey(), action.getCommander(), action.getMethod(), action.getBeforeInterceptors());
                    httpAction.setPacket(packet);
                    httpAction.setRequest(request);
                    httpAction.setResponse(response);
                    executeCommand(httpAction);
                } else {
                    logger.warn("No mapping found for protocol:" + packet.getProtocoleId());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("exception", e);
        }
    }

    private void executeCommand(Action action) {
        try {
            ServerContext.getContext().setAction(action);
            new Invocation(action).invoke();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("exception", e);
        } finally {
            ServerContext.getContext().reset();
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (isRunning) {
            try {
                Action action = actions.take();
                this.executeCommand(action);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                logger.error("run:", e);
            }
        }
    }
}
