package com.gserver.codec;
/**
 * Copyright (c) 2015-2016, James Xiong 熊杰 (xiongjie.cn@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by xiongjie on 2016/12/22.
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.Inflater;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
public class CustomZLibDecoder extends ByteToMessageDecoder {

	/** 
     * 解压缩 
     *  
     * @param data 
     *            待压缩的数据 
     * @return byte[] 解压缩后的数据 
     */  
    private byte[] decompress(byte[] data) {
        byte[] output = new byte[0];
  
        Inflater decompresser = new Inflater();  
        decompresser.reset();  
        decompresser.setInput(data);  
  
        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);  
        try {
            while (!decompresser.finished()) {
                byte[] buf = new byte[1024];
                int i = decompresser.inflate(buf);  
                o.write(buf, 0, i);  
            }  
            output = o.toByteArray();  
        } catch (Exception e) {  
            output = data;  
            e.printStackTrace();  
        } finally {  
            try {
                o.close();  
			} catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
  
        decompresser.end();  
        return output;  
    }
	@Override
	protected void decode(ChannelHandlerContext paramChannelHandlerContext, ByteBuf paramByteBuf,
			List<Object> paramList) throws Exception {
		// TODO Auto-generated method stub
        byte[] bytes = new byte[paramByteBuf.readableBytes()];
        paramByteBuf.readBytes(bytes);
        bytes = decompress(bytes);
        ByteBuf bb = Unpooled.buffer();
        bb.writeBytes(bytes);
        paramList.add(bb);
	}
}
