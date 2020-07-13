import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Workbook;
import pers.acp.file.excel.ExcelService;
import pers.acp.file.excel.data.ExcelCellData;
import pers.acp.file.excel.scheme.ExcelDataSetting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhang by 30/08/2019
 * @since JDK 11
 */
public class TestExcel {

    public static void main(String[] args) throws JsonProcessingException {
        testRead();
//        testCreateExcel1();
//        testCreateExcel2();
//        testCreateExcel3();
    }

    private static void testRead() throws JsonProcessingException {
        String fileName = "D:\\工作资料\\a\\CY工作流\\需求\\测试数据.xls";
//        String fileName = "D:\\工作资料\\a\\CY工作流\\需求\\测试数据.xlsx";
        ObjectMapper objectMapper = new ObjectMapper();
        ExcelService excelService = new ExcelService();
        Workbook wb = excelService.openWorkboot(fileName);
        for (int i = 0; i < 10; i++) {
            List<List<ExcelCellData>> dataList = excelService.readExcelData(wb, 0, i, 0, 1, 0);
            System.out.println("读出的总数：" + dataList.size());
            for (List<ExcelCellData> data : dataList) {
                System.out.println(objectMapper.writeValueAsString(data));
            }
        }
//        List<List<ExcelCellData>> dataList = excelService.readExcelData(fileName, 0, 1, 0, 10, 0, false);
//        System.out.println("读出的总数：" + dataList.size());
//        for (List<ExcelCellData> data : dataList) {
//            System.out.println(objectMapper.writeValueAsString(data));
//        }
    }

    private static void testCreateExcel1() {
//        String fileName = "D:\\test\\out1.xlsx";
        String fileName = "D:\\test\\out1.xls";
//        String template = "D:\\test\\测试1.xlsx";
        String template = "D:\\test\\测试1.xls";
        Map<String, String> params = new HashMap<>();
        params.put("no", "1");
        params.put("value1", "啊");
        params.put("value2", "范德萨");
        params.put("value3", "啊范德萨");
        params.put("value4", "啊范德萨111");
        ExcelService excelService = new ExcelService();
        excelService.createExcelFile(fileName, template, params);
    }

    private static void testCreateExcel2() {
        String fileName = "D:\\test\\out2.xlsx";
//        String fileName = "D:\\test\\out2.xls";
        String template = "D:\\test\\测试2.xlsx";
//        String template = "D:\\test\\测试2.xls";
        ExcelDataSetting dataSetting = new ExcelDataSetting();
        dataSetting.setNames("no,value1,value2,value3");
        for (int i = 0; i < 4; i++) {
            Map<String, String> row = new HashMap<>();
            row.put("no", i + "");
            row.put("value1", "啊" + i);
            row.put("value2", "范德萨" + i);
            row.put("value3", "啊范德萨" + i);
            dataSetting.getDataList().add(row);
        }
        ExcelService excelService = new ExcelService();
        excelService.createExcelFile(fileName, template, "row", dataSetting);
    }

    private static void testCreateExcel3() {
        String fileName = "D:\\test\\out3.xlsx";
        ExcelDataSetting dataSetting = new ExcelDataSetting();
        Map<String, String> body1 = new HashMap<>();
        body1.put("value", "序号");
        body1.put("width", "10");
        Map<String, String> body2 = new HashMap<>();
        body2.put("value", "值1");
        body2.put("bold", "true");
        Map<String, String> body3 = new HashMap<>();
        body3.put("value", "值2");
        Map<String, String> body4 = new HashMap<>();
        body4.put("value", "值3");
        body4.put("width", "50");
        dataSetting.getBodyCtrl().add(body1);
        dataSetting.getBodyCtrl().add(body2);
        dataSetting.getBodyCtrl().add(body3);
        dataSetting.getBodyCtrl().add(body4);
        dataSetting.setShowBodyHead(true);
        dataSetting.setNames("no,value1,value2,value3");
        for (int i = 0; i < 4; i++) {
            Map<String, String> row = new HashMap<>();
            row.put("no", i + "");
            row.put("value1", "啊" + i);
            row.put("value2", "范德萨" + i);
            row.put("value3", "啊范德萨" + i);
            dataSetting.getDataList().add(row);
        }
        ExcelService excelService = new ExcelService();
        excelService.createExcelFile(fileName, false, dataSetting);
    }

}
