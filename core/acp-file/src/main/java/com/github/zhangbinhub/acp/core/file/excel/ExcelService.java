package com.github.zhangbinhub.acp.core.file.excel;

import com.github.zhangbinhub.acp.core.file.excel.scheme.ExcelSheetSetting;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.github.zhangbinhub.acp.core.CommonTools;
import com.github.zhangbinhub.acp.core.log.LogFactory;
import com.github.zhangbinhub.acp.core.file.excel.data.ExcelCellData;
import com.github.zhangbinhub.acp.core.file.excel.data.ExcelDataType;
import com.github.zhangbinhub.acp.core.file.excel.scheme.ExcelDataSetting;
import com.github.zhangbinhub.acp.core.file.excel.scheme.ExcelType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ExcelService {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    private void fillFilePath(File file) {
        CommonTools.doDeleteFile(file, false);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                log.error("mkDirs failed : " + file.getParent());
            }
        }
    }

    /**
     * 通过模板创建Excel文件，替换Excel文件中的变量
     *
     * @param filename     文件绝对路径
     * @param templatePath 模板绝对路径
     * @param data         数据
     * @return Excel文件绝对路径
     */
    public String createExcelFile(String filename, String templatePath, Map<String, String> data) {
        if (CommonTools.isNullStr(filename)) {
            return "";
        }
        if (CommonTools.isNullStr(templatePath)) {
            return "";
        }
        String disFix = CommonTools.getFileExt(filename);
        String tempFix = CommonTools.getFileExt(templatePath);
        if (disFix.equals(tempFix)) {
            Workbook wb;
            try {
                /* 源文件（模板） */
                File sourceFile = new File(templatePath);
                /* 读入源文件 */
                if (tempFix.equals(ExcelType.EXCEL_TYPE_XLSX.getName())) {
                    wb = new XSSFWorkbook(new FileInputStream(sourceFile));
                } else {
                    wb = new HSSFWorkbook(new FileInputStream(sourceFile));
                }
                /* 目标文件 */
                File targetFile = new File(filename);
                CommonTools.doDeleteFile(targetFile, false);
                /* 源文件内容导入目标文件 */
                for (int s = 0; s < wb.getNumberOfSheets(); s++) {
                    Sheet sheet = wb.getSheetAt(s);
                    int rowCount = sheet.getLastRowNum() + 1;
                    for (int i = 0; i < rowCount; i++) {
                        Row row = sheet.getRow(i);
                        if (row != null) {
                            int colCount = row.getLastCellNum() + 1;
                            for (int j = 0; j < colCount; j++) {
                                Cell cell = row.getCell(j);
                                if (cell != null) {
                                    if (!cell.getCellType().equals(CellType.BLANK)) {
                                        cell.setCellValue(CommonTools.replaceVar(cell.getStringCellValue(), data));
                                    }
                                }
                            }
                        }
                    }
                }
                wb.write(new FileOutputStream(targetFile));
                return filename;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return "";
            }
        } else {
            log.error("目标文件格式和模板文件格式不一致！");
            return "";
        }
    }

    /**
     * 通过模板创建Excel文件，替换Excel文件中的变量
     *
     * @param filename         文件绝对路径
     * @param templatePath     模板绝对路径
     * @param firstRowColValue 首行标识
     * @param dataSetting      数据配置
     * @return Excel文件绝对路径
     */
    public String createExcelFile(String filename, String templatePath, String firstRowColValue, ExcelDataSetting dataSetting) {
        if (CommonTools.isNullStr(filename)) {
            return "";
        }
        if (CommonTools.isNullStr(templatePath)) {
            return "";
        }
        String disFix = CommonTools.getFileExt(filename);
        String tempFix = CommonTools.getFileExt(templatePath);
        if (disFix.equals(tempFix)) {
            Workbook wb;
            try {
                /* 源文件（模板） */
                File sourceFile = new File(templatePath);
                /* 读入源文件 */
                if (tempFix.equals(ExcelType.EXCEL_TYPE_XLSX.getName())) {
                    wb = new XSSFWorkbook(new FileInputStream(sourceFile));
                } else {
                    wb = new HSSFWorkbook(new FileInputStream(sourceFile));
                }
                /* 目标文件 */
                File targetFile = new File(filename);
                CommonTools.doDeleteFile(targetFile, false);
                /* 源文件内容导入目标文件 */
                for (int s = 0; s < wb.getNumberOfSheets(); s++) {
                    Sheet sheet = wb.getSheetAt(s);
                    int firstRowIndex = 0;
                    boolean isFindFirstRow = false;
                    int rowCount = sheet.getLastRowNum() + 1;
                    for (int i = 0; i < rowCount; i++) {
                        Row row = sheet.getRow(i);
                        if (row != null) {
                            int colCount = row.getLastCellNum() + 1;
                            for (int j = 0; j < colCount; j++) {
                                Cell cell = row.getCell(j);
                                if (cell != null) {
                                    if (!cell.getCellType().equals(CellType.BLANK)) {
                                        String value = cell.getStringCellValue();
                                        if (value.equals(firstRowColValue)) {
                                            firstRowIndex = i;
                                            isFindFirstRow = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (isFindFirstRow) {
                                break;
                            }
                        }
                    }
                    String[] names = StringUtils.splitPreserveAllTokens(dataSetting.getNames(), ",");
                    CellStyle[] cellStyles = new CellStyle[names.length];
                    List<Map<String, String>> dataList = dataSetting.getDataList();
                    for (int rowIndex = firstRowIndex; rowIndex < dataList.size() + firstRowIndex; rowIndex++) {
                        Map<String, String> dataMap = dataList.get(rowIndex - firstRowIndex);
                        Row row = sheet.getRow(rowIndex);
                        if (rowIndex != firstRowIndex) {
                            row = sheet.createRow(rowIndex);
                        }
                        for (int cellIndex = 0; cellIndex < names.length; cellIndex++) {
                            Cell cell = row.getCell(cellIndex);
                            if (rowIndex != firstRowIndex) {
                                cell = row.createCell(cellIndex);
                                cell.setCellStyle(cellStyles[cellIndex]);
                            } else {
                                if (cell == null) {
                                    cell = row.createCell(cellIndex);
                                    cell.setCellStyle(cellStyles[cellIndex - 1]);
                                } else {
                                    cellStyles[cellIndex] = cell.getCellStyle();
                                }
                            }
                            cell.setCellValue(dataMap.getOrDefault(names[cellIndex], ""));
                        }
                    }
                }
                wb.write(new FileOutputStream(targetFile));
                return filename;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return "";
            }
        } else {
            log.error("目标文件格式和模板文件格式不一致！");
            return "";
        }
    }

    /**
     * 创建Excel文件
     *
     * @param filename         文件绝对路径
     * @param isHorizontal     是否是横向模式
     * @param excelDataSetting 数据配置
     * @return Excel文件绝对路径
     */
    public String createExcelFile(String filename, boolean isHorizontal, ExcelDataSetting excelDataSetting) {
        if (CommonTools.isNullStr(filename)) {
            return "";
        }
        String fileType = CommonTools.getFileExt(filename);
        File file = new File(filename);
        Workbook wb;
        try {
            fillFilePath(file);
            if (file.createNewFile()) {
                wb = getWorkbook(fileType);
                if (wb == null) {
                    return "";
                }
                Sheet sheet = wb.createSheet("sheet1");
                /* 创建一个工作表 ****/
                PrintSetup printSetup = sheet.getPrintSetup();
                printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
                printSetup.setLandscape(isHorizontal);
                SheetData sheetData = new SheetData();
                sheetData.generateSheetDataByDataSetting(wb, sheet, excelDataSetting);
                wb.write(new FileOutputStream(file));
                return filename;
            } else {
                return "";
            }
        } catch (Exception e) {
            log.error("generate Excel Exception:" + e.getMessage(), e);
            CommonTools.doDeleteFile(file, false);
            return "";
        }
    }

    /**
     * 创建Excel文件
     *
     * @param fileName              文件绝对路径
     * @param excelSheetSettingList 配置信息
     * @return Excel文件绝对路径
     */
    public String createExcelFile(String fileName, List<ExcelSheetSetting> excelSheetSettingList) {
        SheetData sheetData = new SheetData();
        String fileType = CommonTools.getFileExt(fileName);
        Workbook wb;
        if (CommonTools.isNullStr(fileName)) {
            return "";
        }
        File file = new File(fileName);
        try {
            fillFilePath(file);
            if (file.createNewFile()) {
                wb = getWorkbook(fileType);
                if (wb == null) {
                    return "";
                }
                /* 循环生成sheet ****/
                for (int i = 0; i < excelSheetSettingList.size(); i++) {
                    ExcelSheetSetting sheetSetting = excelSheetSettingList.get(i);
                    String sheetName = "sheet" + i;
                    if (sheetData.validationConfig(sheetSetting, 0)) {
                        sheetName = sheetSetting.getSheetName();
                    }
                    /* 创建sheet **/
                    Sheet sheet = wb.createSheet(sheetName);
                    /* 生成sheet内数据 **/
                    sheet = sheetData.generateSheetData(wb, sheet, sheetSetting);
                    /* 设置sheet页眉 **/
                    sheetData.generateSheetHeader(sheet, sheetSetting);
                    /* 设置sheet页脚 **/
                    sheetData.generateSheetFooter(sheet, sheetSetting);
                    /* 设置sheet合并单元格 **/
                    sheetData.generateSheetMerge(sheet, sheetSetting);
                    /* 设置sheet打印配置 **/
                    sheetData.generateSheetPrintSetting(wb, i, sheet, sheetSetting);
                    /* 设置sheet窗口冻结 **/
                    sheetData.generateSheetFreeze(sheet, sheetSetting);
                }
                wb.write(new FileOutputStream(file));
                return fileName;
            } else {
                return "";
            }
        } catch (Exception e) {
            log.error("generate Excel Exception:" + e.getMessage());
            CommonTools.doDeleteFile(file, false);
            return "";
        }
    }

    /**
     * 打开excel工作簿
     *
     * @param filePath 文件路径
     * @return 工作簿对象
     */
    public Workbook openWorkboot(String filePath) {
        File excelFile = new File(filePath);
        String fileName = excelFile.getName();
        String prefix = CommonTools.getFileExt(fileName);
        if (prefix.equals(ExcelType.EXCEL_TYPE_XLSX.getName()) || prefix.equals(ExcelType.EXCEL_TYPE_XLS.getName())) {
            try {
                if (prefix.equals(ExcelType.EXCEL_TYPE_XLSX.getName())) {
                    return new XSSFWorkbook(new FileInputStream(excelFile));
                } else {
                    return new HSSFWorkbook(new FileInputStream(excelFile));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 读取excel文件
     *
     * @param filePath 文件路径
     * @param sheetNo  工作表序号，从0开始
     * @param beginRow 读取的起始行，从0开始
     * @param beginCol 读取的起始列，从0开始
     * @param rowNo    读取的行数，0则表示读取全部
     * @param colNo    读取的列数，0则表示读取全部
     * @param isDelete 是否读取完数据后删除文件
     * @return 结果集
     */
    public List<List<ExcelCellData>> readExcelData(String filePath, int sheetNo, int beginRow, int beginCol, int rowNo, int colNo, boolean isDelete) {
        List<List<ExcelCellData>> result = readExcelData(openWorkboot(filePath), sheetNo, beginRow, beginCol, rowNo, colNo);
        if (isDelete) {
            CommonTools.doDeleteFile(new File(filePath), false);
        }
        return result;
    }


    /**
     * 读取excel文件
     *
     * @param workbook 工作簿对象
     * @param sheetNo  工作表序号，从0开始
     * @param beginRow 读取的起始行，从0开始
     * @param beginCol 读取的起始列，从0开始
     * @param rowNo    读取的行数，0则表示读取全部
     * @param colNo    读取的列数，0则表示读取全部
     * @return 结果集
     */
    public List<List<ExcelCellData>> readExcelData(Workbook workbook, int sheetNo, int beginRow, int beginCol, int rowNo, int colNo) {
        List<List<ExcelCellData>> result = new ArrayList<>();
        try {
            Sheet sheet = workbook.getSheetAt(sheetNo);
            int lastRowNum = sheet.getLastRowNum() + 1;
            int rowCount = lastRowNum - beginRow;
            if (rowNo > 0 && rowCount > rowNo) {
                rowCount = rowNo;
            }
            int colCont = 0;
            for (int i = sheet.getFirstRowNum(); i < lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    int cellCount = row.getLastCellNum();
                    if (cellCount >= colCont) {
                        colCont = cellCount;
                    }
                }
            }
            colCont = colCont + 1 - beginCol;
            if (colNo > 0 && colCont > colNo) {
                colCont = colNo;
            }
            for (int i = beginRow; i < beginRow + rowCount; i++) {
                List<ExcelCellData> rowData = new ArrayList<>();
                Row row = sheet.getRow(i);
                for (int j = beginCol; j < colCont; j++) {
                    if (row == null) {
                        continue;
                    }
                    ExcelCellData cellData = new ExcelCellData();
                    cellData.setIndex(j);
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                cellData.setDataType(ExcelDataType.String);
                                cellData.setValue(cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    cellData.setDataType(ExcelDataType.Date);
                                    cellData.setValue(DateUtil.getJavaDate(cell.getNumericCellValue()).getTime());
                                } else {
                                    cellData.setDataType(ExcelDataType.Number);
                                    cellData.setValue(cell.getNumericCellValue());
                                }
                                break;
                            case BOOLEAN:
                                cellData.setDataType(ExcelDataType.Boolean);
                                cellData.setValue(cell.getBooleanCellValue());
                                break;
                            case FORMULA:
                                cellData.setDataType(ExcelDataType.Formula);
                                cellData.setValue(cell.getCellFormula());
                                break;
                            default:
                                cellData.setDataType(ExcelDataType.String);
                                cellData.setValue("");
                                break;
                        }
                    } else {
                        cellData.setDataType(ExcelDataType.String);
                        cellData.setValue("");
                    }
                    rowData.add(cellData);
                }
                result.add(rowData);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = new ArrayList<>();
        }
        return result;
    }

    private Workbook getWorkbook(String fileType) {
        Workbook wb = null;
        if (fileType.equals(ExcelType.EXCEL_TYPE_XLS.getName())) {
            wb = new HSSFWorkbook();
        } else if (fileType.equals(ExcelType.EXCEL_TYPE_XLSX.getName())) {
            wb = new XSSFWorkbook();
        } else {
            log.error("file type [" + fileType + "] is not support! ");
        }
        return wb;
    }

}
