package com.wegame.utils;

/**
 * @Author xiongjie
 * @Date 2022/11/16 18:05
 **/
public class DeltaTime {
    private static long prevTimeMs=System.currentTimeMillis();;
    private static long deltaTime;

    public static void update() {
        long curMs = System.currentTimeMillis();
        deltaTime = curMs - prevTimeMs;
        prevTimeMs = curMs;
    }
    public static long getDeltaTimeMs(){
        return deltaTime;
    }
    public static float getDeltaTimeSec()
    {
        float time = getDeltaTimeMs();
        return time*0.001f;
    }
}
