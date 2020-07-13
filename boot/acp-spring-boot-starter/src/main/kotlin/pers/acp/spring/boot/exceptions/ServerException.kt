package pers.acp.spring.boot.exceptions

import pers.acp.core.base.BaseException
import pers.acp.spring.boot.enums.ResponseCode

class ServerException : BaseException {

    constructor(responseCode: ResponseCode) : super(responseCode.value, responseCode.description)

    constructor(message: String?) : super(message)

    constructor(code: Int?, message: String?) : super(code, message)

}
