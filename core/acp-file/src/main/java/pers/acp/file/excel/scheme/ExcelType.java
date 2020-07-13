package pers.acp.file.excel.scheme;

public enum ExcelType {

    EXCEL_TYPE_XLS("xls"),

    EXCEL_TYPE_XLSX("xlsx");

    private String name;

    ExcelType(String name) {
        this.name = name.toLowerCase();
    }

    public String getName() {
        return this.name;
    }

}
