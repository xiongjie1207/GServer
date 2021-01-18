package test.client;

import com.gserver.components.IComponent;
import com.gserver.components.net.listener.ComponentClientSocketListener;
import com.gserver.config.ClientConfig;
import com.gserver.components.net.packet.IPacket;
import com.gserver.components.net.packet.Packet;
import com.gserver.server.Launcher;
import io.netty.channel.ChannelOption;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import test.utils.Protocol;

import java.util.List;
import java.util.Map;

@Component
public class ClientLauncher extends Launcher {
    private static ClassPathXmlApplicationContext classPathXmlApplicationContext;

    @Override
    protected void initComponents(List<IComponent> plugins) {
        plugins.add(new TestClientSocketListener());
    }

    public static void main(String[] args) {
        classPathXmlApplicationContext = new ClassPathXmlApplicationContext("client/client-dispatcher-servlet.xml");
    }

    public class TestClientSocketListener extends ComponentClientSocketListener {
        @Override
        protected void initConfig(ClientConfig config) {
            config.setPort(5005);
            config.setHost("127.0.0.1");
        }

        @Override
        protected void initOption(Map<ChannelOption<?>, Object> config) {
            config.put(ChannelOption.SO_BACKLOG, 1024);
        }

        @Override
        protected void OnConnected(boolean flag) {
            IPacket packet = Packet.newNetBuilder(Protocol.Login).build();
            this.getChannelFuture().channel().writeAndFlush(packet);
        }
    }
}
