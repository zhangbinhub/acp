package pers.acp.spring.boot.vo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * 错误信息实体
 *
 * @author zhang by 27/12/2018 13:07
 * @since JDK 11
 */
@ApiModel(value = "错误信息")
data class ErrorVo(
        @ApiModelProperty(value = "错误编码")
        var code: Int = 0,
        @ApiModelProperty(value = "信息")
        var error: String? = null,
        @ApiModelProperty(value = "描述")
        var errorDescription: String? = null
)
