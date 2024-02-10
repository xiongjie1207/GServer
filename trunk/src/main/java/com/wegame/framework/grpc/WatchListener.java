package com.wegame.framework.grpc;

/**
 * @Author xiongjie
 * @Date 2024/02/10 01:53
 **/
public interface WatchListener {
    void addChannel(String key, String path);

    void removeAllChannel();

    void removeChannel(String key);
}
