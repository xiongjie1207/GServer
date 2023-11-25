package com.wegame.framework.grpc;

public interface WatchListener {
    void Put(String Key, String Value);

    void Del(String Key, String Value);
}
