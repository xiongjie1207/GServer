package com.wegame.framework.grpc;

/**
 * @Author xiongjie
 * @Date 2024/02/10 01:53
 **/
public interface WatchListener {
    void add(String key, String path);

    void remove(String key);
}
