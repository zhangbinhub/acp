package io.github.zhangbinhub.acp.core.file.excel.scheme;

/**
 * 起始单元格坐标类
 * 
 * @author zb
 * 
 */
public final class ExcelCellPointSetting {

	private int firstCol = -1;

	private int firstRow = -1;

	private int lastCol = -1;

	private int lastRow = -1;

	public int getFirstCol() {
		return firstCol;
	}

	public void setFirstCol(int firstCol) {
		this.firstCol = firstCol;
	}

	public int getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}

	public int getLastCol() {
		return lastCol;
	}

	public void setLastCol(int lastCol) {
		this.lastCol = lastCol;
	}

	public int getLastRow() {
		return lastRow;
	}

	public void setLastRow(int lastRow) {
		this.lastRow = lastRow;
	}
}
