package com.wegame.framework.grpc;

import com.wegame.util.ScheduledUtils;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.WatchOption;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Discovery {
    private Client client;


    private final Map<String, Watch.Watcher> watchMap = new HashMap<>();


    public Discovery(String[] endpoints) {
        client = Client.builder().endpoints(endpoints).build();
    }


    public void watchService(String key, WatchListener listener) {
        GetOption getOption = GetOption.newBuilder().isPrefix(true).build();
        //请求当前前缀
        CompletableFuture<GetResponse> getResponseCompletableFuture = client.getKVClient().get(ByteSequence.from(key, UTF_8), getOption);

        try {
            //获取当前前缀下的服务并存储
            List<KeyValue> kvs = getResponseCompletableFuture.get().getKvs();
            for (KeyValue kv : kvs) {
                listener.addChannel(kv);
            }
            ScheduledUtils.getInstance().executeTask(() -> {
                WatchOption watchOption = WatchOption.newBuilder().isPrefix(true).build();
                //实例化一个监听对象，当监听的key发生变化时会被调用
                Watch.Watcher watcher = client.getWatchClient().watch(ByteSequence.from(key, UTF_8), watchOption, listener);
                this.watchMap.put(key, watcher);
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        client.close();
        client = null;
    }

}
