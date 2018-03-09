package com.gserver.plugins.db.generator;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class DaoInterfaceGenerator extends AbsTemplateGenerator {
    private String outputDir;
    private String packageName;
    private String modelPackageName;

    public DaoInterfaceGenerator(String outputDir, String packageName, String modelPackageName) {
        if (StringUtils.isBlank(packageName))
            throw new IllegalArgumentException("packageName can not be blank.");
        if (packageName.contains("/") || packageName.contains("\\"))
            throw new IllegalArgumentException("packageName error : " + packageName);
        if (StringUtils.isBlank(modelPackageName))
            throw new IllegalArgumentException("modelPackageName can not be blank.");
        if (modelPackageName.contains("/") || modelPackageName.contains("\\"))
            throw new IllegalArgumentException("modelPackageName error : " + modelPackageName);
        if (StringUtils.isBlank(outputDir))
            throw new IllegalArgumentException("daoOutputDir can not be blank.");
        this.outputDir = outputDir;
        this.packageName = packageName;
        this.modelPackageName = modelPackageName;

    }

    @Override
    protected String getOutputDir() {
        return this.outputDir;
    }

    @Override
    protected String getPackage() {
        return this.packageName;
    }


    @Override
    protected String getClassName(TableMeta tableMeta) {
        return "IDao"+tableMeta.entityName.replace("_","");
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
        root.put("className",getClassName(tableMeta));
        root.put("packageName", getPackage());
        root.put("modelPackageName", importClasses);
        root.put("importClasses",importClasses.toString());
        return root;
    }
}
