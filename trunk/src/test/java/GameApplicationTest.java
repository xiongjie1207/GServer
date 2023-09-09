import com.wegame.framework.server.Launcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@Slf4j
@SpringBootApplication
public class GameApplicationTest extends Launcher implements ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(GameApplicationTest.class);
    }

    @Override
    public void run(ApplicationArguments args) {
        startServer();
    }

    @Bean
    protected TcpServerComponent newServerSocketComponent(){
        return new TcpServerComponent();
    }
    @Override
    protected void beforeStop() {
        log.debug("服务器关闭");
    }
    @Bean
    protected TcpServerComponent newSocketComponent(){
        return new TcpServerComponent();
    }
}
