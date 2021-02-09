package com.gserver.components.session;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by jwp on 2017/2/10.
 * session提供服务
 */
public class SessionCounter {

    private static SessionCounter instance = new SessionCounter();

    private AtomicLong atomicLimitNumber;

    public static SessionCounter getInstance() {
        return instance;
    }

    private SessionCounter() {
        atomicLimitNumber = new AtomicLong(0);
    }

    public long incrementAndGet() {
        return atomicLimitNumber.incrementAndGet();
    }


    public long decrementAndGet() {
        return atomicLimitNumber.decrementAndGet();
    }


    public void reset() throws Exception {
        atomicLimitNumber.set(0);
    }

}
