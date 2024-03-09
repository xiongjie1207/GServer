package com.wegame.framework.grpc;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;

import java.util.List;

/**
 * @author fanqiechaodan
 * @Classname ServiceConsumer
 * @Description
 */
@Slf4j
public class Discovery implements PathChildrenCacheListener {

    private final int SESSION_TIMEOUT = 1000;
    private final int connectionTimeoutMs = 20000;
    private final int maxRetries = 10;
    private final CuratorFramework curator;
    private WatchListener listener;
    private String serviceName;
    private final int sleepMsBetweenRetry = 20000;
    private CuratorCache curatorCache;

    public Discovery(String zooKeeperAddress) {
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
     * 获取服务地址
     *
     * @param serviceName
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    @SneakyThrows
    public void watchService(String serviceName, WatchListener listener) {
        this.listener = listener;
        this.serviceName = serviceName;
        curatorCache = CuratorCache.builder(this.curator, this.serviceName).build();
        CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder()
                .forPathChildrenCache(this.serviceName, this.curator, this)
                .build();
        curatorCache.listenable().addListener(curatorCacheListener);
        curatorCache.start();

    }

    @SneakyThrows
    private void getChildrenList(String serviceName) {
        List<String> childrenList = curator.getChildren().forPath(serviceName);
        System.out.println("服务列表:" + childrenList);
        this.listener.removeAllChannel();
        for (String serviceNode : childrenList) {
            String nodePath = serviceName + "/" + serviceNode;
            String serviceUrl = new String(curator.getData().forPath((nodePath)));
            listener.addChannel(nodePath, serviceUrl);
        }
    }

    public void close() throws InterruptedException {
        curator.close();
    }


    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent watchedEvent) throws Exception {
        switch (watchedEvent.getType()) {
            case CHILD_ADDED -> {
                log.warn("服务上线:{}", watchedEvent.getData().getPath());
                String serviceUrl = new String(curator.getData().forPath(watchedEvent.getData().getPath()));
                listener.addChannel(watchedEvent.getData().getPath(), serviceUrl);
            }
            case CHILD_REMOVED -> {
                log.warn("服务下线:{}", watchedEvent.getData().getPath());
                if (curator.checkExists().forPath(watchedEvent.getData().getPath()) == null) {
                    listener.removeChannel(watchedEvent.getData().getPath());
                }
            }
            case CHILD_UPDATED -> {
                log.warn("子目录更新:{}", watchedEvent.getData().getPath());
                getChildrenList(watchedEvent.getData().getPath());
            }
        }
    }
}
