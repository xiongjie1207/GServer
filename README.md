# GServer
本框架采用的开源技术:<br/>
1.netty长链接操作
2.spring,spring mvc 短链接操作，后台管理，对象管理，计划任务
3.log4j日志系统
4.在spring orm之上,包装了一层，从对象的增删改查动作生成sql语句
5.c3p0数据库连接池
6.缓存以spring data提供的接口，使用redis做缓存服务器
7.数据库为mysql，其他数据库未作大量测试

本项目使用maven构建，jdk版本1.8,ide使用intellij idea，希望大家用正版intellij idea，对jetbrains提供支持，
如果对本项目有更好的建议和想法请联系我qq:914772406

简单代码介绍：
<pre>
<code>
@Component
public class WereWolfServer extends GServer {

    @Override
    protected void initComponents(List<IComponent> components) {
        DataSource dataSource = SpringContext.getBean("dataSource");
        List<ResolveDataBase> resolveDatabases = new ArrayList<>();
        resolveDatabases.add(new SimpleResolveDatabase(DBType.MYSQL, dataSource));
        ComponentC3P0 dataSourcePlugin = new ComponentC3P0(resolveDatabases);
        components.add(dataSourcePlugin);
        //逻辑服务器
        ServerSocketListener serverSocketListener = SpringContext.getBean(ServerSocketListener.class);
        components.add(serverSocketListener);

    }

    @Override
    protected void afterStartServer() {
        
    }
    @Override
    protected void afterStopServer() {
        
    }
    @Override
    protected boolean debugModel() {
        return true;
    }

}
</code>
</pre>
<pre>
<code>
@Component
public class ServerSocketListener extends ComponentServerSocketListener{


    @Override
    protected void initConfig(ServerConfig config) {
        Map<String, Object> map = SpringContext.getBean("config");
        config.setPort(MapUtils.getShort(map, "socketPort"));
        config.setAllIdleTimeSeconds(6000);
        config.setReaderIdleTimeSeconds(6000);
        config.setWriterIdleTimeSeconds(6000);
    }

}
</code>
</pre>
<pre>
<code>
@Component
public class UserCommandar extends SocketCommander {
	private static final Logger log = LoggerFactory.getLogger(UserCommandar.class);

	
    /**
     * 登录游戏服务器
     */
    @ActionKey(ProtocolCode.COMMAND_LOGIN)
    public void login() {
	    try {
	    	Channel channel = this.getChannel();
        //...........
		} catch (Exception e) {
			log.error("login", e);
		}
    }
}
</code>
</pre>
commander中的actionKey可以支持基于tcp或者http协议的访问调用.

采用tomcat做服务器，使用spring扫描注解

ComponentScan中提供自己的路径
<pre>
<code>
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"server.commander", "server.listener","server.service.impl","server.dao.impl"},basePackageClasses = {Server.class})
@ImportResource({"/WEB-INF/dispatcher-servlet.xml"})
public class Config extends WebMvcConfigurerAdapter {

}
</code>
</pre>

目前使用json作为数据传输协议，传输协议必须包含以下的节点数据
{"pid":1}
pid即协议id，其他的数据则自己自定义添加。
如{"pid":1,"uname":"test","pwd":"123456"}
