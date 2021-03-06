package com.gserver.components.db;

import com.gserver.components.IComponent;
import com.gserver.components.db.descriptor.ResolveDataBase;
import com.gserver.components.db.spring.jdbc.SpringJDBCBuilder;

import java.util.LinkedHashMap;
import java.util.List;

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
 * Created by xiongjie on 2016/12/23.
 */
public class ComponentC3P0 implements IComponent {

    private LinkedHashMap<String, ResolveDataBase> dataSourceLinkedHashMap;

    public ComponentC3P0(List<ResolveDataBase> listResolveDataBase) {
        dataSourceLinkedHashMap = new LinkedHashMap<String, ResolveDataBase>();
        for (ResolveDataBase resolveDataBase:listResolveDataBase){
            dataSourceLinkedHashMap.put(resolveDataBase.getName(),resolveDataBase);
        }
    }

    @Override
    public String getName() {
        return "c3p0";
    }

    @Override
    public boolean start() {
        SpringJDBCBuilder.getInstance().configDataSource(this.dataSourceLinkedHashMap);
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}