package com.kernel.test;

import org.joda.time.DateTime;
import io.github.zhangbinhub.acp.core.CalendarTools;
import io.github.zhangbinhub.acp.core.CommonTools;
import io.github.zhangbinhub.acp.core.task.BaseAsyncTask;
import io.github.zhangbinhub.acp.core.task.threadpool.ThreadPoolService;
import io.github.zhangbinhub.acp.core.task.timer.Calculation;

import java.math.BigInteger;

/**
 * Create by zhangbin on 2017-11-22 20:06
 */
public class TestSimple {

    public static void main(String[] args) throws InterruptedException {
        BigInteger bigInteger1 = new BigInteger("9223372036854775807");
        BigInteger bigInteger2 = BigInteger.valueOf(Long.parseLong("9223372036854775807"));
        System.out.println("bigInteger1=" + bigInteger1);
        System.out.println("bigInteger1=" + bigInteger1.longValue());
        System.out.println("bigInteger2=" + bigInteger2);
        System.out.println("bigInteger2=" + bigInteger2.longValue());

        testPath();
        testThreadPool();
        testDateTime();
    }

    private static void testDateTime() {
        DateTime now = CommonTools.getNowDateTime();
        System.out.println(now);
        System.out.println(CommonTools.getDateTimeString(now, Calculation.DATETIME_FORMAT));
        System.out.println(CommonTools.getDateTimeString(now, Calculation.DATE_FORMAT));
        System.out.println(CommonTools.getDateTimeString(CalendarTools.getNextDay(now), Calculation.DATE_FORMAT));
        System.out.println(CommonTools.getDateTimeString(CalendarTools.getPrevDay(now), Calculation.DATE_FORMAT));
        System.out.println(CalendarTools.getWeekNo(now));
        System.out.println(CalendarTools.getMonthNoInQuarter(now));
        System.out.println(CalendarTools.getLastDayInMonthNo(now));
    }

    private static void testThreadPool() throws InterruptedException {
        ThreadPoolService threadPoolService = ThreadPoolService.getInstance(10, Integer.MAX_VALUE, 10);
//        for (int i = 0; i < 10000; i++) {
//            threadPoolService.addTask(new BaseAsyncTask(i + "") {
//                @Override
//                public boolean beforeExecuteFun() {
//                    return true;
//                }
//
//                @Override
//                public Object executeFun() {
//                    System.out.println("i=" + this.getTaskName());
//                    return true;
//                }
//
//                @Override
//                public void afterExecuteFun(Object result) {
//
//                }
//            });
//        }
        BaseAsyncTask task = new BaseAsyncTask("", false) {
            @Override
            public boolean beforeExecuteFun() {
                return true;
            }

            @Override
            public Object executeFun() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "123";
            }

            @Override
            public void afterExecuteFun(Object result) {

            }
        };
    }

    private static void testPath() {
        CommonTools.initTools();
        System.out.println(System.getProperty("user.home"));
        System.out.println(CommonTools.getWebRootAbsPath());
        System.out.println(CommonTools.formatAbsPath("/logs"));
        System.out.println(CommonTools.getAbsPath(""));
    }

}
