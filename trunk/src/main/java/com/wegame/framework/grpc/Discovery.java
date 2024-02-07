package com.wegame.framework.grpc;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Discovery {
    private Client client;
    private final Object lock = new Object();
    private final List<GrpcChannel> list = new ArrayList<>();
    private final static AtomicInteger next = new AtomicInteger(0);

    public Discovery(String[] endpoints) {
        client = Client.builder().endpoints(endpoints).build();
    }

    public Channel select() throws Exception {
        if (list.isEmpty()) {
            throw new Exception("channels size is zero");
        } else {
            return list.get(next.getAndIncrement() % list.size()).getChannel();
        }
    }

    private Channel channel(String host) {
        return ManagedChannelBuilder.forTarget(host).usePlaintext().build();
    }

    public void watchService(String prefixAddress) {
        GetOption getOption = GetOption.newBuilder().withPrefix(ByteSequence.from(prefixAddress, UTF_8)).build();
        //请求当前前缀
        CompletableFuture<GetResponse> getResponseCompletableFuture = client.getKVClient().get(ByteSequence.from(prefixAddress, UTF_8), getOption);

        try {
            //获取当前前缀下的服务并存储
            List<KeyValue> kvs = getResponseCompletableFuture.get().getKvs();
            for (KeyValue kv : kvs) {
                addChannel(kv.getKey().toString(UTF_8), kv.getValue().toString(UTF_8));
            }
            //创建线程监听前缀
            new Thread(() -> watcher(prefixAddress)).start();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void watcher(String prefixAddress) {

        System.out.println("watching prefix:" + prefixAddress);
        WatchOption watchOpts = WatchOption.newBuilder().withPrefix(ByteSequence.from(prefixAddress, UTF_8)).build();

        //实例化一个监听对象，当监听的key发生变化时会被调用
        Watch.Listener listener = Watch.listener(watchResponse -> watchResponse.getEvents().forEach(watchEvent -> {
            WatchEvent.EventType eventType = watchEvent.getEventType();
            KeyValue keyValue = watchEvent.getKeyValue();
            switch (eventType) {
                case PUT -> addChannel(keyValue.getKey().toString(UTF_8), keyValue.getValue().toString(UTF_8));
                case DELETE -> removeChannel(keyValue.getKey().toString(UTF_8));
            }
        }));

        client.getWatchClient().watch(ByteSequence.from(prefixAddress, UTF_8), watchOpts, listener);
    }

    private void addChannel(String key, String value) {
        synchronized (lock) {
            list.add(new GrpcChannel(key, channel(value)));
        }
    }

    private void removeChannel(String key) {
        synchronized (lock) {
            int size = this.list.size();
            for (int i = 0; i < size; i++) {
                GrpcChannel channel = this.list.get(i);
                if (Objects.equals(channel.getKey(), key)) {
                    list.remove(i);
                    return;
                }
            }
        }
    }

    public void close() {
        client.close();
        client = null;
    }
}
