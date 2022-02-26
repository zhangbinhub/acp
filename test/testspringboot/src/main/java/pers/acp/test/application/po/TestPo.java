package pers.acp.test.application.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(value = "测试参数", description = "测试参数描述")
public class TestPo {
    @ApiModelProperty(value = "字符串参数1", required = true)
    private String s1;

    @ApiModelProperty(value = "整型参数1")
    private Integer i1;

    @NotBlank(message = "字符串参数1不能为空")
    public String getS1() {
        return s1;
    }

    public void setS1(String s1) {
        this.s1 = s1;
    }

    @NotNull(message = "整型参数1不能为空")
    public Integer getI1() {
        return i1;
    }

    public void setI1(Integer i1) {
        this.i1 = i1;
    }
}
