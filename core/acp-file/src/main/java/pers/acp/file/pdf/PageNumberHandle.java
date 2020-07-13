package pers.acp.file.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.pdf.*;
import pers.acp.file.pdf.fonts.FontLoader;

public class PageNumberHandle extends PdfPageEventHelper {

    private float fontSize = 8;

    private float currNumX = 450;

    private float currNumY = 20;

    private float totalNumX = 497;

    private float totalNumY = 20;

    private String currNumStr = "第 [pageNumber] 页 / 共 ";

    private String totalNumStr = "[pageTotal] 页";

    private BaseFont bf = FontLoader.getSIMSUNFont();

    private PdfTemplate tpl;

    public void onOpenDocument(PdfWriter writer, Document document) {
        try {
            tpl = writer.getDirectContent().createTemplate(100, 100);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public void onEndPage(PdfWriter writer, Document document) {
        int currNumLen = String.valueOf(writer.getPageNumber()).length();
        PdfContentByte cb = writer.getDirectContent();
        cb.saveState();
        String text = currNumStr.replace("[pageNumber]",
                Integer.toString(writer.getPageNumber()));
        cb.beginText();
        cb.setFontAndSize(bf, fontSize);
        cb.setTextMatrix(currNumX, currNumY);
        cb.showText(text);
        cb.endText();
        cb.addTemplate(tpl, totalNumX + currNumLen * 5, totalNumY);
        cb.stroke();
        cb.restoreState();
        cb.closePath();
    }

    public void onCloseDocument(PdfWriter writer, Document document) {
        tpl.beginText();
        tpl.setFontAndSize(bf, fontSize);
        tpl.showText(totalNumStr.replace("[pageTotal]",
                Integer.toString(writer.getPageNumber() - 1)));
        tpl.endText();
        tpl.closePath();
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public float getCurrNumX() {
        return currNumX;
    }

    public void setCurrNumX(float currNumX) {
        this.currNumX = currNumX;
    }

    public float getCurrNumY() {
        return currNumY;
    }

    public void setCurrNumY(float currNumY) {
        this.currNumY = currNumY;
    }

    public float getTotalNumX() {
        return totalNumX;
    }

    public void setTotalNumX(float totalNumX) {
        this.totalNumX = totalNumX;
    }

    public float getTotalNumY() {
        return totalNumY;
    }

    public void setTotalNumY(float totalNumY) {
        this.totalNumY = totalNumY;
    }

    public String getCurrNumStr() {
        return currNumStr;
    }

    /**
     * 当前页码字符串，变量名“[pageNumber]”
     *
     * @param currNumStr 当前页数
     */
    public void setCurrNumStr(String currNumStr) {
        this.currNumStr = currNumStr;
    }

    public String getTotalNumStr() {
        return totalNumStr;
    }

    /**
     * 总页数字符串，变量名“[pageTotal]”
     *
     * @param totalNumStr 总页数
     */
    public void setTotalNumStr(String totalNumStr) {
        this.totalNumStr = totalNumStr;
    }

    public BaseFont getBf() {
        return bf;
    }

    /**
     * 字体
     *
     * @param bf 字体对象
     */
    public void setBf(BaseFont bf) {
        this.bf = bf;
    }
}
