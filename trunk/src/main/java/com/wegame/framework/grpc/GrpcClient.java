package com.wegame.framework.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GrpcClient implements WatchListener {
    private final ArrayList<Channel> channels = new ArrayList<>();
    private final HashMap<String, Channel> list = new HashMap<>();

    private final static AtomicInteger next = new AtomicInteger(0);

    protected Channel select() throws Exception {
        synchronized (channels) {
            if(channels.isEmpty()) {
                throw new Exception("channels size is zero");
            } else {
                return channels.get(next.getAndIncrement() % channels.size());
            }
        }
    }

    @Override
    public void Put(String Key, String host) {
        synchronized (channels) {
            Channel channel = channel(host);
            list.put(host, channel);
            channels.add(channel);
        }
        System.out.println("GRPC PUT KEY:"+Key);
    }

    @Override
    public void Del(String Key, String Value) {
        synchronized (channels) {
            if (list.containsKey(Value)) {
                Channel channel = list.get(Value);
                list.remove(Value);
                channels.remove(channel);
            }
        }
        System.out.println("GRPC DEL KEY:"+Key);
    }

    protected Channel channel(String host){
        return ManagedChannelBuilder.forTarget(host)
                .usePlaintext()
                .build();
    }
}
