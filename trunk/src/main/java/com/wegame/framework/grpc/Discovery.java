package com.wegame.framework.grpc;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;

/**
 * @author fanqiechaodan
 * @Classname ServiceConsumer
 * @Description
 */
@Slf4j
public class Discovery implements org.apache.zookeeper.Watcher {

    private static final int SESSION_TIMEOUT = 20000;

    private final ZooKeeper zooKeeper;
    private WatchListener listener;
    private String serviceName;

    public Discovery(String zooKeeperAddress) throws IOException {
        this.zooKeeper = new ZooKeeper(zooKeeperAddress, SESSION_TIMEOUT, this);
    }

    /**
     * 获取服务地址
     *
     * @param serviceName
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void watchService(String serviceName, WatchListener listener) throws KeeperException, InterruptedException {
        this.listener = listener;
        this.serviceName = serviceName;
        if (zooKeeper.exists(serviceName, true) == null) {
            zooKeeper.create(serviceName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        getChildrenList(this.serviceName);

    }

    private void getChildrenList(String serviceName) throws InterruptedException, KeeperException {
        List<String> childrenList = zooKeeper.getChildren(serviceName, true);
        System.out.println("服务列表:" + childrenList);
        this.listener.removeAllChannel();
        for (String serviceNode : childrenList) {
            String nodePath = serviceName + "/" + serviceNode;
            String serviceUrl = new String(zooKeeper.getData(nodePath, this, null));
            listener.addChannel(nodePath, serviceUrl);
        }
    }

    public void close() throws InterruptedException {
        zooKeeper.close();
    }

    @SneakyThrows
    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case NodeCreated -> {
                log.warn("服务上线:{}", watchedEvent.getPath());
                String serviceUrl = new String(zooKeeper.getData(watchedEvent.getPath(), true, null));
                listener.addChannel(watchedEvent.getPath(), serviceUrl);
            }
            case NodeDeleted -> {
                log.warn("服务下线:{}", watchedEvent.getPath());
                if (zooKeeper.exists(watchedEvent.getPath(), true) == null) {
                    listener.removeChannel(watchedEvent.getPath());
                }
            }
            case NodeChildrenChanged -> {
                log.warn("子目录更新:{}", watchedEvent.getPath());
                getChildrenList(this.serviceName);
            }
        }
    }
}
