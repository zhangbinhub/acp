package com.github.zhangbinhub.acp.core;

import com.github.zhangbinhub.acp.core.log.LogFactory;
import com.github.zhangbinhub.acp.core.file.FileOperation;
import com.github.zhangbinhub.acp.core.file.excel.data.ExcelCellData;
import com.github.zhangbinhub.acp.core.file.excel.scheme.ExcelDataSetting;
import com.github.zhangbinhub.acp.core.file.excel.scheme.ExcelSheetSetting;
import com.github.zhangbinhub.acp.core.file.excel.scheme.ExcelType;
import com.github.zhangbinhub.acp.core.file.excel.ExcelService;
import com.github.zhangbinhub.acp.core.file.pdf.PDFService;
import com.github.zhangbinhub.acp.core.file.pdf.PageNumberHandle;
import com.github.zhangbinhub.acp.core.file.pdf.PermissionType;
import com.github.zhangbinhub.acp.core.file.pdf.fonts.FontLoader;
import com.github.zhangbinhub.acp.core.file.templete.TemplateService;
import com.github.zhangbinhub.acp.core.file.word.WordService;
import com.github.zhangbinhub.acp.core.file.word.WordType;
import com.github.zhangbinhub.acp.core.log.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class FileTools {

    private static final LogFactory log = LogFactory.getInstance(FileTools.class);// 日志对象

    /**
     * 初始化文件工具类
     */
    public static void initTools() {
        FontLoader.initFonts();
    }

    private static String generateNowTimeString() {
        return CommonTools.getDateTimeString(null, "yyyyMMddHHmmssSSS");
    }

    private static String formatFileName(String fileName) {
        if (!CommonTools.isNullStr(fileName)) {
            fileName = fileName.replace("/", File.separator).replace("\\", File.separator);
        }
        return fileName;
    }

    private static String generateExcelResultFileName(ExcelType fileType, String fileName) throws IOException {
        fileName = formatFileName(fileName);
        String nowStr = generateNowTimeString();
        String resultFile;
        if (CommonTools.isNullStr(fileName)) {
            resultFile = FileOperation.buildTmpDir() + File.separator + nowStr + fileType.getName();
        } else if (!CommonTools.isAbsPath(fileName)) {
            resultFile = FileOperation.buildTmpDir() + File.separator + fileName + fileType.getName();
        } else {
            resultFile = CommonTools.getAbsPath(fileName) + fileType.getName();
        }
        return resultFile;
    }

    private static String generatePDFResultFileName(String resultFileName) throws IOException {
        resultFileName = formatFileName(resultFileName);
        String nowStr = generateNowTimeString();
        String resultFile;
        if (CommonTools.isNullStr(resultFileName)) {
            resultFile = FileOperation.buildTmpDir() + File.separator + nowStr + ".pdf";
        } else if (!CommonTools.isAbsPath(resultFileName)) {
            resultFile = FileOperation.buildTmpDir() + File.separator + resultFileName + ".pdf";
        } else {
            resultFile = CommonTools.getAbsPath(resultFileName) + ".pdf";
        }
        return resultFile;
    }

    /**
     * 十六进制字符串转图片文件
     *
     * @param HexString      十六进字符串
     * @param FileName       文件名
     * @param ExtensionsName 扩展名
     * @param PathFlag       生成图片文件路径标志:0-相对于WebRoot；1-自定义
     * @param ResultPathFlag 返回文件路径标志:0-相对于WebRoot；1-绝对路径
     * @param ParentPath     生成图片所在目录
     * @param isDelete       是否异步删除临时图片
     * @return 临时图片路径
     */
    public static String HexToImage(String HexString, String FileName, String ExtensionsName, int PathFlag, int ResultPathFlag, String ParentPath, boolean isDelete) {
        return FileOperation.HexToImage(HexString, FileName, ExtensionsName, PathFlag, ResultPathFlag, ParentPath, isDelete);
    }

    /**
     * 使用freemarker模板，生成文件
     *
     * @param templatePath 模板路径（绝对路径，或相对于系统模板根路径）
     * @param data         数据
     * @param fileName     目标文件名，带扩展名
     * @return 相对于webRoot路径
     */
    public static String exportToFileFromTemplate(String templatePath, Map<String, Object> data, String fileName) {
        try {
            fileName = formatFileName(fileName);
            String nowStr = generateNowTimeString();
            String webRootAdsPath = CommonTools.getWebRootAbsPath();
            String exName = CommonTools.getFileExt(templatePath);
            String resultFile;
            if (CommonTools.isNullStr(fileName)) {
                resultFile = FileOperation.buildTmpDir() + File.separator + nowStr + exName;
            } else if (!CommonTools.isAbsPath(fileName)) {
                resultFile = FileOperation.buildTmpDir() + File.separator + fileName;
            } else {
                resultFile = CommonTools.getAbsPath(fileName);
            }
            String filepath;
            filepath = TemplateService.generateFile(templatePath, data, resultFile);
            return filepath.replace(webRootAdsPath, "").replaceAll("\\\\", "/");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * word转html
     *
     * @param filePath word文件
     * @param foldPath 生成HTML所在路径，相对于webRoot，默认为系统临时文件夹 files/tmp/html
     * @param basePath word中图片附件保存的相对地址，默认为html所在路径的img下
     * @param isDelete 是否删除word文件
     * @return 相对于webRoot路径
     */
    public static String wordToHTML(String filePath, String foldPath, String basePath, boolean isDelete) throws IOException {
        if (CommonTools.isNullStr(filePath)) {
            filePath = "/files/tmp/html";
        }
        String webRootAdsPath = CommonTools.getWebRootAbsPath();
        if (CommonTools.isNullStr(foldPath)) {
            foldPath = FileOperation.buildTmpDir() + File.separator + "html";
        } else {
            foldPath = foldPath.replace("/", File.separator).replace("\\", File.separator);
            if (CommonTools.isAbsPath(foldPath)) {
                log.error("wordToHTML foldPath need relative path!");
                return "";
            }
            foldPath = webRootAdsPath + foldPath;
        }
        if (!CommonTools.isNullStr(basePath)) {
            if (CommonTools.isAbsPath(basePath)) {
                log.error("wordToHTML basePath need relative path!");
                return "";
            }
        }
        String wordPath = CommonTools.getAbsPath(filePath);
        WordService ws = new WordService();
        String htmlFile = ws.wordToHTML(wordPath, foldPath, basePath);
        if (isDelete) {
            CommonTools.doDeleteFile(new File(wordPath), false);
        }
        return htmlFile.replace(webRootAdsPath, "").replaceAll("\\\\", "/");
    }

    /**
     * 使用freemarker模板，生成word
     *
     * @param wordType     word类型
     * @param templatePath 模板路径（绝对路径，或相对于webRoot/files/template）
     * @param data         数据
     * @param fileName     目标文件名
     * @return 相对于webRoot路径
     */
    public static String exportToWordFromTemplate(WordType wordType, String templatePath, Map<String, Object> data, String fileName) throws IOException {
        fileName = formatFileName(fileName);
        String nowStr = generateNowTimeString();
        String extName;
        if (wordType.equals(WordType.WORD_TYPE_DOCX)) {
            extName = ".xml";
        } else {
            extName = ".doc";
        }
        String resultFile;
        if (CommonTools.isNullStr(fileName)) {
            resultFile = FileOperation.buildTmpDir() + File.separator + nowStr + extName;
        } else if (!CommonTools.isAbsPath(fileName)) {
            resultFile = FileOperation.buildTmpDir() + File.separator + fileName + extName;
        } else {
            resultFile = CommonTools.getAbsPath(fileName) + extName;
        }
        return exportToFileFromTemplate(templatePath, data, resultFile);
    }

    /**
     * 读取excel文件
     *
     * @param filePath 文件路径:相对于webRoot
     * @param sheetNo  工作表序号
     * @param beginRow 读取的起始行
     * @param beginCol 读取的起始列
     * @param rowNo    读取的行数，0则表示读取全部
     * @param colNo    读取的列数，0则表示读取全部
     * @param isDelete 是否读取完数据后删除文件
     * @return 相对于webRoot路径
     */
    public static List<List<ExcelCellData>> readExcelData(String filePath, int sheetNo, int beginRow, int beginCol, int rowNo, int colNo, boolean isDelete) {
        String excelPath = (CommonTools.getWebRootAbsPath() + filePath).replace("\\", File.separator).replace("/", File.separator);
        ExcelService excelService = new ExcelService();
        return excelService.readExcelData(excelPath, sheetNo, beginRow, beginCol, rowNo, colNo, isDelete);
    }

    /**
     * 导出Excel文件
     *
     * @param fileType     文件类型
     * @param isHorizontal 是否为横向
     * @return 相对于webRoot的文件位置
     */
    public static String exportToExcelByDataSetting(ExcelType fileType, boolean isHorizontal, ExcelDataSetting excelDataSetting) throws IOException {
        String webRootAdsPath = CommonTools.getWebRootAbsPath();
        String filename = generateExcelResultFileName(fileType, null);
        ExcelService es = new ExcelService();
        String filepath = es.createExcelFile(filename, isHorizontal, excelDataSetting);
        return filepath.replace(webRootAdsPath, "").replaceAll("\\\\", "/");
    }

    /**
     * 生成Excel文件
     *
     * @param fileType         文件类型
     * @param sheetSettingList 配置信息
     * @param fileName         目标文件名
     * @return 相对于webRoot路径
     */
    public static String exportToExcel(ExcelType fileType, List<ExcelSheetSetting> sheetSettingList, String fileName) throws IOException {
        String webRootAdsPath = CommonTools.getWebRootAbsPath();
        String resultFile = generateExcelResultFileName(fileType, fileName);
        ExcelService es = new ExcelService();
        String filepath = es.createExcelFile(resultFile, sheetSettingList);
        return filepath.replace(webRootAdsPath, "").replaceAll("\\\\", "/");
    }

    /**
     * 通过模板创建Excel
     *
     * @param fileType     文件类型
     * @param templatePath 模板绝对路径
     * @param data         数据，数据键必须大写
     * @param fileName     生成的文件名
     * @return 相对于webRoot路径
     */
    public static String exportToExcel(ExcelType fileType, String templatePath, Map<String, String> data, String fileName) throws IOException {
        String webRootAdsPath = CommonTools.getWebRootAbsPath();
        String resultFile = generateExcelResultFileName(fileType, fileName);
        ExcelService es = new ExcelService();
        String filepath = es.createExcelFile(resultFile, templatePath, data);
        return filepath.replace(webRootAdsPath, "").replaceAll("\\\\", "/");
    }

    /**
     * 通过freemarker模板创建Excel
     *
     * @param fileType     文件类型
     * @param templatePath 模板路径（绝对路径，或相对于webRoot/files/template）
     * @param data         数据
     * @param fileName     生成的文件名
     * @return 相对于webRoot路径
     */
    public static String exportToExcelFromTemplate(ExcelType fileType, String templatePath, Map<String, Object> data, String fileName) throws IOException {
        return exportToFileFromTemplate(templatePath, data, generateExcelResultFileName(fileType, fileName));
    }

    /**
     * HTML页面转为PDF
     *
     * @param htmlStr html页面源码，必须完整
     * @return 相对于webRoot的文件位置
     */
    public static String htmlToPDF(String htmlStr) throws IOException {
        return htmlToPDF(htmlStr, null);
    }

    /**
     * HTML页面转为PDF
     *
     * @param htmlStr  html页面源码，必须完整
     * @param fileName 文件名
     * @return 相对于webRoot的文件位置
     */
    public static String htmlToPDF(String htmlStr, String fileName) throws IOException {
        String resultFile = generatePDFResultFileName(fileName);
        PDFService pdfService = new PDFService();
        return pdfService.htmlToPDF(htmlStr, resultFile, null).replace(CommonTools.getWebRootAbsPath(), "").replaceAll("\\\\", "/");
    }

    /**
     * HTML页面转为PDF
     *
     * @param htmlFilePath html文件路径
     * @param fileName     文件名
     * @param basePath     图片相对路径，为空则默认html文件所在路径
     * @return 相对于webRoot路径
     */
    public static String htmlFileToPDF(String htmlFilePath, String fileName, String basePath, boolean isDelete) throws IOException {
        String webRootAdsPath = CommonTools.getWebRootAbsPath();
        String resultFile = generatePDFResultFileName(fileName);
        htmlFilePath = CommonTools.getAbsPath(htmlFilePath);
        File htmlFile = new File(htmlFilePath);
        String prefixName = htmlFile.getParentFile().getName();
        String foldPath;
        if (!CommonTools.isNullStr(basePath)) {
            basePath = basePath.replace("/", File.separator).replace("\\", File.separator);
            if (CommonTools.isAbsPath(basePath)) {
                log.error("htmlFileToPDF basePath need relative path!");
                return "";
            } else {
                basePath = webRootAdsPath + basePath + File.separator + prefixName;
            }
            foldPath = basePath;
            basePath = webRootAdsPath;
        } else {
            foldPath = htmlFile.getParentFile().getCanonicalPath();
            basePath = foldPath;
        }
        PDFService pdfService = new PDFService();
        String result = pdfService.htmlFileToPDF(htmlFilePath, resultFile, basePath);
        if (isDelete) {
            CommonTools.doDeleteFile(new File(foldPath), false);
            CommonTools.doDeleteFile(htmlFile.getParentFile(), false);
        }
        return result.replace(webRootAdsPath, "").replaceAll("\\\\", "/");
    }

    /**
     * 使用freemarker模板，HTML页面转为PDF
     *
     * @param templatePath 模板路径（绝对路径，或相对于webRoot/files/template）
     * @param data         数据
     * @param fileName     目标文件名称
     * @return 相对于webRoot路径
     */
    public static String htmlToPDFFromTemplate(String templatePath, Map<String, Object> data, String fileName) throws IOException {
        String resultFile = generatePDFResultFileName(fileName);
        PDFService pdfService = new PDFService();
        return pdfService.htmlToPDFForTemplate(resultFile, templatePath, data).replace(CommonTools.getWebRootAbsPath(), "").replaceAll("\\\\", "/");
    }

    /**
     * 图片转PDF
     *
     * @param imageFileNames 图片数组
     * @param resultFileName 目标文件名
     * @param flag           0-自动压缩图片 1-按照宽度压缩图片（所有图片宽度一致）
     * @param isHorizontal   图片是否是横向
     * @param top            上边距（单位磅）
     * @param right          右边距（单位磅）
     * @param bottom         下边距（单位磅）
     * @param left           左边距（单位磅）
     * @return PDF文件绝对路径
     */
    public static String ImageToPDF(String[] imageFileNames, String resultFileName, int flag, boolean isHorizontal, float left, float right, float top, float bottom) throws IOException {
        String resultFile = generatePDFResultFileName(resultFileName);
        PDFService pdfService = new PDFService();
        return pdfService.ImageToPDF(imageFileNames, resultFile, flag, isHorizontal, left, right, top, bottom).replace(CommonTools.getWebRootAbsPath(), "").replaceAll("\\\\", "/");
    }

    /**
     * PDF增加页码
     *
     * @param pdfFilePath    PDF文件路径，绝对路径或相对路径
     * @param resultFileName 目标文件名称
     * @param isDeleteFile   是否删除源文件
     * @param orientation    0-自动 1-纵向 2-横向
     * @return 相对于webRoot路径
     */
    public static String PDFAddPageNumber(String pdfFilePath, String resultFileName, boolean isDeleteFile, int orientation) throws IOException {
        String resultFile = generatePDFResultFileName(resultFileName);
        PDFService pdfService = new PDFService();
        return pdfService.PDFAddPageEvent(pdfFilePath, resultFile, new PageNumberHandle(), isDeleteFile, orientation).replace(CommonTools.getWebRootAbsPath(), "").replaceAll("\\\\", "/");
    }

    /**
     * 给PDF增加水印
     *
     * @param pdfFilePath    pdf源文件
     * @param waterMarkPath  水印图片
     * @param resultFileName 目标文件名
     * @param isDeleteFile   是否删除源文件
     * @return 相对于webRoot路径
     */
    public static String PDFAddWaterMark(String pdfFilePath, String waterMarkPath, String resultFileName, boolean isDeleteFile) throws IOException {
        String resultFile = generatePDFResultFileName(resultFileName);
        PDFService pdfService = new PDFService();
        return pdfService.PDFAddWaterMark(pdfFilePath, waterMarkPath, resultFile, isDeleteFile).replace(CommonTools.getWebRootAbsPath(), "").replaceAll("\\\\", "/");
    }

    /**
     * PDF加密
     *
     * @param pdfFilePath    PDF源文件路径
     * @param resultFileName 目标文件名称
     * @param isDeleteFile   是否删除源文件
     * @return 相对于webRoot路径
     */
    public static String PDFEncrypt(String pdfFilePath, String resultFileName, boolean isDeleteFile) throws IOException {
        String resultFile = generatePDFResultFileName(resultFileName);
        PDFService pdfService = new PDFService();
        return pdfService.PDFEncrypt(pdfFilePath, "", resultFile, true, null, PDFService.PDFOWNERPASSWORD, PermissionType.ALLOW_COPY.getValue() | PermissionType.ALLOW_PRINTING.getValue(), isDeleteFile).replace(CommonTools.getWebRootAbsPath(), "").replaceAll("\\\\", "/");
    }

    /**
     * 合并多个PDF为一个
     *
     * @param fileNames      源文件，绝对路径或相对路径
     * @param resultFileName 目标文件名称
     * @param isDeleteFile   是否删除源文件
     * @return 相对于webRoot路径
     */
    public static String PDFToMerge(String[] fileNames, String resultFileName, boolean isDeleteFile) throws IOException {
        String resultFile = generatePDFResultFileName(resultFileName);
        PDFService pdfService = new PDFService();
        return pdfService.PDFToMerge(fileNames, resultFile, isDeleteFile).replace(CommonTools.getWebRootAbsPath(), "").replaceAll("\\\\", "/");
    }

}
