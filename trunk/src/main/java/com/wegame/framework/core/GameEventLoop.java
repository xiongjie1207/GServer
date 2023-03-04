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

import com.wegame.framework.aop.Invocation;
import com.wegame.framework.packet.IPacket;
import com.wegame.framework.session.ISession;
import com.wegame.utils.AppStatus;
import com.wegame.utils.DeltaTime;
import com.wegame.utils.ScheduledUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameEventLoop implements Runnable {
    private static final GameEventLoop instance = new GameEventLoop();
    private final List<Action> actions;
    private final List<IUpdater> updaterList;

    protected GameEventLoop() {
        actions = new ArrayList<>();
        updaterList = new ArrayList<>();

    }

    public static GameEventLoop getInstance() {
        return instance;
    }

    public void start(int initialDelay, int delay) {
        ScheduledUtil.getInstance().newSingleThreadExecutor(this,initialDelay,delay);
    }

    public void stop() {
        ScheduledUtil.getInstance().shutdown();
    }

    public void addUpdater(IUpdater updater) {
        this.updaterList.add(updater);
    }

    public void removeUpdater(IUpdater updater) {
        this.updaterList.remove(updater);
    }

    public void clearUpdater() {
        this.updaterList.clear();
    }

    public void dispatch(IPacket packet, ISession session) {
        try {
            Action action = ActionMapping.getInstance().getAction(packet.getModule(),packet.getPid());
            if (action != null) {
                SocketAction socketAction = new SocketAction(action);
                socketAction.setPacket(packet);
                socketAction.setSession(session);
                synchronized (this.actions) {
                    actions.add(socketAction);
                }
            } else {
                log.warn("No mapping found for socket protocol:"+packet.getModule()+"_"+ packet.getPid());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("exception", e);
        }


    }

    public void dispatch(IPacket packet, HttpServletRequest request, HttpServletResponse response) {
        try {
            Action action = ActionMapping.getInstance().getAction(packet.getModule(),packet.getPid());
            if (action != null) {
                HttpAction httpAction = new HttpAction(action);
                httpAction.setPacket(packet);
                httpAction.setRequest(request);
                httpAction.setResponse(response);
                executeCommand(httpAction);
            } else {
                log
                    .warn("No mapping found for http protocol:"+packet.getModule()+"_"+ packet.getPid());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("exception", e);
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
            log.error("exception", e);
        } finally {
            ServerContext.getContext().reset();
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            DeltaTime.update();
            List<Action> tempAction;
            synchronized (this.actions) {
                tempAction = new ArrayList<>(this.actions.size());
                tempAction.addAll(this.actions);
                this.actions.clear();
            }
            for (Action action : tempAction) {
                this.executeCommand(action);
            }
            for (IUpdater update : this.updaterList) {
                update.update();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("CommanderGroup.run:", e);
        }
    }
}
