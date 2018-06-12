package com.gserver.components.db.descriptor;
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SimpleResolveDatabase extends ResolveDataBase {

    private Logger logger = Logger.getLogger(this.getClass());
    private final ConcurrentMap<Object, Content> cache = new ConcurrentHashMap<Object, Content>();

    public SimpleResolveDatabase(String name, DataSource dataSource) {
        super(name, dataSource);
    }

    public Table loadTable(String tableName) {
        return loadTable(null, tableName);
    }

    public Table loadTable(String database, String tableName) {
        return loadTable(database, tableName, null);
    }

    public Table loadTable(String database, String tableName, String versionField) {
        String cacheKey = tableName;
        if (StringUtils.isNotBlank(database)) {
            cacheKey = database + "#" + cacheKey;
        }
        Content content = cache.get(cacheKey);
        if (content != null) {
            return new Table(content);
        }
        content = new Content();
        boolean tableExist = false;
        ResultSet pkRS = null;
        ResultSet columnsRS = null;
        ResultSet indexRS = null;
        Connection con = null;
        try {
            con = this.getDataSource().getConnection();
            DatabaseMetaData databaseMetaData = con.getMetaData();
            //primary keys
            pkRS = databaseMetaData.getPrimaryKeys(null, null, tableName);
            while (pkRS.next()) {
                String columnName = pkRS.getString("COLUMN_NAME");
                content.addPrimaryFieldName(columnName);
            }

            //columns
            columnsRS = databaseMetaData.getColumns(null, null, tableName, null);
            while (columnsRS.next()) {
                Column field = new Column();
                field.setJdbcType(columnsRS.getInt("DATA_TYPE"));
                field.setName(columnsRS.getString("COLUMN_NAME"));
                field.setRequest(columnsRS.getBoolean("NULLABLE"));
                field.setLength(columnsRS.getInt("COLUMN_SIZE"));
                if (StringUtils.isNotEmpty(columnsRS.getString("REMARKS"))) {
                    field.setAliasName(columnsRS.getString("REMARKS"));
                }

                if (content.getPrimaryKey().getFields().contains(field.getName())) {
                    field.setPrimaryKey(true);
                }
                content.addField(field);
                tableExist = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(pkRS);
            closeResultSet(columnsRS);
            closeResultSet(indexRS);
            closeConnect(con);

        }

        if (tableExist) {
            Table table = new Table(content);
            content.setTableName(tableName);
            content.setDatabase(database);
            table.getAllSelectFields();
            if (StringUtils.isNotEmpty(versionField)
                    && content.getColumns().containsKey(versionField)) {
                content.setVersionField(content.getColumns().get(versionField));
            }
            cache.put(cacheKey, content);

            return table;
        } else {
            return null;
        }
    }


    @Override
    public Table reloadTable(String tableName) {
        return reloadTable(null, tableName);
    }

    @Override
    public Table reloadTable(String database, String tableName) {
        return reloadTable(database, tableName, null);
    }

    public Table reloadTable(String database, String tableName, String versionField) {
        String cacheKey = tableName;
        if (StringUtils.isNotBlank(database)) {
            cacheKey = database + "#" + tableName;
        }
        cache.remove(cacheKey);

        return loadTable(database, tableName, versionField);
    }


    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void closeConnect(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<String> loadTables() {
        List<String> tables = new ArrayList<String>();
        ResultSet tableRS = null;
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            DatabaseMetaData databaseMetaData = con.getMetaData();
            tableRS = databaseMetaData.getTables(null, "%", "%", new String[]{"TABLE"});
            while (tableRS.next()) {
                String name = tableRS.getString("TABLE_NAME");
                tables.add(name);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(tableRS);
            closeConnect(con);
        }
        return tables;
    }


}
