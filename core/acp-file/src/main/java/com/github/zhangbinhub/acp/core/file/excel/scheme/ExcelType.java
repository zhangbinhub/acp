package com.github.zhangbinhub.acp.core.file.excel.scheme;

public enum ExcelType {

    EXCEL_TYPE_XLS("xls"),

    EXCEL_TYPE_XLSX("xlsx");

    private final String name;

    ExcelType(String name) {
        this.name = name.toLowerCase();
    }

    public String getName() {
        return this.name;
    }

}
