package io.github.zhangbinhub.acp.boot.exceptions

import io.github.zhangbinhub.acp.core.base.BaseException
import io.github.zhangbinhub.acp.boot.enums.ResponseCode

class ServerException : BaseException {

    constructor(responseCode: ResponseCode) : super(responseCode.value, responseCode.description)

    constructor(message: String?) : super(message)

    constructor(code: Int?, message: String?) : super(code, message)

}
