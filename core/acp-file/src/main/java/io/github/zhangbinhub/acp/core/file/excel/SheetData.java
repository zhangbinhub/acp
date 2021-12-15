package io.github.zhangbinhub.acp.core.file.excel;

import io.github.zhangbinhub.acp.core.file.excel.scheme.*;
import io.github.zhangbinhub.acp.core.file.excel.scheme.*;
import io.github.zhangbinhub.acp.core.log.LogFactory;
import org.apache.commons.lang3.StringUtils;

import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import io.github.zhangbinhub.acp.core.file.excel.scheme.*;
import io.github.zhangbinhub.acp.core.CommonTools;
import io.github.zhangbinhub.acp.core.log.LogFactory;

import java.util.List;
import java.util.Map;

final class SheetData {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    private static final short FONT_SIZE = 11;

    /**
     * 生成sheet内数据
     *
     * @param wb           工作簿对象
     * @param sheet        sheet对象
     * @param sheetSetting sheet配置
     * @return sheet对象
     */
    Sheet generateSheetData(Workbook wb, Sheet sheet, ExcelSheetSetting sheetSetting) throws Exception {
        if (validationConfig(sheetSetting, 1)) {
            return generateSheetDataByDataSetting(wb, sheet, sheetSetting.getData());
        } else {
            throw new Exception("config is not complete!");
        }
    }

