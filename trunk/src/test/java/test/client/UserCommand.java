package test.client;

import com.gserver.components.commands.Commander;
import com.gserver.components.net.packet.IPacket;
import com.gserver.components.net.packet.Packet;
import com.gserver.core.annotation.ActionKey;
import com.gserver.utils.Loggers;
import org.springframework.stereotype.Component;
import test.model.PlayerOuterClass;
import test.utils.Protocol;

import java.util.HashMap;


@Component
public class UserCommand extends Commander {

    @ActionKey(Protocol.JoinRoom)
    public void joinRoom() {
        try {
            byte[] data = this.getData();
            PlayerOuterClass.Player player = PlayerOuterClass.Player.parseFrom(data);
            Loggers.GameLogger.info(player.getName());

            HashMap<String, Object> map = new HashMap<>();
            map.put("name", "张三");
            IPacket packet = Packet.newJsonBuilder(Protocol.JoinRoom).setData(map).build();
            getSocketSession().write(packet);
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
