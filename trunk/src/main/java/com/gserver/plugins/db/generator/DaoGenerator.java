package com.gserver.plugins.db.generator;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

class DaoGenerator extends AbsTemplateGenerator {
    protected String daoPackageName;
    protected String daoOutputDir;
    protected String modelPackageName;
    protected String cacheName = "";

    public DaoGenerator(String cacheName, String outputDir, String daoPackageName, String modelPackageName) {
        if (StringUtils.isBlank(cacheName))
            throw new IllegalArgumentException("daoPackageName can not be blank.");
        if (StringUtils.isBlank(daoPackageName))
            throw new IllegalArgumentException("daoPackageName can not be blank.");
        if (StringUtils.isBlank(modelPackageName))
            throw new IllegalArgumentException("modelPackageName can not be blank.");
        if (daoPackageName.contains("/") || daoPackageName.contains("\\"))
            throw new IllegalArgumentException("daoPackageName error : " + daoPackageName);
        if (modelPackageName.contains("/") || modelPackageName.contains("\\"))
            throw new IllegalArgumentException("modelPackageName error : " + modelPackageName);
        if (StringUtils.isBlank(outputDir))
            throw new IllegalArgumentException("daoOutputDir can not be blank.");

        this.daoPackageName = daoPackageName;
        this.modelPackageName = modelPackageName;
        this.daoOutputDir = outputDir;
        this.cacheName = cacheName;
    }

    @Override
    protected String getOutputDir() {
        return this.daoOutputDir;
    }

    @Override
    protected String getPackage() {
        return this.daoPackageName;
    }

    @Override
    protected String getClassName(TableMeta tableMeta) {
        return "IDao"+tableMeta.entityName.replace("_", "");
    }

    @Override
    protected String getTemplate() {
        return "IDaoTemplate.ftl";
    }

    @Override
    protected Map<String, Object> getData(TableMeta tableMeta) {
        String entityName = tableMeta.entityName.replace("_", "");
        StringBuilder importClasses = new StringBuilder();
        importClasses.append("import ").append(modelPackageName).append(".").append(entityName).append(";");
        Map<String, Object> root = new HashMap<>();
        root.put("entity", entityName);
        root.put("packageName", getPackage());
        root.put("cacheName", cacheName);
        if (tableMeta.primaryKey != null) {
            root.put("primaryKey", tableMeta.primaryKey);
        }
        root.put("importClasses", importClasses.toString());
        return root;
    }
}
