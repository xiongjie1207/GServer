package com.wegame.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * sessionId生成器
 */
public class LongIdGenerator {
    private static final LongIdGenerator instance = new LongIdGenerator();
    private final AtomicLong id_gen;

    private LongIdGenerator() {
        id_gen = new AtomicLong(0);
    }

    public static LongIdGenerator getInstance() {
        return instance;
    }

    public long generateId() {
        return id_gen.incrementAndGet();
    }
}