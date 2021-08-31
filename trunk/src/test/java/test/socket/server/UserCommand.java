package test.socket.server;

import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import com.gserver.components.commands.JsonCommander;
import com.gserver.components.net.packet.IPacket;
import com.gserver.components.net.packet.Packet;
import com.gserver.core.annotation.ActionKey;
import com.gserver.utils.Loggers;
import org.springframework.stereotype.Component;
import test.model.PlayerOuterClass;
import test.utils.Protocol;


@Component
public class UserCommand extends JsonCommander {
    @ActionKey(Protocol.Login)
    private void login(){
        try {
            PlayerOuterClass.Player.Builder builder = PlayerOuterClass.Player.newBuilder();
            builder.setId(100);
            builder.setName("张三");
            IPacket packet = Packet.newProtoBuilder(Protocol.JoinRoom).setData(builder.build()).build();
            getSocketSession().write(packet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @ActionKey(Protocol.JoinRoom)
    private void joinRoom() {
        try {
            String name = this.getDataToString("name");
            Loggers.GameLogger.info("接收到客户端:"+name+"的消息");
            PlayerOuterClass.Player.Builder builder = PlayerOuterClass.Player.newBuilder();
            Message message=builder.build();
            JsonFormat.printToString(message);
            builder.setId(100);
            builder.setName("张三");
            IPacket packet = Packet.newProtoBuilder(Protocol.JoinRoom).setData(builder.build()).build();
            getSocketSession().write(packet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
