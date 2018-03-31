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
package com.gserver.plugins.db.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * TableMeta
 */
class TableMeta {
	
	public String name;					// 表名
	public String remarks="";				// 表备注
	public String primaryKey;			// 主键，复合主键以逗号分隔
	public List<ColumnMeta> columnMetas = new ArrayList<ColumnMeta>();	// 字段 meta


	public String entityName;			// 生成的 model 名

}




