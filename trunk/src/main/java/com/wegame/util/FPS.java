package com.wegame.util;


import java.util.ArrayList;
import java.util.List;

/**
 * @Author xiongjie
 * @Date 2022/11/19 11:14
 **/
public class FPS {
    private float currentTime=0;
    private float interval;
    private List<IFPSListener> listeners;

    /**
     *
     * @param interval 每间隔interval秒刷新一次
     */
    public FPS(float interval){
        if(interval<=0){
            interval=1000;
        }
        this.interval = interval;
        this.listeners = new ArrayList<>();
    }
    public void addListener(IFPSListener listener){
        this.listeners.add(listener);
    }

    public void update() {
        this.currentTime+= DeltaTime.getDeltaTimeMs();
        while (this.currentTime>=this.interval){
            this.currentTime = this.currentTime-this.interval;
            for (IFPSListener IFPSListener :this.listeners){
                IFPSListener.onUpdate();
            }
        }
    }
}
