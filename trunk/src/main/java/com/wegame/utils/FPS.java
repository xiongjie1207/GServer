package com.wegame.utils;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.math.NumberUtils;

/**
 * @Author xiongjie
 * @Date 2022/11/19 11:14
 **/
public class FPS {
    private float currentTime=0;
    private float duaration;
    private List<IFPSListener> listeners;

    /**
     *
     * @param duaration 刷新一帧的时间
     */
    public FPS(float duaration){
        if(duaration<=0){
            duaration=1000;
        }
        this.duaration = duaration;
        this.listeners = new ArrayList<>();
    }
    public void addListener(IFPSListener listener){
        this.listeners.add(listener);
    }

    public void update() {
        this.currentTime+= DeltaTime.getDeltaTimeMs();
        while (this.currentTime>=this.duaration){
            this.currentTime = this.currentTime-this.duaration;
            for (IFPSListener IFPSListener :this.listeners){
                IFPSListener.onUpdate();
            }
        }
    }
}
