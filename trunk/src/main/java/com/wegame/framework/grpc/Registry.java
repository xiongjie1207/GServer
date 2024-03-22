package com.wegame.framework.grpc;


import com.wegame.framework.config.ZKConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

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
    private ZKConfig zkConfig;

    public Registry(ZKConfig zkConfig) {
        this.zkConfig = zkConfig;
        this.curator = CuratorFrameworkFactory.builder()
                //连接地址  集群用,隔开
                .connectString(this.zkConfig.getZKAddress())
                .connectionTimeoutMs(connectionTimeoutMs)
                //会话超时时间
                .sessionTimeoutMs(SESSION_TIMEOUT)
                //设置重试机制
                .retryPolicy(new ExponentialBackoffRetry(sleepMsBetweenRetry, maxRetries))
                .build();
        this.curator.start();
        this.registryService();
    }

    /**
     * 注册服务
     */
    @SneakyThrows
    private void registryService() {
        // 如果根节点不存在就创建
        String nodePath = zkConfig.getPrefixFormat(zkConfig.getServiceName()) + "/" + zkConfig.getZKValue();
        Stat state = curator.checkExists().forPath(nodePath);
        if (state != null) {
            curator.delete().forPath(nodePath);
        }
        String serviceNodePath = curator.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(nodePath, this.zkConfig.getZKValue().getBytes());
        System.out.println("注册成功;serviceNodePath:" + serviceNodePath);
    }

    public void close() {
        try {
            String rootNodePath = zkConfig.getPrefixFormat(zkConfig.getServiceName()) + "/";
            List<String> childrenList = curator.getChildren().forPath(rootNodePath);
            for (String serviceNode : childrenList) {
                String childNodePath = rootNodePath + serviceNode;
                Stat state = curator.checkExists().forPath(childNodePath);
                if (state != null) {
                    curator.delete().forPath(childNodePath);
                }
            }
            curator.close();
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}
