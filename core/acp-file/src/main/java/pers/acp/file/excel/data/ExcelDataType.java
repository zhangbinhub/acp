package pers.acp.file.excel.data;

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

    private String name;

    public String getName() {
        return name;
    }

    private ExcelDataType(String name) {
        this.name = name;
    }

}
