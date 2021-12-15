package com.github.zhangbinhub.acp.core.file.excel.data;

/**
 * @author zhang by 30/08/2019
 * @since JDK 11
 */
public class ExcelCellData {

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public ExcelDataType getDataType() {
        return dataType;
    }

    public void setDataType(ExcelDataType dataType) {
        this.dataType = dataType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    private long index;
    private ExcelDataType dataType;
    private Object value;
}
