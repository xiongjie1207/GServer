package com.wegame.framework.grpc;

import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Program: ibox-sum
 * @Description: Etcd用观察者
 * @Author: ccm
 * @CreateTime: 2021-12-29 17:59
 */
@Data
public class EtcdObserver implements StreamObserver<LeaseKeepAliveResponse> {
    @Resource
    private EtcdUtil etcdUtil;

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
        // 服务器正常，但是在etcd掉线或崩溃的情况下，循环续约直至成功
        // 判断服务器是否正常关闭
        while (!NotifyChannel.SystemExit.isClose()) {
            boolean repeatFlag = etcdUtil.putAndKeepAlive(key, value, ttl);
            if (repeatFlag){
                break;
            }
            TimeUnit.SECONDS.sleep(5);
        }
    }

    @Override
    public void onCompleted() {
        logger.debug("服务器：{}，onCompleted", key);
    }
}
