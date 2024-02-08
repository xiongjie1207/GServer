package com.wegame.framework.grpc;

import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @Author xiongjie
 * @Date 2024/02/08 20:40
 **/
public abstract class GrpcClient implements WatchListener {
    private final Object lock = new Object();
    private Discovery discovery;
    private final Map<String, Channel> channelMap = new HashMap<>();

    @Override
    public void onNext(WatchResponse watchResponse) {
        for (WatchEvent watchEvent : watchResponse.getEvents()) {
            WatchEvent.EventType eventType = watchEvent.getEventType();
            KeyValue keyValue = watchEvent.getKeyValue();
            switch (eventType) {
                case PUT -> addChannel(keyValue);
                case DELETE -> removeChannel(keyValue);
            }
        }
    }

    public void addChannel(KeyValue keyValue) {
        String key = keyValue.getKey().toString(UTF_8);
        String value = keyValue.getValue().toString(UTF_8);
        synchronized (lock) {
            channelMap.put(key, channel(value));
        }
    }

    public void removeChannel(KeyValue keyValue) {
        String key = keyValue.getKey().toString(UTF_8);
        String value = keyValue.getValue().toString(UTF_8);
        synchronized (lock) {
            channelMap.remove(key);
        }
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {

    }

    protected Channel select() throws Exception {
        for (Channel channel : this.channelMap.values()) {
            return channel;
        }
        return null;
    }


    protected Channel channel(String host) {
        return ManagedChannelBuilder.forTarget(host).usePlaintext().build();
    }

    @PostConstruct
    private void init() {
        discovery = new Discovery(getEtcdAddress());
        discovery.watchService(remoteServiceName(), this);
    }

    protected abstract String[] getEtcdAddress();

    protected abstract String remoteServiceName();
}
