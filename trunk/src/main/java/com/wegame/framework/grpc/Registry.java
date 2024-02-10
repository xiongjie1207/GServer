package com.wegame.framework.grpc;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * @author fanqiechaodan
 * @Classname ServiceRegistry
 * @Description 服务注册
 */
@Slf4j
public class Registry {
    private final int connectionTimeoutMs = 20000;
    private static final int SESSION_TIMEOUT = 20000;
    private final int maxRetries = 10;
    private final CuratorFramework curator;
    private final int sleepMsBetweenRetry = 20000;
    private String serviceName;

    public Registry(String zooKeeperAddress) throws IOException {
        this.curator = CuratorFrameworkFactory.builder()
                //连接地址  集群用,隔开
                .connectString(zooKeeperAddress)
                .connectionTimeoutMs(connectionTimeoutMs)
                //会话超时时间
                .sessionTimeoutMs(SESSION_TIMEOUT)
                //设置重试机制
                .retryPolicy(new ExponentialBackoffRetry(sleepMsBetweenRetry, maxRetries))
                .build();
        this.curator.start();
    }

    /**
     * 注册服务
     *
     * @param serviceName
     * @param serviceAddress
     */
    @SneakyThrows
    public void registryService(String serviceName, String serviceAddress) {
        this.serviceName = serviceName;
        // 如果根节点不存在就创建
        String nodePath = serviceName + "/" + serviceAddress;
        Stat state = curator.checkExists().forPath(nodePath);
        if (state != null) {
            curator.delete().forPath(nodePath);
        }
        String serviceNodePath = curator.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(nodePath, serviceAddress.getBytes());
        System.out.println("注册成功;serviceNodePath:" + serviceNodePath);
    }

    public void close() {
        try {
            List<String> childrenList = curator.getChildren().forPath(this.serviceName);
            for (String serviceNode : childrenList) {
                String nodePath = this.serviceName + "/" + serviceNode;
                Stat state = curator.checkExists().forPath(nodePath);
                if (state != null) {
                    curator.delete().forPath(nodePath);
                }
            }
            curator.close();
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}
