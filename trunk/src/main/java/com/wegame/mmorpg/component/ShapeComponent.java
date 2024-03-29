package com.wegame.mmorpg.component;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Data;

@Data
public class ShapeComponent {

    @Protobuf(order = 1, required = true)
    private float height;
    @Protobuf(order = 2, required = true)
    private float R;


}
