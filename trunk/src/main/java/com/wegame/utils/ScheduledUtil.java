package com.wegame.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author xiongjie
 * @Date 2022/11/18 18:59
 **/
public class ScheduledUtil {
    private ExecutorService scheduledExecutorService;
    private ScheduledExecutorService singleScheduledExecutorService;
    private static ScheduledUtil instance;
    public static ScheduledUtil getInstance(){
        if(instance==null){
            instance=new ScheduledUtil();
        }
        return instance;
    }
    private ScheduledUtil(){
        scheduledExecutorService = Executors.newFixedThreadPool(4);
    }
    public void newSingleThreadExecutor(Runnable runnable, int initialDelay, int delay){
        if(this.singleScheduledExecutorService!=null){
            return;
        }
        ThreadNameFactory threadNameFactory = new ThreadNameFactory("singleScheduledExecutorService");
        this.singleScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(threadNameFactory);
        this.singleScheduledExecutorService.scheduleAtFixedRate(runnable, initialDelay, delay,
            TimeUnit.MILLISECONDS);
    }
    public void submitTask(Runnable runnable){
        scheduledExecutorService.submit(runnable);
    }
    public void shutdown(){
        this.singleScheduledExecutorService.shutdown();
        this.singleScheduledExecutorService = null;
        this.scheduledExecutorService.shutdown();
    }
}
