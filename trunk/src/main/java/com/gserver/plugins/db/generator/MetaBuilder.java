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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.sql.DataSource;

/**
 * MetaBuilder
 */
class MetaBuilder {

	protected DataSource dataSource;
	protected Set<String> excludedTables = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

	protected Connection conn = null;
	protected DatabaseMetaData dbMeta = null;


	protected TypeMapping typeMapping = new TypeMapping();

	public MetaBuilder(DataSource dataSource) {
		if (dataSource == null) {
			throw new IllegalArgumentException("dataSource can not be null.");
		}
		this.dataSource = dataSource;
	}


	public void addExcludedTable(String... excludedTables) {
		if (excludedTables != null) {
			for (String table : excludedTables) {
				this.excludedTables.add(table);
			}
		}
	}


	public void setTypeMapping(TypeMapping typeMapping) {
		if (typeMapping != null) {
			this.typeMapping = typeMapping;
		}
	}

	public List<TableMeta> build() {
		System.out.println("Build TableMeta ...");
		try {
			conn = dataSource.getConnection();
			dbMeta = conn.getMetaData();

			List<TableMeta> ret = new ArrayList<TableMeta>();
			buildTableNames(ret);
			for (TableMeta tableMeta : ret) {
				buildPrimaryKey(tableMeta);
				buildColumnMetas(tableMeta);
			}
			return ret;
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
		finally {
			if (conn != null)
				try {conn.close();} catch (SQLException e) {throw new RuntimeException(e);}
		}
	}

	/**
	 * 通过继承并覆盖此方法，跳过一些不希望处理的 table，定制更加灵活的 table 过滤规则
	 * @return 返回 true 时将跳过当前 tableName 的处理
	 */
	protected boolean isSkipTable(String tableName) {
		return false;
	}
	/**
	 * 不同数据库 dbMeta.getTables(...) 的 schemaPattern 参数意义不同
	 */
	protected ResultSet getTablesResultSet() throws SQLException {
		return dbMeta.getTables(conn.getCatalog(), null, null, new String[]{"TABLE", "VIEW"});
	}

	protected void buildTableNames(List<TableMeta> ret) throws SQLException {
		ResultSet rs = getTablesResultSet();
		while (rs.next()) {
			String tableName = rs.getString("TABLE_NAME");

			if (excludedTables.contains(tableName)) {
				System.out.println("Skip table :" + tableName);
				continue ;
			}
			if (isSkipTable(tableName)) {
				System.out.println("Skip table :" + tableName);
				continue ;
			}

			TableMeta tableMeta = new TableMeta();
			tableMeta.name = tableName;
			tableMeta.remarks = rs.getString("REMARKS");

			tableMeta.entityName = tableName;
			ret.add(tableMeta);
		}
		rs.close();
	}

	protected void buildPrimaryKey(TableMeta tableMeta) throws SQLException {
		ResultSet rs = dbMeta.getPrimaryKeys(conn.getCatalog(), null, tableMeta.name);

		String primaryKey = "";
		int index = 0;
		while (rs.next()) {
			if (index++ > 0)
				primaryKey += ",";
			primaryKey += rs.getString("COLUMN_NAME");
		}
		tableMeta.primaryKey = primaryKey;
		rs.close();
	}

	/**
	 *
	 * JDBC 与时间有关类型转换规则，mysql 类型到 java 类型如下对应关系：
	 * DATE				java.sql.Date
	 * DATETIME			java.sql.Timestamp
	 * TIMESTAMP[(M)]	java.sql.Timestamp
	 * TIME				java.sql.Time
	 *
	 * 对数据库的 DATE、DATETIME、TIMESTAMP、TIME 四种类型注入 new java.util.Date()对象保存到库以后可以达到“秒精度”
	 * 为了便捷性，getter、setter 方法中对上述四种字段类型采用 java.util.Date，可通过定制 TypeMapping 改变此映射规则
	 */
	protected void buildColumnMetas(TableMeta tableMeta) throws SQLException {
		String sql = "select * from `" + tableMeta.name + "` where 1 = 2";
		Statement stm = conn.createStatement();
		ResultSet rs = stm.executeQuery(sql);
		ResultSetMetaData rsmd = rs.getMetaData();

		for (int i=1; i<=rsmd.getColumnCount(); i++) {
			ColumnMeta cm = new ColumnMeta();
			cm.name = rsmd.getColumnName(i);

			String colClassName = rsmd.getColumnClassName(i);
			String typeStr = typeMapping.getType(colClassName);
			if (typeStr != null) {
				cm.javaType = typeStr;
			}
			else {
				int type = rsmd.getColumnType(i);
				if (type == Types.BINARY || type == Types.VARBINARY || type == Types.BLOB) {
					cm.javaType = "byte[]";
				}
				else if (type == Types.CLOB || type == Types.NCLOB) {
					cm.javaType = "java.lang.String";
				}
				else {
					cm.javaType = "java.lang.String";
				}
			}

			// 构造字段对应的属性名 attrName
			cm.attrName = buildAttrName(cm.name);

			tableMeta.columnMetas.add(cm);
		}

		rs.close();
		stm.close();
	}

	/**
	 * 构造 colName 所对应的 attrName，mysql 数据库名称和字段名采用首字母大写的方式
	 *
	 */
	protected String buildAttrName(String colName) {
		return colName;
	}
}







