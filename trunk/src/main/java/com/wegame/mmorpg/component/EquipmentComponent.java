package com.wegame.mmorpg.component;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Data;

@Data
public class EquipmentComponent {
    @Protobuf(order = 1, required = true)
    private int weaponType;

    @Protobuf(order = 2, required = true)
    private int amorType;

}



