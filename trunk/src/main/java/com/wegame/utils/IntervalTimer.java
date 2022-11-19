package com.wegame.utils;


import java.util.ArrayList;
import java.util.List;
import lombok.Setter;

/**
 * @Author xiongjie
 * @Date 2022/11/19 11:14
 **/
public class IntervalTimer {
    private float currentTime=0;
    @Setter
    private float intervalTime;
    private List<IIntervalListener> listeners;
    public IntervalTimer(float intervalTime){
        this.listeners = new ArrayList<>();
        this.intervalTime = intervalTime;
    }
    public void addListener(IIntervalListener listener){
        this.listeners.add(listener);
    }

    public void update() {
        this.currentTime+= DeltaTime.getDeltaTimeMs();
        while (this.currentTime>=this.intervalTime){
            this.currentTime = this.currentTime-this.intervalTime;
            for (IIntervalListener IIntervalListener :this.listeners){
                IIntervalListener.interval();
            }
        }
    }
}
