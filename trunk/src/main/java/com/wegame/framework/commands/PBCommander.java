package com.wegame.framework.commands;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

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
