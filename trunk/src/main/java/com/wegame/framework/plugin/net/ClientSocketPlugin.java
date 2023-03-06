package com.wegame.framework.plugin.net;

import io.netty.channel.ChannelFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author xiongjie
 * @Date 2023/03/06 14:58
 **/
@Slf4j
public class ClientSocketPlugin extends AbsClientSocketPlugin{
    @Override
    protected void OnConnectedHandler(ChannelFuture future){
        if (future.isSuccess()) {
            log.debug("服务端连接成功...");
        } else {
            future.channel().eventLoop().schedule(() -> connect(), 500, TimeUnit.MILLISECONDS);
        }
    }
}
