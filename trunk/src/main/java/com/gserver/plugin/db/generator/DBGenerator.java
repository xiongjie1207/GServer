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

package com.gserver.plugin.db.generator;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 * 生成器
 * 1：生成时会强制覆盖 entity，建议不要修改三类文件，在数据库有变化重新生成一次便可
 */
public class DBGenerator {

	protected MetaBuilder metaBuilder;
	protected EntityGenerator baseModelGenerator;



	/**
	 * 构造 DBGenerator，只生成 entity
	 * @param dataSource 数据源
	 * @param ModelPackageName  包名
	 * @param ModelOutputDir  输出目录
	 */
	public DBGenerator(DataSource dataSource, String ModelPackageName, String ModelOutputDir) {
		this(dataSource, new EntityGenerator(ModelPackageName, ModelOutputDir));
	}

	public DBGenerator(DataSource dataSource, EntityGenerator baseModelGenerator) {
		if (dataSource == null) {
			throw new IllegalArgumentException("dataSource can not be null.");
		}
		if (baseModelGenerator == null) {
			throw new IllegalArgumentException("baseModelGenerator can not be null.");
		}

		this.metaBuilder = new MetaBuilder(dataSource);
		this.baseModelGenerator = baseModelGenerator;
	}



	/**
	 * 设置 MetaBuilder，便于扩展自定义 MetaBuilder
	 */
	public void setMetaBuilder(MetaBuilder metaBuilder) {
		if (metaBuilder != null) {
			this.metaBuilder = metaBuilder;
		}
	}

	public void setTypeMapping(TypeMapping typeMapping) {
		this.metaBuilder.setTypeMapping(typeMapping);
	}


	/**
	 * 添加不需要处理的数据表
	 */
	public void addExcludedTable(String... excludedTables) {
		metaBuilder.addExcludedTable(excludedTables);
	}

	public void generate() {
		List<TableMeta> tableMetas = metaBuilder.build();

		genrate(tableMetas);
	}
	public List<String> generate(String tables){
		String[] tableName = tables.split(",");
		List<String> nonexistent = new ArrayList<>();
		List<TableMeta> tableMetas = metaBuilder.build();
		List<TableMeta> tempTableMetas = new ArrayList<>();
		for (String name:tableName) {
			nonexistent.add(name);
			for (TableMeta meta : tableMetas) {
				if (meta.name.equalsIgnoreCase(name)){
					tempTableMetas.add(meta);
					nonexistent.remove(name);
				}
			}
		}
		genrate(tempTableMetas);
		System.out.println("表 "+nonexistent+" 不存在");
		return nonexistent;
	}
	private void genrate(List<TableMeta> tableMetas){
		long start = System.currentTimeMillis();
		if (tableMetas.size() == 0) {
			System.out.println("TableMeta 数量为 0，不生成任何文件");
			return ;
		}

		baseModelGenerator.generate(tableMetas);

		long usedTime = (System.currentTimeMillis() - start) / 1000;
		System.out.println("Generate complete in " + usedTime + " seconds.");
	}
}



