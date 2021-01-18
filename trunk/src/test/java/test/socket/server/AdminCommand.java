package test.socket.server;

import com.gserver.components.commands.JsonCommander;
import com.gserver.components.net.packet.IPacket;
import com.gserver.components.net.packet.Packet;
import com.gserver.core.annotation.ActionKey;
import org.springframework.stereotype.Component;
import test.utils.Protocol;

import java.util.HashMap;


@Component
public class AdminCommand extends JsonCommander {
    @ActionKey(test.utils.Protocol.AdminLogin)
    private void adminLogin() {
        try {
            String name = this.getDataToString("name");
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", "张三");
            IPacket packet = Packet.newJsonBuilder(Protocol.AdminLogin).setData(map).build();
            createJsonRenderer().render(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
