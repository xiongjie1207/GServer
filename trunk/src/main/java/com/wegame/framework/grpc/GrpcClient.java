package com.wegame.framework.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author xiongjie
 * @Date 2024/02/08 20:40
 **/
@Slf4j
public abstract class GrpcClient implements WatchListener {
    private Discovery serviceConsumer;
    private final Map<String, Channel> channelMap = new HashMap<>();


    @Override
    public void add(String key, String path) {
        channelMap.put(key, channel(path));
    }

    @Override
    public void remove(String key) {
        channelMap.remove(key);
    }

    public void removeChannel(String key) {
        log.warn("服务掉线:{}", key);
        channelMap.remove(key);
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
        try {
            serviceConsumer = new Discovery(getZKAddress());
            serviceConsumer.watchService(remoteServiceName(), this);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    protected abstract String getZKAddress();

    protected abstract String remoteServiceName();
}
