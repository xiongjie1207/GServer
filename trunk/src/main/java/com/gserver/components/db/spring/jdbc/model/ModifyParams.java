package com.gserver.components.db.spring.jdbc.model;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModifyParams implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -9165433475864049756L;

    private List<Object> params;

    private List<Integer> types;

    public ModifyParams() {
        params = new ArrayList<>();
        types = new ArrayList<>();
    }


    public List<Object> getParamsValue() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public List<Integer> getParamsType() {
        return types;
    }

    public void setTypes(List<Integer> types) {
        this.types = types;
    }
}
