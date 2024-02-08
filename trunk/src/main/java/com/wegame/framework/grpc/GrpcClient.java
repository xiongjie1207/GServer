package com.wegame.framework.grpc;

import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @Author xiongjie
 * @Date 2024/02/08 20:40
 **/
@Slf4j
public abstract class GrpcClient implements WatchListener {
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
        channelMap.put(key, channel(value));
    }

    public void removeChannel(KeyValue keyValue) {
        String key = keyValue.getKey().toString(UTF_8);
        log.warn("服务掉线:{}", key);
        channelMap.remove(key);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {

    }

    protected Channel select() throws Exception {
        if (this.channelMap.isEmpty()) {
            throw new Exception("channels size is zero");
        }
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

    @PreDestroy
    private void destroy() {
        discovery.close();
    }

    protected abstract String[] getEtcdAddress();

    protected abstract String remoteServiceName();
}
