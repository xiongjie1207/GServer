package com.gserver.plugins.db.asyn;
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

import com.gserver.plugins.db.core.BaseDAL;
import com.gserver.plugins.db.descriptor.IEntity;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;


public class AsynSQLTask implements Runnable {
    private Logger logger = Logger.getLogger(this.getClass());

    /**
     * 日志队列
     */
    private BlockingQueue<AsynRecord> asynQueue;

    /**
     * 开关
     */
    private volatile boolean activeFlag = true;

    private List<AsynRecord> records = new ArrayList<AsynRecord>();


    private BaseDAL baseDAL;

    public AsynSQLTask(BaseDAL baseDAL) {
        this.baseDAL = baseDAL;
    }

    @Override
    public void run() {

        while (activeFlag) {
            try {
                AsynRecord r = asynQueue.take();
                switch (r.getMethod()) {
                    case INSERT:
                        if (r.getObj() instanceof IEntity) {
                            baseDAL.insert((IEntity) r.getObj());
                        } else if (r.getObj() instanceof Map) {
                            baseDAL.insert(r.getTable(), (Map<String, Object>) r.getObj());
                        }
                        break;
                    case INSERT_TABLE:
                        baseDAL.insert(r.getTable(), r.getMapObj());
                        break;
                }
            } catch (Exception e) {
                logger.error("【严重】日志任务失败!", e);
            }
        }
    }


    public void setLogQueue(BlockingQueue<AsynRecord> asynQueue) {
        this.asynQueue = asynQueue;
    }

    public boolean isActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }


}
