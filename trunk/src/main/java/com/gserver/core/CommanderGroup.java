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
import com.gserver.components.net.packet.IPacket;
import com.gserver.components.session.ISession;
import com.gserver.utils.AppStatus;
import com.gserver.utils.Loggers;
import com.gserver.utils.ThreadNameFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class CommanderGroup implements Runnable {
    private static CommanderGroup instance = new CommanderGroup();
    private BlockingQueue<Action> actions;
    private ScheduledExecutorService scheduledThreadPool;

    public static CommanderGroup getInstance() {
        return instance;
    }

    private CommanderGroup() {
        actions = new LinkedBlockingQueue<>();
        ThreadNameFactory threadNameFactory = new ThreadNameFactory("commandGroup");
        scheduledThreadPool = Executors.newSingleThreadScheduledExecutor(threadNameFactory);
        scheduledThreadPool.scheduleWithFixedDelay(this,0,1,TimeUnit.MILLISECONDS);

    }


    public void dispatch(IPacket packet, ISession session) {
        try {
            Action action = ActionMapping.getInstance().getAction(packet.getPid());
            if (action != null) {
                SocketAction socketAction = new SocketAction(action.getActionKey(), action.getCommander(), action.getMethod(), action.getBeforeInterceptor());
                socketAction.setPacket(packet);
                socketAction.setSession(session);
                actions.add(socketAction);
            } else {
                Loggers.GameLogger.warn("No mapping found for protocol:" + packet.getPid());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Loggers.ErrorLogger.error("exception", e);
        }


    }

    public void dispatch(IPacket packet, HttpServletRequest request, HttpServletResponse response) {
        try {
            Action action = ActionMapping.getInstance().getAction(packet.getPid());
            if (action != null) {
                HttpAction httpAction = new HttpAction(action.getActionKey(), action.getCommander(), action.getMethod(), action.getBeforeInterceptor());
                httpAction.setPacket(packet);
                httpAction.setRequest(request);
                httpAction.setResponse(response);
                executeCommand(httpAction);
            } else {
                Loggers.GameLogger.warn("No mapping found for protocol:" + packet.getPid());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Loggers.ErrorLogger.error("exception", e);
        }
    }

    private void executeCommand(Action action) {
        try {
            if (AppStatus.Status == AppStatus.Running) {
                ServerContext.getContext().setAction(action);
                new Invocation(action).invoke();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Loggers.ErrorLogger.error("exception", e);
        } finally {
            ServerContext.getContext().reset();
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            Action action = actions.take();
            this.executeCommand(action);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Loggers.ErrorLogger.error("CommanderGroup.run:", e);
        }
    }
}
