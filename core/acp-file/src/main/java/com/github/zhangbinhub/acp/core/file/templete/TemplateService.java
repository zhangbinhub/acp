package com.github.zhangbinhub.acp.core.file.templete;

import com.github.zhangbinhub.acp.core.file.FileOperation;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;

import com.github.zhangbinhub.acp.core.CommonTools;
import com.github.zhangbinhub.acp.core.log.LogFactory;

import java.io.*;
import java.util.Map;

public class TemplateService {

    private static final LogFactory log = LogFactory.getInstance(TemplateService.class);// 日志对象

    /**
     * 获取模板文件路径
     *
     * @param template 模板文件
     * @return [0]-模板所在文件夹，[1]-模板文件名
     */
    private static String[] getTemplatePath(String template) throws IOException {
        String[] result = new String[2];
        String templatePath = template.replace("/", File.separator).replace("\\", File.separator);
        if (templatePath.contains(File.separator)) {
            String fileName = templatePath.substring(templatePath.lastIndexOf(File.separator) + 1);
            templatePath = templatePath.substring(0, templatePath.lastIndexOf(File.separator));
            if (CommonTools.isNullStr(templatePath) || !CommonTools.isAbsPath(templatePath)) {
                templatePath = FileOperation.buildTemplateDir() + templatePath;
            }
            result[0] = templatePath;
            result[1] = fileName;
        } else {
            result[0] = FileOperation.buildTemplateDir();
            result[1] = templatePath;
        }
        return result;
    }

    /**
     * 通过模板生成文件
     *
     * @param template       模板路径，绝对路径或相对于系统模板根路径
     * @param variables      填充数据集
     * @param resultFilePath 目标文件
     * @return 文件绝对路径
     */
    public static String generateFile(String template, Map<String, Object> variables, String resultFilePath) {
        try {
            String[] templatePath = getTemplatePath(template);
            Configuration configuration = new Configuration(new Version(2, 3, 24));
            configuration.setDefaultEncoding(CommonTools.getDefaultCharset());
            configuration.setDirectoryForTemplateLoading(new File(templatePath[0]));
            Template tp = configuration.getTemplate(templatePath[1]);
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(resultFilePath))));
            tp.process(variables, out);
            log.info("create file \"" + resultFilePath + "\" from template \"" + templatePath[0] + File.separator + templatePath[1] + "\"");
            return resultFilePath;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 通过模板生成字符串
     *
     * @param template  模板路径，绝对路径或相对于系统模板根路径
     * @param variables 填充数据集
     * @return 目标字符串
     */
    public static String generateString(String template, Map<String, Object> variables) {
        try {
            String[] templatePath = getTemplatePath(template);
            Configuration configuration = new Configuration(new Version(2, 3, 24));
            configuration.setDefaultEncoding(CommonTools.getDefaultCharset());
            configuration.setDirectoryForTemplateLoading(new File(templatePath[0]));
            Template tp = configuration.getTemplate(templatePath[1]);
            Writer out = new StringWriter();
            tp.process(variables, out);
            log.info("create string from template \"" + templatePath[0] + File.separator + templatePath[1] + "\"");
            return out.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

}
