package com.wegame.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author xiongjie
 * @Date 2022/11/18 18:59
 **/
public class ScheduledUtil {
    private List<ScheduledExecutorService> scheduledThreadPool;
    private ExecutorService scheduledExecutorService;
    private static ScheduledUtil instance;
    public static ScheduledUtil getInstance(){
        if(instance==null){
            instance=new ScheduledUtil();
        }
        return instance;
    }
    private ScheduledUtil(){
        scheduledThreadPool = new ArrayList<>();
        scheduledExecutorService = Executors.newFixedThreadPool(4);
    }
    public void submitTask(Runnable runnable, int initialDelay, int delay){
        ThreadNameFactory threadNameFactory = new ThreadNameFactory("commandGroup_"+this.scheduledThreadPool.size());
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(threadNameFactory);
        this.scheduledThreadPool.add(scheduledExecutorService);
        scheduledExecutorService.scheduleAtFixedRate(runnable, initialDelay, delay,
            TimeUnit.MILLISECONDS);
    }
    public void submitTask(Runnable runnable){
        scheduledExecutorService.submit(runnable);
    }
    public void shutdown(){
        for (ScheduledExecutorService scheduledExecutorService:this.scheduledThreadPool){
            scheduledExecutorService.shutdown();
        }
        this.scheduledExecutorService.shutdown();
    }
}
