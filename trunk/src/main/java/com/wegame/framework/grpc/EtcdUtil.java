package com.wegame.framework.grpc;

import com.sailurcloud.common.config.EtcdConfig;
import io.etcd.jetcd.*;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * @Program: grpc-demo
 * @Description: ETCD工具类
 * @Author: ccm
 * @CreateTime: 2021-11-11 13:47
 */
@Component
public class EtcdUtil implements Consumer<WatchResponse> {


    private KV kvClient;

    private Watch watchClient;

    private Lease leaseClient;


    private final List<String> keys = new ArrayList<>();
    @Resource
    private EtcdConfig etcdConfig;
    private WatchListener watchListener;

    @PostConstruct
    private void init() throws Exception {
        String[] etcd_address = etcdConfig.getEtcdAddress();
        Collection<URI> uris = new ArrayList<>();
        for (String etcdAddress : etcd_address) {
            uris.add(URI.create(etcdAddress));
        }
        Client etcdClient = Client.builder().endpoints(uris).build();
        kvClient = etcdClient.getKVClient();
        watchClient = etcdClient.getWatchClient();
        leaseClient = etcdClient.getLeaseClient();

    }

    @PreDestroy
    private void destroy() {
        leaseClient.close();
        keys.forEach(key -> kvClient.delete(ByteSequence.from(key.getBytes())));

    }


    /**
     * 存放键值对，并设置无限续约
     *
     * @param key
     * @param value
     * @param ttl
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public boolean putAndKeepAlive(String key, String value, long ttl) throws ExecutionException, InterruptedException, TimeoutException {
        keys.add(key);
        LeaseGrantResponse leaseGrantResponse = leaseClient.grant(ttl).get(3, TimeUnit.SECONDS);
        long leaseId = leaseGrantResponse.getID();
        if (leaseId <= 0) {
            return false;
        }
        // 存放键值对
        PutOption putOption = PutOption.newBuilder().withLeaseId(leaseId).build();
        kvClient.put(ByteSequence.from(key.getBytes()), ByteSequence.from(value.getBytes()), putOption);
        // 自动续约租期
        EtcdObserver etcdObserver = new EtcdObserver(key, value, ttl);
        leaseClient.keepAlive(leaseId, etcdObserver);
        return true;
    }


    /**
     * 监听指定前缀密钥
     *
     * @param prefix 密钥前缀
     */
    public void watchPrefix(String prefix, WatchListener watchListener) throws ExecutionException, InterruptedException {
        WatchOption watchOption = WatchOption.newBuilder().isPrefix(true).build();
        Watch.Listener listener = Watch.listener(this);
        watchClient.watch(ByteSequence.from(prefix.getBytes()), watchOption, listener);
        this.watchListener = watchListener;
        GetOption build = GetOption.newBuilder().isPrefix(true).build();
        List<KeyValue> keyValueList = kvClient.get(ByteSequence.from(prefix.getBytes()), build).get().getKvs();
        keyValueList.forEach(keyValue -> this.watchListener.Put(keyValue.getKey().toString(StandardCharsets.UTF_8), keyValue.getValue().toString(StandardCharsets.UTF_8)));
    }


    @Override
    public void accept(WatchResponse watchResponse) {
        watchResponse.getEvents().forEach(watchEvent -> {
            // 获取事件类型
            WatchEvent.EventType eventType = watchEvent.getEventType();
            // 获取触发事件的键值对
            KeyValue keyValue = watchEvent.getKeyValue();
            String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
            String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
            // 根据事件执行不同的操作
            if (WatchEvent.EventType.PUT.equals(eventType)) {
                this.watchListener.Put(key, value);
            } else if (WatchEvent.EventType.DELETE.equals(eventType)) {
                this.watchListener.Del(key, value);
            }
        });
    }
}
