package com.github.zhangbinhub.acp.core.file.pdf;

import com.github.zhangbinhub.acp.core.file.FileOperation;
import com.github.zhangbinhub.acp.core.file.pdf.fonts.FontLoader;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.*;

import org.xhtmlrenderer.pdf.ITextRenderer;
import com.github.zhangbinhub.acp.core.file.templete.TemplateService;
import com.github.zhangbinhub.acp.core.CommonTools;
import com.github.zhangbinhub.acp.core.log.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

public final class PDFService {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    /**
     * 拥有者密码
     */
    public static String PDFOWNERPASSWORD = "zb";

    /**
     * HTML页面转为PDF
     *
     * @param htmlstr  html页面源码,必须完整
     * @param fileName 文件名
     * @param basePath 图片basePath
     * @return PDF文件绝对路径
     */
    public String htmlToPDF(String htmlstr, String fileName, String basePath) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(fileName);
            ITextRenderer renderer = FontLoader.loadFonts(new ITextRenderer());
            String htmlString = HtmlParse.parseHTML(htmlstr);
            renderer.setDocumentFromString(htmlString);
            String baseUrl = FileOperation.getFileBaseURL(basePath);
            renderer.getSharedContext().setBaseURL(baseUrl);
            renderer.layout();
            renderer.createPDF(os);
            os.close();
            return fileName;
        } catch (Exception e) {
            log.error("generate PDF Exception:" + e.getMessage());
            try {
                if (os != null) {
                    os.close();
                }
                File file = new File(fileName);
                CommonTools.doDeleteFile(file, false);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
            return "";
        }
    }

    /**
     * HTML页面转为PDF
     *
     * @param htmlFilePath html文件绝对路径
     * @param fileName     文件名
     * @param basePath     图片文件所在路径
     * @return PDF文件绝对路径
     */
    public String htmlFileToPDF(String htmlFilePath, String fileName, String basePath) {
        return htmlToPDF(CommonTools.getFileContent(htmlFilePath), fileName, basePath);
    }

    /**
     * HTML页面转为PDF
     *
     * @param fileName     文件名
     * @param templatePath 模板文件路径
     * @param data         填充模板数据项
     * @return PDF文件绝对路径
     */
    public String htmlToPDFForTemplate(String fileName, String templatePath, Map<String, Object> data) {
        String htmlstr = TemplateService.generateString(templatePath, data);
        return htmlToPDF(htmlstr, fileName, null);
    }

    /**
     * 图片转PDF,边距单位（磅）
     *
     * @param imageFileNames 图片文件，绝对路径或相对路径
     * @param resultFile     结果文件绝对路径
     * @param flag           0-方案一,1-方案二
     * @param isHorizontal   是否水平
     * @param left           左边距单位（磅）
     * @param right          右边距单位（磅）
     * @param top            上边距单位（磅）
     * @param bottom         下边距单位（磅）
     * @return PDF文件绝对路径
     */
    public String ImageToPDF(String[] imageFileNames, String resultFile, int flag, boolean isHorizontal, float left, float right, float top, float bottom) {
        if (imageFileNames.length > 0) {
            Document doc;
            if (isHorizontal) {
                doc = new Document(PageSize.A4.rotate(), left, right, top,
                        bottom);
            } else {
                doc = new Document(PageSize.A4, left, right, top, bottom);
            }
            try {
                PdfWriter.getInstance(doc, new FileOutputStream(resultFile));
                doc.open();
                for (String imageFileName : imageFileNames) {
                    doc.newPage();
                    String imageFilePath = CommonTools.getAbsPath(imageFileName);
                    Image image = Image.getInstance(imageFilePath);
                    float dpi = 72;// itext 中dpi为72px/英寸
                    float heigth = image.getHeight() / dpi * 25.4f;
                    float width = image.getWidth() / dpi * 25.4f;
                    int percent;
                    if (flag == 0) {
                        percent = getPercent(heigth, width, isHorizontal);
                    } else if (flag == 1) {
                        percent = getPercent2(width, isHorizontal);
                    } else {
                        log.error("image to PDF failed:don't distinguish compression param");
                        return "";
                    }
                    image.setAlignment(Image.MIDDLE);
                    image.scalePercent(percent);
                    doc.add(image);
                }
                doc.close();
                return resultFile;
            } catch (Exception e) {
                doc.close();
                log.error("image to PDF Exception:" + e.getMessage(), e);
                return "";
            }
        } else {
            return "";
        }
    }

    /**
     * PDF增加页面事件
     *
     * @param pdfFilePath    PDF文件路径，绝对路径或相对路径
     * @param resultFileName 目标文件绝对路径
     * @param event          事件类
     * @param isDeleteFile   是否删除源文件
     * @param orientation    0-自动 1-纵向 2-横向
     * @return PDF文件绝对路径
     */
    public String PDFAddPageEvent(String pdfFilePath, String resultFileName, PdfPageEventHelper event, boolean isDeleteFile, int orientation) {
        Document document = null;
        try {
            /* 重构文件路径 start **/
            pdfFilePath = CommonTools.getAbsPath(pdfFilePath);
            /* 重构文件路径 end **/
            PdfReader reader = new PdfReader(pdfFilePath, "PDF".getBytes());
            if (orientation == 0 || orientation == 1) {
                document = new Document();
            } else {
                document = new Document(PageSize.A4.rotate());
            }
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(resultFileName));
            writer.setPageEvent(event);
            document.open();
            PdfImportedPage page;
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                page = writer.getImportedPage(reader, i);
                Image image = Image.getInstance(page);
                int readWidth = (int) reader.getPageSize(i).getWidth();
                int writeWidth = (int) writer.getPageSize().getWidth();
                if (orientation == 0 || orientation == 1) {
                    if (writeWidth + 10 < readWidth)
                        image.setRotationDegrees(90);
                } else {
                    if (writeWidth > readWidth + 10)
                        image.setRotationDegrees(90);
                }
                image.setAbsolutePosition(0, 0);
                document.add(image);
                document.newPage();
            }
            document.close();
            if (isDeleteFile) {
                CommonTools.doDeleteFile(new File(pdfFilePath), false);
            }
            log.info("add page number success");
            return resultFileName;
        } catch (Exception e) {
            try {
                if (document != null) {
                    document.close();
                }
            } catch (Exception ex) {
                log.error("add page number Exception:" + ex.getMessage(), ex);
            }
            log.error("add page number Exception:" + e.getMessage(), e);
            return "";
        }
    }

    /**
     * 给PDF增加水印
     *
     * @param pdfFilePath    pdf源文件
     * @param waterMarkPath  水印图片
     * @param resultFileName 目标文件名
     * @param isDeleteFile   是否删除源文件
     * @return PDF文件绝对路径
     */
    public String PDFAddWaterMark(String pdfFilePath, String waterMarkPath, String resultFileName, boolean isDeleteFile) {
        return PDFEncrypt(pdfFilePath, waterMarkPath, resultFileName, true,
                null, PDFOWNERPASSWORD, PdfWriter.ALLOW_COPY | PdfWriter.ALLOW_PRINTING, isDeleteFile);
    }

    /**
     * PDF加密
     *
     * @param pdfFilePath    pdf源文件
     * @param waterMarkPath  水印图片路径,为空则表示不增加水印
     * @param resultFileName 目标文件名
     * @param isEncrypt      是否进行文档加密
     * @param userPassword   打开文档时需输入的访问密码
     * @param ownerPassword  文档的设置许可密码
     * @param permissions    允许的权限
     * @param isDeleteFile   是否删除源文件
     * @return PDF文件绝对路径
     */
    public String PDFEncrypt(String pdfFilePath, String waterMarkPath, String resultFileName, boolean isEncrypt, String userPassword, String ownerPassword, int permissions, boolean isDeleteFile) {
        PdfStamper stamp = null;
        Image img = null;
        try {
            /* 重构文件路径 start **/
            pdfFilePath = CommonTools.getAbsPath(pdfFilePath);
            if (!CommonTools.isNullStr(waterMarkPath)) {
                waterMarkPath = CommonTools.getAbsPath(waterMarkPath);
                /* 获得水印对象实例 **/
                img = Image.getInstance(waterMarkPath);
            }
            /* 重构文件路径 end **/
            PdfReader reader = new PdfReader(pdfFilePath, "PDF".getBytes());
            stamp = new PdfStamper(reader, new FileOutputStream(resultFileName));
            if (isEncrypt) {
                /* 加密PDF,只允许查看和复制 **/
                stamp.setEncryption(PdfWriter.STANDARD_ENCRYPTION_40,
                        userPassword, ownerPassword, permissions);
            }
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                if (img != null) {
                    PdfContentByte under = stamp.getUnderContent(i);
                    int width = (int) reader.getPageSize(i).getWidth();
                    /* 设置水印图片插入的绝对位置 **/
                    img.setAbsolutePosition(0, 0);
                    if (width > (int) img.getWidth()) {
                        /* 旋转水印图片 **/
                        img.setRotationDegrees(-90);
                        /* 插入水印 **/
                        under.addImage(img);
                        img.setRotationDegrees(0);
                    } else {
                        under.addImage(img);
                    }
                }
            }
            stamp.close();// 关闭
            if (isDeleteFile) {
                CommonTools.doDeleteFile(new File(pdfFilePath), false);
            }
            log.info("PDF encrypt success");
            return resultFileName;
        } catch (Exception e) {
            try {
                if (stamp != null) {
                    stamp.close();// 关闭
                }
            } catch (Exception ex) {
                log.error("PDF encrypt Exception:" + ex.getMessage(), ex);
            }
            log.error("PDF encrypt Exception:" + e.getMessage(), e);
            return "";
        }
    }

    /**
     * 合并多个PDF为一个
     *
     * @param fileNames      源文件，绝对路径或相对路径
     * @param resultFileName 目标文件绝对路径
     * @param isDeleteFile   是否删除源文件
     * @return PDF文件绝对路径
     */
    public String PDFToMerge(String[] fileNames, String resultFileName, boolean isDeleteFile) {
        if (fileNames.length > 0) {
            Document document = null;
            try {
                String filename = CommonTools.getAbsPath(fileNames[0]);
                document = new Document(new PdfReader(filename).getPageSize(1));
                PdfCopy copy = new PdfCopy(document, new FileOutputStream(
                        resultFileName));
                document.open();
                for (String fileName : fileNames) {
                    String file = CommonTools.getAbsPath(fileName);
                    PdfReader reader = new PdfReader(file);
                    int n = reader.getNumberOfPages();
                    for (int j = 1; j <= n; j++) {
                        document.newPage();
                        PdfImportedPage page = copy.getImportedPage(reader, j);
                        copy.addPage(page);
                    }
                    if (isDeleteFile) {
                        CommonTools.doDeleteFile(new File(file), false);
                    }
                }
                document.close();
                log.info("merge PDF success");
                return resultFileName;
            } catch (Exception e) {
                if (document != null) {
                    document.close();
                }
                log.error("merge PDF Exception:" + e.getMessage(), e);
                return "";
            }
        } else {
            return "";
        }
    }

    /**
     * 第一种解决方案 在不改变图片形状的同时,判断,如果h>w,则按h压缩,否则在w>h或w=h的情况下,按宽度压缩
     *
     * @param h 高
     * @param w 宽
     * @return 结果
     */
    private int getPercent(float h, float w, boolean isHorizontal) {
        int p;
        float p2;
        if (isHorizontal) {
            if (h > w) {
                p2 = 210 / h * 100;
            } else {
                p2 = 297 / w * 100;
            }
        } else {
            if (h > w) {
                p2 = 297 / h * 100;
            } else {
                p2 = 210 / w * 100;
            }
        }
        p = Math.round(p2);
        return p;
    }

    /**
     * 第二种解决方案,统一按照宽度压缩 这样来的效果是,所有图片的宽度是相等的
     *
     * @param w 宽
     * @return 结果
     */
    private int getPercent2(float w, boolean isHorizontal) {
        int p;
        float p2;
        if (isHorizontal) {
            p2 = 297 / w * 100;
        } else {
            p2 = 210 / w * 100;
        }
        p = Math.round(p2);
        return p;
    }
}
