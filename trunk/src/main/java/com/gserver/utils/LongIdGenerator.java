package com.gserver.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * sessionId生成器
 */
public class LongIdGenerator {
    private static LongIdGenerator instance = new LongIdGenerator();
    private AtomicLong id_gen;

    public static LongIdGenerator getInstance() {
        return instance;
    }

    private LongIdGenerator() {
        id_gen = new AtomicLong(0);
    }


    public long generateId() {
        return id_gen.incrementAndGet();
    }
}