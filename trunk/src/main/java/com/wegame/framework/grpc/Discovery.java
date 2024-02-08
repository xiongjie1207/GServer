package com.wegame.framework.grpc;

import com.wegame.util.ScheduledUtils;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Discovery implements Watch.Listener {
    private Client client;
    private final Object lock = new Object();
    private final Map<String, Channel> channelMap = new HashMap<>();
    private final Map<String, Watch.Watcher> watchMap = new HashMap<>();
    private final static AtomicInteger next = new AtomicInteger(0);

    public Discovery(String[] endpoints) {
        client = Client.builder().endpoints(endpoints).build();
    }

    public Channel select() throws Exception {
        for (Channel channel : this.channelMap.values()) {
            return channel;
        }
        return null;
    }

    private Channel channel(String host) {
        return ManagedChannelBuilder.forTarget(host).usePlaintext().build();
    }

    public void watchService(String key) {
        GetOption getOption = GetOption.newBuilder().withPrefix(ByteSequence.from(key, UTF_8)).build();
        //请求当前前缀
        CompletableFuture<GetResponse> getResponseCompletableFuture = client.getKVClient().get(ByteSequence.from(key, UTF_8), getOption);

        try {
            //获取当前前缀下的服务并存储
            List<KeyValue> kvs = getResponseCompletableFuture.get().getKvs();
            for (KeyValue kv : kvs) {
                addChannel(kv.getKey().toString(UTF_8), kv.getValue().toString(UTF_8));
            }
            ScheduledUtils.getInstance().executeTask(() -> watcher(ByteSequence.from(key, UTF_8)));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void watcher(ByteSequence key) {

        System.out.println("watching prefix:" + key);
        WatchOption watchOption = WatchOption.newBuilder().withPrefix(key).build();
        //实例化一个监听对象，当监听的key发生变化时会被调用
        Watch.Watcher watcher = client.getWatchClient().watch(key, watchOption, this);
        this.watchMap.put(key.toString(UTF_8), watcher);
    }

    private void addChannel(String key, String value) {
        synchronized (lock) {
            channelMap.put(key, channel(value));
        }
    }

    private void removeChannel(String key) {
        synchronized (lock) {
            channelMap.remove(key);
        }
    }

    public void close() {
        client.close();
        client = null;
    }

    @Override
    public void onNext(WatchResponse watchResponse) {
        for (WatchEvent watchEvent : watchResponse.getEvents()) {
            WatchEvent.EventType eventType = watchEvent.getEventType();
            KeyValue keyValue = watchEvent.getKeyValue();
            String key = keyValue.getKey().toString(UTF_8);
            switch (eventType) {
                case PUT -> {
                    addChannel(key, keyValue.getValue().toString(UTF_8));
                }
                case DELETE -> {
                    removeChannel(key);
                }
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {

    }
}
