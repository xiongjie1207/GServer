package com.gserver.components.db.asyn;
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
public enum Method {

    SELECT_BY_CRITERIA(1),

    COUNT_BY_CRITERIA(5),

    SELECT_BY_PRIMARY_KEY(6),

    UPDATE(7),

    DELETE(8),

    INSERT(2),

    INSERT_TABLE(3),

    INSERT_DATABASE_TABLE(4),

    CLEAR(9);

    public final int type;

    Method(int type) {
        this.type = type;
    }

}
