package com.kernel.test;

import pers.acp.core.CommonTools;
import pers.acp.core.DBConTools;
import pers.acp.core.match.DecimalProcessModeEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Create by zhangbin on 2017-12-20 11:36
 */
public class TestSQL {

    public static void main(String[] args) {
        DBConTools dbConTools = new DBConTools();
        List<Map<String, Object>> result = dbConTools.getDataListBySql("select * from t_menu");
        System.out.println(CommonTools.objectToJson(result));

        BigDecimal gasPrice = BigDecimal.valueOf(160000);
        System.out.println(gasPrice.toPlainString());
        System.out.println(gasPrice.setScale(0, DecimalProcessModeEnum.HalfUp.getMode()).toPlainString());
    }

}
