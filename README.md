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

简单介绍：
框架主要采用插件的思路，这里要感谢jfinal的作者，提供了一个思路，所有的功能都是以插件的形式集成到系统中，具体的使用方式可以看以下范例<br/>
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
public class SocketListener extends PluginSocketListener {
    private Logger logger = Logger.getLogger(this.getClass());

    public SocketListener() {
        this.setClientListener(getClientListener());
    }

    @Override
    protected void initConfig(ServerConfig config) {
        config.setPort((short) 5300);
    }

    @Override
    protected void initOption(Map<ChannelOption<?>, Object> config) {

    }

    @Override
    protected void initChildOption(Map<ChannelOption<?>, Object> config) {

    }

    protected ClientListener getClientListener() {
        return new ClientListener() {

            /*
             * 新客户端连接监听
             * @param ctx
             */
            public void onClientConnected(ChannelHandlerContext ctx) {
                logger.debug("client connected.............");
            }

            @Override
            public void onClientException(ChannelHandlerContext ctx) {
                logger.debug("client exception.............");
            }

            /*
                         * 断线监听
                         * @param ctx
                         */
            public void onClientDisconnected(ChannelHandlerContext ctx) {
                logger.debug("client disconnected.............");
            }

            @Override
            public void onReaderIdle(ChannelHandlerContext ctx) {
                logger.debug("client onReaderIdle.............");
            }

            @Override
            public void onWriterIdle(ChannelHandlerContext ctx) {
                logger.debug("client onWriterIdle.............");
            }

            @Override
            public void onAllIdle(ChannelHandlerContext ctx) {
                logger.debug("client onAllIdle.............");
            }
        };
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

采用tomcat做服务器，使用spring java config扫描注解

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
{"pid":"1"}
pid即协议id，其他的数据则自己自定义添加。
如{"pid":"1","uname":"test","pwd":"123456"}