    /**
     * 生成sheet内数据
     *
     * @param wb          工作簿对象
     * @param sheet       需要填充数据的sheet对象
     * @param dataSetting 数据
     * @return sheet对象
     */
    Sheet generateSheetDataByDataSetting(Workbook wb, Sheet sheet, ExcelDataSetting dataSetting) {
        int rowIndex = dataSetting.getDefaultRowIndex();
        int cellIndex = dataSetting.getDefaultCellIndex();
        /* 插入标题 start ****/
        List<Map<String, String>> titleCtrl = dataSetting.getTitleCtrl();
        for (Map<String, String> titleMap : titleCtrl) {
            String title = titleMap.getOrDefault("value", "");
            String colConfig = titleMap.getOrDefault("col", "");
            if (!CommonTools.isNullStr(colConfig)) {
                cellIndex = Integer.parseInt(colConfig);
            }
            String rowConfig = titleMap.getOrDefault("row", "");
            if (!CommonTools.isNullStr(rowConfig)) {
                rowIndex = Integer.parseInt(rowConfig);
            }
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }
            Cell cell = row.createCell(cellIndex);
            CellStyle cellStyle = createStyle(wb, titleMap, 0);// 标题样式
            cell.setCellStyle(cellStyle);
            cell.setCellValue(title);

            String heightConfig = titleMap.getOrDefault("height", "");
            if (!CommonTools.isNullStr(heightConfig)) {
                row.setHeightInPoints(Integer.parseInt(heightConfig));
            }
            String widthConfig = titleMap.getOrDefault("width", "");
            if (!CommonTools.isNullStr(widthConfig)) {
                sheet.setColumnWidth(cellIndex, Integer.parseInt(widthConfig) * 256);
            }
            String colspanConfig = titleMap.getOrDefault("colspan", "");
            String rowspanConfig = titleMap.getOrDefault("rowspan", "");
            if (!CommonTools.isNullStr(colspanConfig) && !CommonTools.isNullStr(rowspanConfig)) {// 合并单元格
                sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex + Integer.parseInt(rowspanConfig) - 1, cellIndex, cellIndex + Integer.parseInt(colspanConfig) - 1));
            } else if (!CommonTools.isNullStr(colspanConfig)) {
                sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, cellIndex, cellIndex + Integer.parseInt(colspanConfig) - 1));
            } else if (!CommonTools.isNullStr(rowspanConfig)) {
                sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex + Integer.parseInt(rowspanConfig) - 1, cellIndex, cellIndex));
            }
            if (!CommonTools.isNullStr(rowspanConfig)) {
                rowIndex += Integer.parseInt(rowspanConfig);
            } else {
                rowIndex++;
            }
        }
        /* 插入标题 end ****/

        /* 填充数据的内容 start ****/
        cellIndex = dataSetting.getDefaultCellIndex();
        if (dataSetting.isShowBodyHead()) {
            if (dataSetting.getBodyCtrl().size() == 0) {
                rowIndex = createBodyHeadByName(wb, dataSetting.getNames(), rowIndex, cellIndex, sheet);
            } else {
                rowIndex = createBodyHead(wb, dataSetting.getBodyCtrl(), rowIndex, cellIndex, sheet);
            }
        }
        List<Map<String, String>> bodyCtrl = dataSetting.getBodyCtrl();
        for (int i = 0; i < dataSetting.getDataList().size(); i++) {
            Map<String, String> rowData = dataSetting.getDataList().get(i);
            int index = cellIndex;
            String[] name = StringUtils.splitPreserveAllTokens(dataSetting.getNames(), ",");
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }
            for (String aName : name) {
                Cell cell = row.createCell(index);
                CellStyle cellStyle;
                Map<String, String> bodyMap = bodyCtrl.get(index - cellIndex);
                cellStyle = createStyle(wb, bodyMap, 1);// 标题样式
                String widthConfig = bodyMap.getOrDefault("width", "");
                if (!CommonTools.isNullStr(widthConfig)) {
                    sheet.setColumnWidth(index, Integer.parseInt(widthConfig) * 256);
                }
                String heightConfig = bodyMap.getOrDefault("height", "");
                if (!CommonTools.isNullStr(heightConfig)) {
                    row.setHeightInPoints(Integer.parseInt(heightConfig));
                }
                cell.setCellStyle(cellStyle);
                cell.setCellValue(rowData.getOrDefault(aName, ""));
                index++;
            }
            rowIndex++;
        }
        /* 填充数据的内容 end ****/

        /* 插入脚部数据 start ****/
        cellIndex = dataSetting.getDefaultCellIndex();
        List<Map<String, String>> footCtrl = dataSetting.getFootCtrl();
        for (Map<String, String> footMap : footCtrl) {
            String foot = footMap.getOrDefault("value", "");
            String rowConfig = footMap.getOrDefault("row", "");
            if (!CommonTools.isNullStr(rowConfig)) {
                rowIndex = Integer.parseInt(rowConfig);
            }
            String colConfig = footMap.getOrDefault("col", "");
            if (!CommonTools.isNullStr(colConfig)) {
                cellIndex = Integer.parseInt(colConfig);
            }
            String paddingRowConfig = footMap.getOrDefault("paddingRow", "");
            int paddingrow = 0;
            if (!CommonTools.isNullStr(paddingRowConfig)) {
                paddingrow = Integer.parseInt(paddingRowConfig);
            }
            String widthConfig = footMap.getOrDefault("width", "");
            if (!CommonTools.isNullStr(widthConfig)) {
                sheet.setColumnWidth(cellIndex, Integer.parseInt(widthConfig) * 256);
            }
            Row row = sheet.getRow(rowIndex + paddingrow);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }
            String heightConfig = footMap.getOrDefault("height", "");
            if (!CommonTools.isNullStr(heightConfig)) {
                row.setHeightInPoints(Integer.parseInt(heightConfig));
            }
            CellStyle cellStyle = createStyle(wb, footMap, 2);// 页脚样式
            String colspanConfig = footMap.getOrDefault("colspan", "");
            String rowspanConfig = footMap.getOrDefault("rowspan", "");
            if (!CommonTools.isNullStr(colspanConfig) && !CommonTools.isNullStr(rowspanConfig)) {// 合并单元格
                sheet.addMergedRegion(new CellRangeAddress(rowIndex + paddingrow, rowIndex + paddingrow + Integer.parseInt(rowspanConfig) - 1, cellIndex, cellIndex + Integer.parseInt(colspanConfig) - 1));
            } else if (!CommonTools.isNullStr(colspanConfig)) {
                sheet.addMergedRegion(new CellRangeAddress(rowIndex + paddingrow, rowIndex + paddingrow, cellIndex, cellIndex + Integer.parseInt(colspanConfig) - 1));
            } else if (!CommonTools.isNullStr(rowspanConfig)) {
                sheet.addMergedRegion(new CellRangeAddress(rowIndex + paddingrow, rowIndex + paddingrow + Integer.parseInt(rowspanConfig) - 1, cellIndex, cellIndex));
            }
            Cell cell = row.createCell(cellIndex);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(foot);
        }
        /* 插入页脚 end ****/
        return sheet;
    }

    /**
     * 生成sheet页眉
     *
     * @param sheet        sheet对象
     * @param sheetSetting sheet配置
     */
    void generateSheetHeader(Sheet sheet, ExcelSheetSetting sheetSetting) {
        if (validationConfig(sheetSetting, 2)) {
            ExcelHeaderAndFooterSetting headerAndFooterSetting = sheetSetting.getHeader();
            Header head = sheet.getHeader();
            if (!CommonTools.isNullStr(headerAndFooterSetting.getCenter())) {
                buildHeadFooter(head, headerAndFooterSetting.getCenter(), 1);
            }
            if (!CommonTools.isNullStr(headerAndFooterSetting.getLeft())) {
                buildHeadFooter(head, headerAndFooterSetting.getLeft(), 0);
            }
            if (!CommonTools.isNullStr(headerAndFooterSetting.getRight())) {
                buildHeadFooter(head, headerAndFooterSetting.getRight(), 2);
            }
        }
    }

    /**
     * 生成sheet页脚
     *
     * @param sheet        sheet对象
     * @param sheetSetting sheet配置
     */
    void generateSheetFooter(Sheet sheet, ExcelSheetSetting sheetSetting) {
        if (validationConfig(sheetSetting, 3)) {
            ExcelHeaderAndFooterSetting headerAndFooterSetting = sheetSetting.getFooter();
            Footer foot = sheet.getFooter();
            if (!CommonTools.isNullStr(headerAndFooterSetting.getCenter())) {
                buildHeadFooter(foot, headerAndFooterSetting.getCenter(), 1);
            }
            if (!CommonTools.isNullStr(headerAndFooterSetting.getLeft())) {
                buildHeadFooter(foot, headerAndFooterSetting.getLeft(), 0);
            }
            if (!CommonTools.isNullStr(headerAndFooterSetting.getRight())) {
                buildHeadFooter(foot, headerAndFooterSetting.getRight(), 2);
            }
        }
    }

    /**
     * 设置sheet合并单元格
     *
     * @param sheet        sheet对象
     * @param sheetSetting sheet配置
     */
    void generateSheetMerge(Sheet sheet, ExcelSheetSetting sheetSetting) throws Exception {
        if (validationConfig(sheetSetting, 4)) {
            List<ExcelCellPointSetting> mergeCells = sheetSetting.getMergeCells();
            for (ExcelCellPointSetting cellPointSetting : mergeCells) {
                if (cellPointSetting.getFirstCol() > -1 && cellPointSetting.getFirstRow() > -1 && cellPointSetting.getLastCol() > -1 && cellPointSetting.getLastRow() > -1) {
                    sheet.addMergedRegion(new CellRangeAddress(cellPointSetting.getFirstRow(), cellPointSetting.getLastRow(), cellPointSetting.getFirstCol(), cellPointSetting.getLastCol()));
                } else {
                    throw new Exception("merge cell config is not complete!");
                }
            }
        }
    }

    /**
     * 设置sheet打印配置
     *
     * @param wb           工作簿对象
     * @param sheetIndex   sheet编号
     * @param sheet        sheet对象
     * @param sheetSetting sheet配置
     */
    void generateSheetPrintSetting(Workbook wb, int sheetIndex, Sheet sheet, ExcelSheetSetting sheetSetting) throws Exception {
        if (validationConfig(sheetSetting, 5)) {
            ExcelPrintSetting excelPrintSetting = sheetSetting.getPrintSetting();
            PrintSetup printSetup = sheet.getPrintSetup();
            printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
            printSetup.setLandscape(excelPrintSetting.isHorizontal());
            if (excelPrintSetting.getPageWidth() > -1) {
                printSetup.setFitWidth((short) excelPrintSetting.getPageWidth());
            }
            if (excelPrintSetting.getPageHeight() > -1) {
                printSetup.setFitHeight((short) excelPrintSetting.getPageHeight());
            }
            sheet.setMargin(Sheet.TopMargin, excelPrintSetting.getTopMargin());
            sheet.setMargin(Sheet.BottomMargin, excelPrintSetting.getBottomMargin());
            sheet.setMargin(Sheet.LeftMargin, excelPrintSetting.getLeftMargin());
            sheet.setMargin(Sheet.RightMargin, excelPrintSetting.getRightMargin());
            if (excelPrintSetting.getHorizontalCentre() != null) {
                sheet.setHorizontallyCenter(Boolean.parseBoolean(excelPrintSetting.getHorizontalCentre().toString()));
            }
            if (excelPrintSetting.getVerticallyCenter() != null) {
                sheet.setVerticallyCenter(Boolean.parseBoolean(excelPrintSetting.getVerticallyCenter().toString()));
            }
            ExcelCellPointSetting printArea = excelPrintSetting.getPrintArea();
            if (printArea != null) {
                if (printArea.getFirstCol() > -1 && printArea.getFirstRow() > -1 && printArea.getLastCol() > -1 && printArea.getLastRow() > -1) {
                    wb.setPrintArea(sheetIndex, printArea.getFirstCol(), printArea.getLastCol(), printArea.getFirstRow(), printArea.getLastRow());
                } else {
                    throw new Exception("print config is not complete!");
                }
            }
            ExcelCellPointSetting printTitles = excelPrintSetting.getPrintTitles();
            if (printTitles != null) {
                if (printTitles.getFirstCol() > -1 && printTitles.getFirstRow() > -1 && printTitles.getLastCol() > -1 && printTitles.getLastRow() > -1) {
                    sheet.setRepeatingRows(new CellRangeAddress(printTitles.getFirstRow(), printTitles.getLastRow(), printTitles.getFirstCol(), printTitles.getLastCol()));
                } else if (printTitles.getFirstCol() > -1 && printTitles.getLastCol() > -1) {
                    sheet.setRepeatingColumns(new CellRangeAddress(0, 0, printTitles.getFirstCol(), printTitles.getLastCol()));
                } else if (printTitles.getFirstRow() > -1 && printTitles.getLastRow() > -1) {
                    sheet.setRepeatingRows(new CellRangeAddress(printTitles.getFirstRow(), printTitles.getLastRow(), 0, 0));
                } else {
                    throw new Exception("print titles is not complete!");
                }
            }
        }
    }

    /**
     * 设置sheet窗口冻结
     *
     * @param sheet        sheet对象
     * @param sheetSetting sheet配置
     */
    void generateSheetFreeze(Sheet sheet, ExcelSheetSetting sheetSetting) throws Exception {
        if (validationConfig(sheetSetting, 6)) {
            int row;
            int col;
            ExcelFreezeSetting freezeSetting = sheetSetting.getFreeze();
            row = freezeSetting.getRow();
            if (row < 0) {
                throw new Exception("freeze row config is not complete!");
            }
            col = freezeSetting.getCol();
            if (col < 0) {
                throw new Exception("freeze cell config is not complete!");
            }
            sheet.createFreezePane(col, row, col, row);
        }
    }

    /**
     * 校验配置信息
     *
     * @param sheetSetting sheet配置
     * @param flag         0-sheet基本配置信息 1-数据配置信息 2-页眉配置信息 3-页脚配置信息 4-合并单元格配置信息 5-打印配置信息
     *                     6-冻结窗口配置信息
     * @return 校验是否通过
     */
    boolean validationConfig(ExcelSheetSetting sheetSetting, int flag) {
        if (flag == 0) {
            return !CommonTools.isNullStr(sheetSetting.getSheetName());
        } else if (flag == 1) {
            if (sheetSetting.getData() == null) {
                log.error("generate Excel failed: don't find data!");
                return false;
            }
        } else if (flag == 2) {
            return sheetSetting.getHeader() != null;
        } else if (flag == 3) {
            return sheetSetting.getFooter() != null;
        } else if (flag == 4) {
            return sheetSetting.getMergeCells() != null;
        } else if (flag == 5) {
            return sheetSetting.getPrintSetting() != null;
        } else if (flag == 6) {
            return sheetSetting.getFreeze() != null;
        }
        return true;
    }

    /**
     * 创建样式
     *
     * @param wb    工作簿对象
     * @param style 样式信息
     * @param flag  0-标题,1-数据,2-脚
     * @return 单元格样式
     */
    private CellStyle createStyle(Workbook wb, Map<String, String> style, int flag) {
        CellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        /* 设置字体大小 */
        short fontSize = FONT_SIZE;
        String fontConfig = style.getOrDefault("font", "");
        if (!CommonTools.isNullStr(fontConfig)) {
            fontSize = Short.parseShort(fontConfig);
        }
        font.setFontHeightInPoints(fontSize);
        /* 设置字体颜色 */
        short color = IndexedColors.BLACK.getIndex();
        String colorConfig = style.getOrDefault("color", "");
        if (!CommonTools.isNullStr(colorConfig)) {
            switch (colorConfig) {
                case "black":
                    color = IndexedColors.BLACK.getIndex();
                    break;
                case "blue":
                    color = IndexedColors.BLUE.getIndex();
                    break;
                case "red":
                    color = IndexedColors.RED.getIndex();
                    break;
                case "green":
                    color = IndexedColors.GREEN.getIndex();
                    break;
            }
        }
        font.setColor(color);
        /* 设置字体加粗 */
        String boldConfig = style.getOrDefault("bold", "");
        if (flag == 0) {
            font.setBold(!boldConfig.equals("false"));
        } else {
            font.setBold(boldConfig.equals("true"));
        }
        /* 设置字体下划线 */
        String underlineConfig = style.getOrDefault("underline", "");
        if (underlineConfig.equals("true")) {
            font.setUnderline(Font.U_SINGLE);
        }
        cellStyle.setFont(font);
        /* 设置单元格对齐方式 */
        String alignConfig = style.getOrDefault("align", "");
        HorizontalAlignment align = HorizontalAlignment.LEFT;
        if (flag == 0) {
            align = HorizontalAlignment.CENTER;
        }
        if (!CommonTools.isNullStr(alignConfig)) {
            switch (alignConfig) {
                case "center":
                    align = HorizontalAlignment.CENTER;
                    break;
                case "left":
                    align = HorizontalAlignment.LEFT;
                    break;
                case "right":
                    align = HorizontalAlignment.RIGHT;
                    break;
            }
        }
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(align);
        /* 设置单元格自动换行 */
        cellStyle.setWrapText(true);
        /* 设置单元格边框 */
        String borderConfig = style.getOrDefault("border", "");
        if (!CommonTools.isNullStr(borderConfig)) {
            switch (borderConfig) {
                case "no":
                    cellStyle.setBorderTop(BorderStyle.NONE);
                    cellStyle.setBorderBottom(BorderStyle.NONE);
                    cellStyle.setBorderLeft(BorderStyle.NONE);
                    cellStyle.setBorderRight(BorderStyle.NONE);
                    break;
                case "all":
                    cellStyle.setBorderTop(BorderStyle.THIN);
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);
                    break;
                case "top":
                    cellStyle.setBorderTop(BorderStyle.THIN);
                    break;
                case "bottom":
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    break;
                case "right":
                    cellStyle.setBorderRight(BorderStyle.THIN);
                    break;
                case "left":
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    break;
            }
        }
        if (flag != 2) {
            if (CommonTools.isNullStr(borderConfig)) {
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setBorderBottom(BorderStyle.THIN);
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
            }
        } else {
            if (CommonTools.isNullStr(borderConfig)) {
                cellStyle.setBorderTop(BorderStyle.NONE);
                cellStyle.setBorderBottom(BorderStyle.NONE);
                cellStyle.setBorderLeft(BorderStyle.NONE);
                cellStyle.setBorderRight(BorderStyle.NONE);
            }
        }
        return cellStyle;
    }

    /**
     * 通过配置信息生成表头
     *
     * @param wb        工作簿
     * @param bodyCtrl  表头配置
     * @param rowIndex  起始行
     * @param cellIndex 起始列
     * @param sheet     工作表
     * @return 下一个起始行号
     */
    private int createBodyHead(Workbook wb, List<Map<String, String>> bodyCtrl, int rowIndex, int cellIndex, Sheet sheet) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        for (int i = 0; i < bodyCtrl.size(); i++) {
            Cell cell = row.createCell(cellIndex + i);
            Map<String, String> headMap = bodyCtrl.get(i);
            String headTitle = headMap.getOrDefault("value", "");
            CellStyle cellStyle = createStyle(wb, headMap, 0);// 标题样式
            String widthConfig = headMap.getOrDefault("width", "");
            if (!CommonTools.isNullStr(widthConfig)) {
                sheet.setColumnWidth(cellIndex + i, Integer.parseInt(widthConfig) * 256);
            }
            cell.setCellValue(headTitle);
            cell.setCellStyle(cellStyle);
        }
        return ++rowIndex;
    }

    /**
     * 通过数据列名生成表头
     *
     * @param wb        工作簿
     * @param names     名称
     * @param rowIndex  开始行
     * @param cellIndex 开始列
     * @param sheet     sheet对象
     * @return 结束行
     */
    private int createBodyHeadByName(Workbook wb, String names, int rowIndex, int cellIndex, Sheet sheet) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        CellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        /* 设置字体颜色 */
        font.setColor(IndexedColors.BLACK.getIndex());
        /* 设置字体大小 */
        font.setFontHeightInPoints(FONT_SIZE);
        /* 设置字体加粗 */
        font.setBold(true);
        cellStyle.setFont(font);
        /* 设置单元格对齐方式 */
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        /* 设置单元格自动换行 */
        cellStyle.setWrapText(true);
        /* 边框 */
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        int cellindex = cellIndex;
        String[] name = StringUtils.splitPreserveAllTokens(names, ",");
        for (String aName : name) {
            Cell cell = row.createCell(cellindex);
            cell.setCellValue(aName);
            cell.setCellStyle(cellStyle);
            cellindex++;
        }
        return ++rowIndex;
    }

    /**
     * 构建页眉页脚
     *
     * @param headerFooter 页眉页脚对象
     * @param info         配置信息字符串
     * @param flag         0-左 1-中 2-右
     */
    private void buildHeadFooter(Header headerFooter, String info, int flag) {
        StringBuilder contents;
        switch (flag) {
            case 0:
                contents = new StringBuilder(headerFooter.getLeft());
                break;
            case 2:
                contents = new StringBuilder(headerFooter.getRight());
                break;
            default:
                contents = new StringBuilder(headerFooter.getCenter());
        }
        int pageNumberBegin = info.indexOf("[pageNumber]");
        int pageTotalBegin = info.indexOf("[pageTotal]");
        if (pageNumberBegin > -1 && pageTotalBegin < 0) {
            contents.append(info, 0, pageNumberBegin);
            contents.append(HSSFHeader.page());
            contents.append(info.substring(pageNumberBegin + 12));
        } else if (pageNumberBegin > -1) {
            if (pageNumberBegin < pageTotalBegin) {
                contents.append(info, 0, pageNumberBegin);
                contents.append(HSSFHeader.page());
                contents.append(info, pageNumberBegin + 12, pageTotalBegin);
                contents.append(HSSFHeader.numPages());
                contents.append(info.substring(pageTotalBegin + 11));
            } else {
                contents.append(info, 0, pageTotalBegin);
                contents.append(HSSFHeader.numPages());
                contents.append(info, pageTotalBegin + 11, pageNumberBegin);
                contents.append(HSSFHeader.page());
                contents.append(info.substring(pageNumberBegin + 12));
            }
        } else if (pageTotalBegin > -1) {
            contents.append(info, 0, pageTotalBegin);
            contents.append(HSSFHeader.numPages());
            contents.append(info.substring(pageTotalBegin + 11));
        } else {
            contents.append(info);
        }
        if (flag == 0) {
            headerFooter.setLeft(contents.toString());
        } else if (flag == 2) {
            headerFooter.setRight(contents.toString());
        } else {
            headerFooter.setCenter(contents.toString());
        }
    }

    /**
     * 构建页眉页脚
     *
     * @param headerFooter 页眉页脚对象
     * @param info         配置信息字符串
     * @param flag         0-左 1-中 2-右
     */
    private void buildHeadFooter(Footer headerFooter, String info, int flag) {
        StringBuilder contents;
        switch (flag) {
            case 0:
                contents = new StringBuilder(headerFooter.getLeft());
                break;
            case 2:
                contents = new StringBuilder(headerFooter.getRight());
                break;
            default:
                contents = new StringBuilder(headerFooter.getCenter());
        }
        int pageTotalBegin = info.indexOf("[pageTotal]");
        int pageNumberBegin = info.indexOf("[pageNumber]");
        if (pageNumberBegin > -1 && pageTotalBegin < 0) {
            contents.append(info, 0, pageNumberBegin);
            contents.append(HSSFFooter.page());
            contents.append(info.substring(pageNumberBegin + 12));
        } else if (pageNumberBegin > -1) {
            if (pageNumberBegin < pageTotalBegin) {
                contents.append(info, 0, pageNumberBegin);
                contents.append(HSSFFooter.page());
                contents.append(info, pageNumberBegin + 12, pageTotalBegin);
                contents.append(HSSFFooter.numPages());
                contents.append(info.substring(pageTotalBegin + 11));
            } else {
                contents.append(info, 0, pageTotalBegin);
                contents.append(HSSFFooter.numPages());
                contents.append(info, pageTotalBegin + 11, pageNumberBegin);
                contents.append(HSSFFooter.page());
                contents.append(info.substring(pageNumberBegin + 12));
            }
        } else if (pageTotalBegin > -1) {
            contents.append(info, 0, pageTotalBegin);
            contents.append(HSSFFooter.numPages());
            contents.append(info.substring(pageTotalBegin + 11));
        } else {
            contents.append(info);
        }
        if (flag == 0) {
            headerFooter.setLeft(contents.toString());
        } else if (flag == 2) {
            headerFooter.setRight(contents.toString());
        } else {
            headerFooter.setCenter(contents.toString());
        }
    }

}
