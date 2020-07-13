package pers.acp.client.exceptions

import pers.acp.core.base.BaseException

/**
 * @author zhang by 11/07/2019
 * @since JDK 11
 */
class HttpException : BaseException {

    constructor(message: String?) : super(message)

    constructor(code: Int?, message: String?) : super(code, message)
}