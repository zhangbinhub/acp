package io.github.zhangbinhub.acp.core.file.word;


import io.github.zhangbinhub.acp.core.CommonTools;
import io.github.zhangbinhub.acp.core.log.LogFactory;

public class WordService {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    /**
     * word 转 html
     *
     * @param filePath word文件全路径
     * @param foldPath 生成HTML所在路径，相对于webroot，默认为系统临时文件夹 files/tmp/html
     * @param basePath word中图片附件保存的相对地址，默认为html所在路径的img下
     */
    public String wordToHTML(String filePath, String foldPath, String basePath) {
        try {
            String ext = CommonTools.getFileExt(filePath);
            switch (ext) {
                case "doc": {
                    return DocToHtml.convert2Html(filePath, foldPath, basePath);
                }
                case "docx": {
                    throw new UnsupportedOperationException("fileType [" + ext + "] is unsupported!");
                }
                default:
                    throw new UnsupportedOperationException("fileType [" + ext + "] is not word file!");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }
}
