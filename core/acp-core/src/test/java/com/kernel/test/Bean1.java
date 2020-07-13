package com.kernel.test;

import java.util.List;

/**
 * Created by zhangbin on 2016/9/6.
 */
public class Bean1 {

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public int getParam2() {
        return param2;
    }

    public void setParam2(int param2) {
        this.param2 = param2;
    }

    private String param1;

    private int param2;

    public List<Bean2> getBean2() {
        return bean2;
    }

    public void setBean2(List<Bean2> bean2) {
        this.bean2 = bean2;
    }

    private List<Bean2> bean2;

    private char param3;

    private Float param4;

    private Double param5;

    private long param6;

    public char getParam3() {
        return param3;
    }

    public void setParam3(char param3) {
        this.param3 = param3;
    }

    public Float getParam4() {
        return param4;
    }

    public void setParam4(Float param4) {
        this.param4 = param4;
    }

    public double getParam5() {
        return param5;
    }

    public void setParam5(double param5) {
        this.param5 = param5;
    }

    public long getParam6() {
        return param6;
    }

    public void setParam6(long param6) {
        this.param6 = param6;
    }

    public boolean isParam7() {
        return param7;
    }

    public void setParam7(boolean param7) {
        this.param7 = param7;
    }

    private boolean param7;

}
