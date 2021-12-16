package com.kernel.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.zhangbinhub.acp.core.CommonTools;
import io.github.zhangbinhub.acp.core.log.LogFactory;
import io.github.zhangbinhub.acp.core.security.AesEncrypt;
import io.github.zhangbinhub.acp.core.security.DesEncrypt;
import io.github.zhangbinhub.acp.core.security.HmacEncrypt;
import io.github.zhangbinhub.acp.core.security.Md5Encrypt;
import io.github.zhangbinhub.acp.core.security.key.KeyManagement;

import java.math.BigDecimal;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by zhangbin on 2016/9/5.
 */
public class Test {

    private static final LogFactory log = LogFactory.getInstance(Test.class);

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("param1", "11");
        json.put("param2", 2);
        ObjectNode json2 = mapper.createObjectNode();
        json2.put("param1", "11");
        json2.put("param2", 2);

        ArrayNode jsonArray2 = mapper.createArrayNode();
        jsonArray2.add(json2);

        json.set("bean2", jsonArray2);

        ArrayNode jsonArray = mapper.createArrayNode();
        jsonArray.add(json);

        Bean1 bean1 = CommonTools.jsonToObject(json, Bean1.class);
        System.out.println(bean1.getParam1());
        bean1.setParam3('a');
        bean1.setParam4(Float.valueOf("1.0E-4"));
        bean1.setParam5(2.43242);
        bean1.setParam6(899999999);
        bean1.setParam7(false);
        String result = CommonTools.objectToJson(bean1).toString();
        System.out.println(result);

        Bean1 bean11 = CommonTools.jsonToObject(CommonTools.getJsonFromStr(result), Bean1.class);
        System.out.println(bean11);

        System.out.println(CommonTools.class.getName());
        System.out.println(CommonTools.class.getCanonicalName());
        System.out.println(CommonTools.class.getTypeName());
        System.out.println(CommonTools.class.getSimpleName());

        List<String> list = new ArrayList<>();
        list.add("111a");
        list.add("2222");
        System.out.println(CommonTools.strInList("111A", list, true));

//        long start = System.currentTimeMillis();
//        String str = FileCommon.getFileContent("C:\\WorkFile\\个人\\Oms.log.2017-02-17");
//        System.out.println("耗时：" + (System.currentTimeMillis() - start));

//        DBConTools dbConTools = new DBConTools(0);
//        System.out.println(dbConTools.getDbType());

        String key = "OW9puTMy5WcC9lgp";
        String text = "/file/param/secret_key";
        String encrypt = AesEncrypt.encrypt(text, KeyManagement.getAESKey(key));
        System.out.println("AES密文：" + encrypt);
        System.out.println("AES明文：" + AesEncrypt.decrypt(encrypt, KeyManagement.getAESKey(key)));

        System.out.println("HMAC密文：" + HmacEncrypt.encrypt(text, KeyManagement.getKey(key, "HmacSHA1"), "HmacSHA1"));

        key = "OW9puTMy5WcC9lgp00000000";
        encrypt = DesEncrypt.encryptBy3DesEcb(text, KeyManagement.get3DESKey(key));
        System.out.println("3DES密文：" + encrypt);
        System.out.println("3DES明文：" + DesEncrypt.decryptBy3DesEcb(encrypt, KeyManagement.get3DESKey(key)));

        key = "OW9puTMy";
        encrypt = DesEncrypt.encryptByDesCbc(text, KeyManagement.getDESKey(key));
        System.out.println("DES密文：" + encrypt);
        System.out.println("DES明文：" + DesEncrypt.decryptByDesCbc(encrypt, KeyManagement.getDESKey(key)));

        DecimalFormat df = new DecimalFormat("#0.00");
        System.out.println(df.format(9999888811.12313132D));

        String aaa = "fdsafsa.dsfk.sadf.asdf1223:88080";
        System.out.println(aaa.split(":")[0]);
        System.out.println(aaa.split(":")[1]);

        String url = "https://127.0.0.1/bin/sa";
        try {
            URI uri = new URI(url);
            System.out.println(uri.getHost());
            System.out.println(uri.getPort());
        } catch (Exception e) {
        }

        System.out.println(new Date().getTime());

        double aa = 0.0001;
        JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(true);
        JsonNode jsonNode = jsonNodeFactory.numberNode(BigDecimal.valueOf(Double.valueOf(String.valueOf(aa))));
        JsonNode jsonNode1 = jsonNodeFactory.numberNode(new BigDecimal("300"));
        System.out.println(">>>>>>" + jsonNode.toString());

//        JsonNodeFactory jsonNodeFactoryTure = new JsonNodeFactory(true);
//        System.out.println(jsonNodeFactoryTure.numberNode(BigDecimal.valueOf(aa)).toString());

//        System.out.println(jsonNodeFactoryTure.numberNode(BigDecimal.ZERO).toString());

        Object[] params = {new BigDecimal("300"), "1231", 0.01, 300};
        List<Object> paramsList = Arrays.asList(params);
        JsonNode parameters = CommonTools.objectToJson(paramsList);
        System.out.println(parameters.toString());

        System.out.println(new StringBuilder("0xfadkfn1").insert(2, "0").toString());

        log.info(">>>>>>> info log");
        log.error(">>>>>>> info log");

        List<BigDecimal> list1 = new ArrayList<>();
        list1.add(BigDecimal.valueOf(123.123));
        list1.add(BigDecimal.valueOf(123.12));
        list1.add(BigDecimal.valueOf(123.22));
        list1.sort(Comparator.naturalOrder());
        System.out.println(list1.get(0).toString());
        System.out.println(list1.get(list1.size() - 1).toString());

        System.out.println(Md5Encrypt.encrypt(Md5Encrypt.encrypt("888888") + "admin"));

        String dtext = "nameValueConsole";
        String ntext = CommonTools.toUnderline(dtext);
        System.out.println(ntext);
        System.out.println(CommonTools.toCamel("name,param_config"));
        System.out.println(CommonTools.toCamel("namnifowniea"));
        System.out.println(CommonTools.toCamel("NIFOSADI"));
        System.out.println(CommonTools.toCamel("NIFOSAND_NFDOSIA"));
    }
}