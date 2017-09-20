package com.gserver.plugins.db.criteria;
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
public enum Condition{
        IS_NULL(1),
        IS_NOT_NULL(2),
        EQUAL(3),
        NOT_EQUAL(4),
        GREATER_THAN(5),
        GREATER_THAN_OR_EQUAL(6),
        LESS_THAN(7),
        LESS_THAN_OR_EQUAL(8),
        LIKE(9),
        NOT_LIKE(10),
        IN(11),
        NOT_IN(12),
        BETWEEN(13),
        NOT_BETWEEN(14),
        SQL(15);
        
        public final int type;
        Condition(int type){
            this.type = type;
        }
        
        public int getType(){
        	return this.type;
        }
       
    }