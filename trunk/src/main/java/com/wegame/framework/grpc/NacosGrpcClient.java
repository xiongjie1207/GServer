package com.wegame.framework.grpc;

import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author xiongjie
 * @Date 2024/02/08 20:40
 **/
@Slf4j
public abstract class NacosGrpcClient implements WatchListener, EventListener {
    private final Map<String, ManagedChannel> channelMap = new HashMap<>();
    protected Map<String, Object> config;
    private NamingService namingService;

    protected NacosGrpcClient() {
    }

    @Override
    public void addChannel(String key, String path) {
        ManagedChannel channel = this.channelMap.get(key);
        if (channel != null) {
            channel.shutdown();
            this.channelMap.remove(key);
        }
        this.channelMap.put(key, channel(path));
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
        if (config != null) {
            return ManagedChannelBuilder.forTarget(host).defaultServiceConfig(config).usePlaintext().build();
        } else {
            return ManagedChannelBuilder.forTarget(host).defaultServiceConfig(config).usePlaintext().build();
        }

    }

//    @PostConstruct
    private void init() {
        try {
            namingService = this.namingService();
            namingService.subscribe(remoteServiceName(), this);
            List<Instance> list = namingService.getAllInstances(remoteServiceName());
            processIntance(list);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private String getKey(Instance instance) {
        return String.format("/%s/%s:%d", remoteServiceName(), instance.getIp(), instance.getPort());
    }

    private void processIntance(List<Instance> list) {
        for (Instance instance : list) {
            if (instance.isHealthy()) {
                // 实例状态为上线
                String key = getKey(instance);
                this.addChannel(key, String.format("%s:%d", instance.getIp(), instance.getPort()));
            } else {
                // 实例状态为下线
                String key = getKey(instance);
                this.removeChannel(key);
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof NamingEvent namingEvent) {
            processIntance(namingEvent.getInstances());
        }
    }


    protected abstract String remoteServiceName();

    protected abstract NamingService namingService();
}
