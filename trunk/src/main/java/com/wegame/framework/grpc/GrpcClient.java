package com.wegame.framework.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
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
    private Discovery discovery;
    private final Map<String, ManagedChannel> channelMap = new HashMap<>();


    @Override
    public void addChannel(String key, String path) {
        ManagedChannel channel = channelMap.get(key);
        if (channel != null) {
            channel.shutdownNow();
        }
        channelMap.put(key, channel(path));
    }

    @Override
    public void removeAllChannel() {
        for (Map.Entry<String, ManagedChannel> entry : this.channelMap.entrySet()) {
            entry.getValue().shutdownNow();
        }
        this.channelMap.clear();
    }

    @Override
    public void removeChannel(String key) {
        ManagedChannel channel = channelMap.get(key);
        channel.shutdownNow();
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


    protected ManagedChannel channel(String host) {
        return ManagedChannelBuilder.forTarget(host).usePlaintext().build();
    }

    @PostConstruct
    private void init() {
        try {
            discovery = new Discovery(getZKAddress());
            discovery.watchService(remoteServiceName(), this);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    protected abstract String getZKAddress();

    protected abstract String remoteServiceName();
}
