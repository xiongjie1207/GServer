package com.wegame.framework.core;

public final class ServerInfo {

    private ServerInfo() {
    }

    public static long getUsedMemoryMB() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 /
            1024;
    }

    public static long getFreeMemoryMB() {
        return (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() +
            Runtime.getRuntime().freeMemory()) / 1048576;
    }

    public static long getTotalMemoryMB() {
        return Runtime.getRuntime().maxMemory() / 1048576;
    }

    public static String memoryInfo() {
        long freeMem = getFreeMemoryMB();
        long totalMem = getTotalMemoryMB();
        return "Free memory " + freeMem + " Mb of " + totalMem + " Mb";
    }
}
