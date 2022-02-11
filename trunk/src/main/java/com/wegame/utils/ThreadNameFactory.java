package com.wegame.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

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
public class ThreadNameFactory implements ThreadFactory{
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(0);
    private final String namePrefix;
    private final boolean daemon;

    public ThreadNameFactory(String namePreFix)
    {
       this(namePreFix, false);
    }

    public ThreadNameFactory(String namePreFix, boolean daemon)
    {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
                .getThreadGroup();
        this.namePrefix = namePreFix + "-thread-";
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r)
    {
        Thread t = new Thread(group, r, namePrefix
                + threadNumber.getAndIncrement(), 0);
        if(daemon) {
           t.setDaemon(daemon);
        }else{
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
        }
        return t;
    }

    public String getNamePrefix() {
        return namePrefix;
    }
}
