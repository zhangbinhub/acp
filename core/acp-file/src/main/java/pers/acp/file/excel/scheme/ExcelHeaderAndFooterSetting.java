package pers.acp.file.excel.scheme;

/**
 * @author zhang by 30/08/2019
 * @since JDK 11
 */
public class ExcelHeaderAndFooterSetting {
    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    /**
     * (string)***[pageNumber]***[pageTotal]***
     * 其中[pageNumber]和[pageTotal]是两个占位符，生成Excel时自动用当前页号和总页数替换
     */
    private String left = "";
    /**
     * (string)***[pageNumber]***[pageTotal]***
     * 其中[pageNumber]和[pageTotal]是两个占位符，生成Excel时自动用当前页号和总页数替换
     */
    private String center = "";
    /**
     * (string)***[pageNumber]***[pageTotal]***
     * 其中[pageNumber]和[pageTotal]是两个占位符，生成Excel时自动用当前页号和总页数替换
     */
    private String right = "";
}
