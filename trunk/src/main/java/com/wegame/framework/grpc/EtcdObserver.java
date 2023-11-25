package com.wegame.framework.grpc;

import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.grpc.stub.StreamObserver;
import lombok.Data;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Program: wegame
 * @Description: Etcd用观察者
 * @Author: xiongjie.cn@gmail.com
 * @CreateTime: 2021-12-29 17:59
 */
@Data
public class EtcdObserver implements StreamObserver<LeaseKeepAliveResponse> {

    private static final Logger logger = LoggerFactory.getLogger(EtcdObserver.class);

    /**
     * 续约Key
     */
    private String key;

    /**
     * 续约value
     */
    private String value;

    /**
     * 续约时长
     */
    private long ttl;

    public EtcdObserver(String key, String value, long ttl){
        this.key = key;
        this.value = value;
        this.ttl = ttl;
    }

    @Override
    public void onNext(LeaseKeepAliveResponse value) {
        logger.debug("服务器：{}，etcd续约完成，时长：{}s", key, ttl);
    }

    @SneakyThrows
    @Override
    public void onError(Throwable t) {
        logger.error("服务器：{}，etcd续约出错，错误内容：{}", key, t.getMessage());
    }

    @Override
    public void onCompleted() {
        logger.debug("服务器：{}，onCompleted", key);
    }
}
