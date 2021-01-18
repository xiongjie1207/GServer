package test.socket.server;

import com.gserver.components.net.listener.ComponentHttpController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Administrator on 2017/1/11.
 */
@Controller("httpServerListener")
public class HttpServerController extends ComponentHttpController {

    @RequestMapping(value = "/process.do",method=RequestMethod.POST)
    public void process() {
        this.handle();

    }

}
