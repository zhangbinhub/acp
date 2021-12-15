package com.github.zhangbinhub.acp.boot.enums

import com.github.zhangbinhub.acp.core.exceptions.EnumValueUndefinedException

/**
 * 响应编码
 * Create by zhangbin on 2017-8-8 17:40
 */
enum class ResponseCode(val value: Int, val description: String) {

    Success(200, "请求成功"),

    InvalidParameter(422, "无效的参数"),

    AuthError(10001, "权限验证失败"),

    DbError(10002, "数据库错误"),

    TypeMismatch(99991, "请求参数类型不正确"),

    ServiceError(99997, "服务异常"),

    SysError(99998, "系统异常"),

    OtherError(99999, "其他系统异常");

    companion object {

        private var map: MutableMap<Int, ResponseCode> = mutableMapOf()

        init {
            for (type in values()) {
                map[type.value] = type
            }
        }

        @JvmStatic
        @Throws(EnumValueUndefinedException::class)
        fun getEnum(value: Int): ResponseCode {
            return if (map.containsKey(value)) {
                map.getValue(value)
            } else {
                throw EnumValueUndefinedException(ResponseCode::class.java, value.toString() + "")
            }
        }
    }

}
