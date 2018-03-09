package com.gserver.plugins.db.generator;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityGenerator extends AbsTemplateGenerator {
    protected String entityPackageName;
    protected String entityOutputDir;
    public EntityGenerator(String outputDir, String entityPackageName) {
        if (StringUtils.isBlank(entityPackageName))
            throw new IllegalArgumentException("daoPackageName can not be blank.");
        if (entityPackageName.contains("/") || entityPackageName.contains("\\"))
            throw new IllegalArgumentException("daoPackageName error : " + entityPackageName);
        if (StringUtils.isBlank(outputDir))
            throw new IllegalArgumentException("daoOutputDir can not be blank.");

        this.entityPackageName = entityPackageName;
        this.entityOutputDir = outputDir;
    }

    @Override
    protected String getOutputDir() {
        return this.entityOutputDir;
    }

    @Override
    protected String getPackage() {
        return this.entityPackageName;
    }

    @Override
    protected String getClassName(TableMeta tableMeta) {
        return tableMeta.entityName.replace("_", "");
    }

    @Override
    protected String getTemplate() {
        return "EntityTemplate.ftl";
    }

    @Override
    protected Map<String, Object> getData(TableMeta tableMeta) {
        String entityName = tableMeta.entityName.replace("_", "");
        Map<String, Object> root = new HashMap<>();
        List<Map<String,Object>> fields = new ArrayList<>();
        for (ColumnMeta columnMeta : tableMeta.columnMetas) {
            Map<String,Object> data = new HashMap<>();
            fields.add(data);
            data.put("fieldName",columnMeta.attrName);
            data.put("fieldType",columnMeta.javaType);
        }

        root.put("fields",fields);
        root.put("entity", entityName);
        root.put("tableName",tableMeta.entityName);
        root.put("className",getClassName(tableMeta));
        root.put("packageName", getPackage());
        return root;
    }
}
