package test.socket.server;

import com.gserver.components.IComponent;
import com.gserver.components.net.listener.ComponentServerSocketListener;
import com.gserver.config.ServerConfig;
import com.gserver.core.SpringContext;
import com.gserver.server.Launcher;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import org.apache.commons.collections.MapUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@SpringBootApplication
@ImportResource(value = {"classpath:server/server-dispatcher-servlet.xml"})

public class ServerLauncher extends Launcher {

    @Override
    protected void initComponents(List<IComponent> plugins) {
        plugins.add(new TestServerSocketListener());
        plugins.add(SpringContext.getBean("httpServerListener"));
    }

    public static void main(String[] args) {
        SpringApplication.run(ServerLauncher.class, args);
    }

    private static class TestServerSocketListener extends ComponentServerSocketListener {
        @Override
        protected void initConfig(ServerConfig config) {
            Properties properties = SpringContext.getBean("prop");
            config.setPort(MapUtils.getInteger(properties, "config.ports",5005));
            config.setVersion(MapUtils.getString(properties, "config.version","1.0.0"));
            config.setBindIP(MapUtils.getString(properties, "config.bindIp","0.0.0.0"));
            config.setMaxOnlinePlayer(MapUtils.getInteger(properties, "config.maxOnline",5000));
            config.setAllIdleTimeSeconds(MapUtils.getInteger(properties, "config.allIdleTimeSeconds",6000));
            config.setBossCount(MapUtils.getShort(properties, "config.bossCount",(short)1));
            config.setReaderIdleTimeSeconds(MapUtils.getInteger(properties, "config.readerIdleTimeSeconds",6000));
            config.setWorkerCount(MapUtils.getShort(properties, "config.workerCount",(short)10));
            config.setWriterIdleTimeSeconds(MapUtils.getInteger(properties, "config.writerIdleTimeSeconds",6000));
        }

        @Override
        protected void initOption(Map<ChannelOption<?>, Object> config) {
            config.put(ChannelOption.SO_BACKLOG, 1024);
        }

        @Override
        protected void initChildOption(Map<ChannelOption<?>, Object> config) {
            config.put(ChannelOption.SO_REUSEADDR, true); //重用地址
            config.put(ChannelOption.SO_RCVBUF, 65536);
            config.put(ChannelOption.SO_SNDBUF, 65536);
            config.put(ChannelOption.TCP_NODELAY, true);
            config.put(ChannelOption.SO_KEEPALIVE, true);
            config.put(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false));
        }


    }
}
