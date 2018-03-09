package com.gserver.plugins.db.generator;


import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public abstract class AbsTemplateGenerator {
    protected Configuration cfg;
    protected Template temp;

    public AbsTemplateGenerator() {
        this.cfg = new Configuration();
        try {
            cfg.setClassForTemplateLoading(this.getClass(), "/");
            cfg.setObjectWrapper(new DefaultObjectWrapper());
            temp = cfg.getTemplate(getTemplate());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected abstract String getOutputDir();

    protected abstract String getPackage();

    protected abstract String getClassName(TableMeta tableMeta);

    protected abstract String getTemplate();

    protected abstract Map<String, Object> getData(TableMeta tableMeta);

    public void generate(TableMeta tableMeta) {
        try {
            File dir = new File(getOutputDir() + File.separator + getPackage());
            if (!dir.exists())
                dir.mkdirs();
            String target = dir.toString() + File.separator + getClassName(tableMeta) + ".java";
            FileWriter fw = new FileWriter(target);
            BufferedWriter bw = new BufferedWriter(fw);
            Map<String, Object> root = getData(tableMeta);
            temp.process(root, bw);
            bw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
