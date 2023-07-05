import com.wegame.framework.component.net.AbsServerSocketComponent;
import com.wegame.framework.config.ServerConfig;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;

import java.util.Map;

public class ServerSocketComponent extends AbsServerSocketComponent {

    @Override
    protected void initConfig(ServerConfig config) {
        config.setAllIdleTimeSeconds(6000);
        config.setPort(20001);
        config.setBossCount((short) 1);
        config.setReaderIdleTimeSeconds(6000);
        config.setWorkerCount((short) 10);
        config.setWriterIdleTimeSeconds(6000);
    }
    @Override
    protected void initOption(Map<ChannelOption<?>, Object> config) {
        config.put(ChannelOption.SO_BACKLOG, 1024);
    }

    @Override
    protected void initChildOption(Map<ChannelOption<?>, Object> config) {
        //重用地址
        config.put(ChannelOption.SO_REUSEADDR, true);
        config.put(ChannelOption.SO_RCVBUF, 65536);
        config.put(ChannelOption.SO_SNDBUF, 65536);
        config.put(ChannelOption.TCP_NODELAY, true);
        config.put(ChannelOption.SO_KEEPALIVE, true);
        config.put(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false));
    }

}