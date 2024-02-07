package com.wegame.framework.grpc;

import io.grpc.Channel;
import lombok.Data;

/**
 * @Author xiongjie
 * @Date 2024/02/06 12:58
 **/
@Data
public class GrpcChannel {
    public GrpcChannel(String key, Channel channel) {
        this.key = key;
        this.channel = channel;
    }

    private String key;
    private Channel channel;
}
