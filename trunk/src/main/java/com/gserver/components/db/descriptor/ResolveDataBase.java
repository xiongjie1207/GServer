package com.gserver.components.db.descriptor;

import javax.sql.DataSource;
import java.util.List;

public abstract class ResolveDataBase {
    public ResolveDataBase(String name,DataSource dataSource){
        this.dataSource=dataSource;this.name=name;
    }
    private DataSource dataSource;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    /**
     * 解析表
     *
     * @param tableName table name
     * @return table
     */
    public abstract Table loadTable(String tableName);

    /**
     * 重新加载表数据
     *
     * @param tableName table name
     * @return table
     */
    public abstract Table reloadTable(String tableName);

    /**
     * 解析表
     *
     * @param database  database
     * @param tableName table name
     * @return table
     */
    public abstract Table loadTable(String database, String tableName);

    public abstract Table loadTable(String database, String tableName, String versionField);

    /**
     * 重新加载表数据
     *
     * @param database  database
     * @param tableName table name
     * @return table
     */
    public abstract Table reloadTable(String database, String tableName);

    public abstract Table reloadTable(String database, String tableName, String versionField);

    /**
     * 加载所有表
     *
     * @return all table names
     */
    public abstract List<String> loadTables();

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }
}
