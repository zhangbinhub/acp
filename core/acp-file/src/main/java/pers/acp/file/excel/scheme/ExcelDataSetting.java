package pers.acp.file.excel.scheme;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhang by 30/08/2019
 * @since JDK 11
 */
public class ExcelDataSetting {
    public List<Map<String, String>> getDataList() {
        return dataList;
    }

    public void setDataList(List<Map<String, String>> dataList) {
        this.dataList = dataList;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public List<Map<String, String>> getTitleCtrl() {
        return titleCtrl;
    }

    public void setTitleCtrl(List<Map<String, String>> titleCtrl) {
        this.titleCtrl = titleCtrl;
    }

    public List<Map<String, String>> getBodyCtrl() {
        return bodyCtrl;
    }

    public void setBodyCtrl(List<Map<String, String>> bodyCtrl) {
        this.bodyCtrl = bodyCtrl;
    }

    public List<Map<String, String>> getFootCtrl() {
        return footCtrl;
    }

    public void setFootCtrl(List<Map<String, String>> footCtrl) {
        this.footCtrl = footCtrl;
    }

    public boolean isShowBodyHead() {
        return showBodyHead;
    }

    public void setShowBodyHead(boolean showBodyHead) {
        this.showBodyHead = showBodyHead;
    }

    public int getDefaultRowIndex() {
        return defaultRowIndex;
    }

    public void setDefaultRowIndex(int defaultRowIndex) {
        this.defaultRowIndex = defaultRowIndex;
    }

    public int getDefaultCellIndex() {
        return defaultCellIndex;
    }

    public void setDefaultCellIndex(int defaultCellIndex) {
        this.defaultCellIndex = defaultCellIndex;
    }

    /**
     * 内容[name:value,...]
     */
    private List<Map<String, String>> dataList = new ArrayList<>();
    /**
     * 列名，“,”分隔
     */
    private String names = "";
    /**
     * 内容[value:,row:,col:,colspan:,rowspan:,width:,height:,font:,bold:true|false,underline:true|false,align:,color:,border:no|all|top|left|right|bottom]^...
     */
    private List<Map<String, String>> titleCtrl = new ArrayList<>();
    /**
     * 内容[value:,colspan:,rowspan:,width:,height:,font:,bold:true|false,underline:true|false,align:,color:,border:no|all|top|left|right|bottom]^...
     */
    private List<Map<String, String>> bodyCtrl = new ArrayList<>();
    /**
     * 内容[value:,row:,col:,paddingRow:,colspan:,rowspan:,width:,height:,font:,bold:true|false,underline:true|false,align:,color:,border:no|all|top|left|right|bottom]^...
     */
    private List<Map<String, String>> footCtrl = new ArrayList<>();
    /**
     * 是否显示表头
     */
    private boolean showBodyHead = false;
    /**
     * 默认起始行号
     */
    private int defaultRowIndex = 0;
    /**
     * 默认起始列号
     */
    private int defaultCellIndex = 0;
}
