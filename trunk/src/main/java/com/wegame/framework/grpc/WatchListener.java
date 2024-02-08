package com.wegame.framework.grpc;

import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;

/**
 * @Author xiongjie
 * @Date 2024/02/08 20:43
 **/
public interface WatchListener extends Watch.Listener {
    void addChannel(KeyValue keyValue);

    void removeChannel(KeyValue keyValue);
}
