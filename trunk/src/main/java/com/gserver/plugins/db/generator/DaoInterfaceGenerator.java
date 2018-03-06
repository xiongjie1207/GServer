package com.gserver.plugins.db.generator;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DaoInterfaceGenerator {
    protected String packageTemplate = "package %s;\n\n";
    protected String importTemplate ="import %s.%s;\n\n";
    protected String classDescriptTemplate = "/**\n * Generated by GServer, do not modify this file.\n **/\n";

    protected String classDefineTemplate = "public interface IDao%s {\n\t";

    protected String methodLoadDefineTemplate = "public %s load(%s id);\n\n\t";
    protected String methodUpdateDefineTemplate = "public void update(%s entity);\n\n\t";
    protected String methodDeleteDefineTemplate = "public void delete(%s entity);\n\n\t";
    protected String methodInsertDefineTemplate = "public Object insert(%s entity);\n\n\t";

    protected String iDaoPackageName;
    protected String iDaoOutputDir;
    protected String modelPakageName;

    public DaoInterfaceGenerator(String iDaoPackageName, String modelPakageName, String iDaoOutputDir) {
        if (StringUtils.isBlank(iDaoPackageName))
            throw new IllegalArgumentException("iDaoPackageName can not be blank.");
        if (StringUtils.isBlank(modelPakageName))
            throw new IllegalArgumentException("modelPackageName can not be blank.");
        if (iDaoPackageName.contains("/") || iDaoPackageName.contains("\\"))
            throw new IllegalArgumentException("iDaoPackageName error : " + iDaoPackageName);
        if (modelPakageName.contains("/") || modelPakageName.contains("\\"))
            throw new IllegalArgumentException("modelPakageName error : " + modelPakageName);
        if (StringUtils.isBlank(iDaoOutputDir))
            throw new IllegalArgumentException("daoOutputDir can not be blank.");

        this.iDaoPackageName = iDaoPackageName;
        this.modelPakageName = modelPakageName;
        this.iDaoOutputDir = iDaoOutputDir;
    }

    public void generate(List<TableMeta> tableMetas) {
        System.out.println("Generate interface dao ...");
        for (TableMeta tableMeta : tableMetas)
            genDaoContent(tableMeta);
        wirtToFile(tableMetas);
    }

    protected void genDaoContent(TableMeta tableMeta) {
        StringBuilder ret = new StringBuilder();
        genPackage(ret);
        genImport(tableMeta, ret);
        genClassDes(ret);
        genClassDefine(tableMeta, ret);
        genMethodLoad(tableMeta, ret);

        genMethodDelete(tableMeta, ret);

        genMethodUpdate(tableMeta, ret);

        genMethodInsert(tableMeta, ret);
        ret.append(String.format("%n"));
        ret.append(String.format("}%n"));
        tableMeta.modelContent = ret.toString();
    }

    protected void genPackage(StringBuilder ret) {
        ret.append(String.format(packageTemplate, iDaoPackageName));
    }

    protected void genImport(TableMeta tableMeta, StringBuilder ret) {
        ret.append(String.format(importTemplate, modelPakageName, tableMeta.entityName.replace("_", "")));
    }

    protected void genClassDefine(TableMeta tableMeta, StringBuilder ret) {
        ret.append(String.format(classDefineTemplate, tableMeta.entityName.replace("_", "")));
    }

    protected void genClassDes(StringBuilder ret) {
        ret.append(classDescriptTemplate);
    }


    /**
     * 生成加载方法
     *
     * @param tableMeta
     * @param ret
     */
    protected void genMethodLoad(TableMeta tableMeta, StringBuilder ret) {
        String entity = tableMeta.entityName.replace("_", "");
        ret.append(String.format(methodLoadDefineTemplate, entity, "Object", entity, entity));
    }

    /**
     * 生成更新方法
     *
     * @param tableMeta
     * @param ret
     */
    protected void genMethodUpdate(TableMeta tableMeta, StringBuilder ret) {
        ret.append(String.format(methodUpdateDefineTemplate, tableMeta.entityName.replace("_", "")));
    }

    /**
     * 生成删除方法
     *
     * @param tableMeta
     * @param ret
     */
    protected void genMethodDelete(TableMeta tableMeta, StringBuilder ret) {
        String entity = tableMeta.entityName.replace("_", "");
        ret.append(String.format(methodDeleteDefineTemplate, entity, entity, StringUtils.capitalize(tableMeta.primaryKey)));
    }

    /**
     * 生成插入方法
     *
     * @param tableMeta
     * @param ret
     */
    protected void genMethodInsert(TableMeta tableMeta, StringBuilder ret) {
        ret.append(String.format(methodInsertDefineTemplate, tableMeta.entityName.replace("_", "")));
    }

    protected void wirtToFile(List<TableMeta> tableMetas) {
        try {
            for (TableMeta tableMeta : tableMetas)
                wirtToFile(tableMeta);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * base model 覆盖写入
     */
    protected void wirtToFile(TableMeta tableMeta) throws IOException {
        File dir = new File(iDaoOutputDir + File.separator + iDaoPackageName.replace(".", File.separator));
        if (!dir.exists())
            dir.mkdirs();
        String target = dir.toString() + File.separator + "IDao" + tableMeta.entityName.replace("_", "") + ".java";
        FileWriter fw = new FileWriter(target);
        try {
            fw.write(tableMeta.modelContent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fw.close();
        }
    }
}
