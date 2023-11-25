package com.wegame.framework.grpc;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Program: wegame
 * @Description: 通知
 * @Author: xiongjie.cn@gmail.com
 * @CreateTime: 2021-11-19 22:03
 */
public class NotifyChannel {
    private AtomicInteger cas = new AtomicInteger();

    public static NotifyChannel SystemExit = new NotifyChannel();

    public NotifyChannel() {
        cas.set(1);
    }

    public boolean isClose() {
        return cas.get() == 0;
    }

    public void close() {
        cas.set(0);
    }
}
