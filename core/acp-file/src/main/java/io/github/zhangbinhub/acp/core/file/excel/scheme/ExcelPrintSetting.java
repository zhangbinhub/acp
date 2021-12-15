package io.github.zhangbinhub.acp.core.file.excel.scheme;


/**
 * 打印配置类
 * 
 * @author zb
 * 
 */
public final class ExcelPrintSetting {

	private boolean isHorizontal = false;// 是否为横向，默认否

	private int pageWidth = -1;// 页面宽度，单位mm，-1为不设置

	private int pageHeight = -1;// 页面高度，单位mm，-1为不设置

	private double topMargin = 0d;// 顶部边距，单位英寸

	private double bottomMargin = 0d;// 底部边距，单位英寸

	private double leftMargin = 0d;// 左部边距，单位英寸

	private double rightMargin = 0d;// 右部边距，单位英寸

	private Object horizontalCentre = null;// 水平居中 null不设置，true/false

	private Object verticallyCenter = null;// 垂直居中 null不设置，true/false

	private ExcelCellPointSetting printArea = null;// 打印区域 null不设置

	private ExcelCellPointSetting printTitles = null;// 打印标题 null不设置

	public boolean isHorizontal() {
		return isHorizontal;
	}

	public void setHorizontal(boolean isHorizontal) {
		this.isHorizontal = isHorizontal;
	}

	public int getPageWidth() {
		return pageWidth;
	}

	public void setPageWidth(int pageWidth) {
		this.pageWidth = pageWidth;
	}

	public int getPageHeight() {
		return pageHeight;
	}

	public void setPageHeight(int pageHeight) {
		this.pageHeight = pageHeight;
	}

	public double getTopMargin() {
		return topMargin;
	}

	public void setTopMargin(double topMargin) {
		this.topMargin = topMargin;
	}

	public double getBottomMargin() {
		return bottomMargin;
	}

	public void setBottomMargin(double bottomMargin) {
		this.bottomMargin = bottomMargin;
	}

	public double getLeftMargin() {
		return leftMargin;
	}

	public void setLeftMargin(double leftMargin) {
		this.leftMargin = leftMargin;
	}

	public double getRightMargin() {
		return rightMargin;
	}

	public void setRightMargin(double rightMargin) {
		this.rightMargin = rightMargin;
	}

	public Object getHorizontalCentre() {
		return horizontalCentre;
	}

	public void setHorizontalCentre(Object horizontalCentre) {
		this.horizontalCentre = horizontalCentre;
	}

	public Object getVerticallyCenter() {
		return verticallyCenter;
	}

	public void setVerticallyCenter(Object verticallyCenter) {
		this.verticallyCenter = verticallyCenter;
	}

	public ExcelCellPointSetting getPrintArea() {
		return printArea;
	}

	public void setPrintArea(ExcelCellPointSetting printArea) {
		this.printArea = printArea;
	}

	public ExcelCellPointSetting getPrintTitles() {
		return printTitles;
	}

	public void setPrintTitles(ExcelCellPointSetting printTitles) {
		this.printTitles = printTitles;
	}
}
