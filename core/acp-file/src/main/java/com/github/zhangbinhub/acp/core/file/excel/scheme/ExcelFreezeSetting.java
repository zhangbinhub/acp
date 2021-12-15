package com.github.zhangbinhub.acp.core.file.excel.scheme;

/**
 * @author zhang by 30/08/2019
 * @since JDK 11
 */
public class ExcelFreezeSetting {
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    private int row = 0;
    private int col = 0;
}
