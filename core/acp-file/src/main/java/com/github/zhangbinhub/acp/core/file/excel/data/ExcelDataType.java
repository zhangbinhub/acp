package com.github.zhangbinhub.acp.core.file.excel.data;

/**
 * @author zhang by 30/08/2019
 * @since JDK 11
 */
public enum ExcelDataType {

    String("String"),

    Formula("Formula"),

    Number("Number"),

    Boolean("Boolean"),

    Date("Date");

    private final String name;

    public String getName() {
        return name;
    }

    ExcelDataType(String name) {
        this.name = name;
    }

}
