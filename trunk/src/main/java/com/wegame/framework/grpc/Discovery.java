package com.wegame.framework.grpc;

import lombok.SneakyThrows;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;

/**
 * @author fanqiechaodan
 * @Classname ServiceConsumer
 * @Description
 */
public class Discovery implements org.apache.zookeeper.Watcher {

    private static final int SESSION_TIMEOUT = 500000;

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
        if (zooKeeper.exists(serviceName, this) == null) {
            zooKeeper.create(serviceName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        List<String> childrenList = zooKeeper.getChildren(serviceName, this);
        for (String serviceNode : childrenList) {
            String nodePath = serviceName + "/" + serviceNode;
            String serviceUrl = new String(zooKeeper.getData(nodePath, this, null));
            listener.add(nodePath, serviceUrl);
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
                String serviceUrl = new String(zooKeeper.getData(watchedEvent.getPath(), this, null));
                listener.add(watchedEvent.getPath(), serviceUrl);
            }
            case NodeDeleted -> {
                listener.remove(watchedEvent.getPath());
            }
            case NodeChildrenChanged -> {
                List<String> childrenList = zooKeeper.getChildren(watchedEvent.getPath(), this);
                for (String serviceNode : childrenList) {
                    String nodePath = watchedEvent.getPath() + "/" + serviceNode;
                    String serviceUrl = new String(this.zooKeeper.getData(nodePath, this, null));
                    listener.add(nodePath, serviceUrl);
                }

            }
        }

    }
}
