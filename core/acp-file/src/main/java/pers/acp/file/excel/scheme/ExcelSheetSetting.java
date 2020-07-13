package pers.acp.file.excel.scheme;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 30/08/2019
 * @since JDK 11
 */
public class ExcelSheetSetting {
    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public ExcelPrintSetting getPrintSetting() {
        return printSetting;
    }

    public void setPrintSetting(ExcelPrintSetting printSetting) {
        this.printSetting = printSetting;
    }

    public ExcelHeaderAndFooterSetting getHeader() {
        return header;
    }

    public void setHeader(ExcelHeaderAndFooterSetting header) {
        this.header = header;
    }

    public ExcelHeaderAndFooterSetting getFooter() {
        return footer;
    }

    public void setFooter(ExcelHeaderAndFooterSetting footer) {
        this.footer = footer;
    }

    public ExcelDataSetting getData() {
        return data;
    }

    public void setData(ExcelDataSetting data) {
        this.data = data;
    }

    public List<ExcelCellPointSetting> getMergeCells() {
        return mergeCells;
    }

    public void setMergeCells(List<ExcelCellPointSetting> mergeCells) {
        this.mergeCells = mergeCells;
    }

    public ExcelFreezeSetting getFreeze() {
        return freeze;
    }

    public void setFreeze(ExcelFreezeSetting freeze) {
        this.freeze = freeze;
    }

    private String sheetName;
    private ExcelPrintSetting printSetting;
    private ExcelHeaderAndFooterSetting header;
    private ExcelHeaderAndFooterSetting footer;
    private ExcelDataSetting data;
    private List<ExcelCellPointSetting> mergeCells = new ArrayList<>();
    private ExcelFreezeSetting freeze;
}
