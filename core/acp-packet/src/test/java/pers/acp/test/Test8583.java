package pers.acp.test;

import io.github.zhangbinhub.acp.core.packet.iso8583.Iso8583Packet;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author zhangbin by 2018-2-3 23:09
 * @since JDK 11
 */
public class Test8583 {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            //***********************组装8583报文测试--start***********************//
            TreeMap<String, String> filedMap = new TreeMap<>();//报文域
            filedMap.put("FIELD003", "1799");//交易码
            filedMap.put("FIELD013", "2013-11-06");//交易日期
            filedMap.put("FIELD008", "12345678901");//账号
            filedMap.put("FIELD033", "aa索隆bb");//注意这个域是变长域!
            filedMap.put("FIELD036", "123456");//注意这个域是变长域!

            byte[] send = Iso8583Packet.make8583(filedMap);
            String hex = ByteUtils.toHexString(send);
            System.out.println("完成组装8583报文==" + hex + "==");
            System.out.println("完成组装8583报文==" + new String(send) + "==");
            //***********************组装8583报文测试--end***********************//

            System.out.println(Integer.toBinaryString(send[0]));
            System.out.println(Integer.toBinaryString(send[0] >>> 7));

            //***********************解析8583报文测试--start***********************//
            Map<String, String> back = Iso8583Packet.analyze8583(ByteUtils.fromHexString(hex));
            System.out.println("完成解析8583报文==" + back.toString() + "==");
            //***********************解析8583报文测试--end***********************//
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
