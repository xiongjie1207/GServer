package com.wegame.framework.commands;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author xiongjie
 * @Date 2022/11/17 20:30
 **/
@Slf4j
public class PBCommander extends Commander{
    public <T> T getMessage(Class<T> clazz){
        T newObject = null;
        try {
            Codec<T> codec = ProtobufProxy.create(clazz);
            newObject = codec.decode(this.getData());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return newObject;
    }
}
