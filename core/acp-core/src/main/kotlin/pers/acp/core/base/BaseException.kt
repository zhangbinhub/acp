package pers.acp.core.base

import java.lang.RuntimeException

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
abstract class BaseException(override val message: String?) : RuntimeException(message) {

    var code: Int? = 1

    constructor(e: Exception) : this(e.message)

    constructor(code: Int?, message: String?) : this(message) {
        this.code = code
    }

}
