package pers.acp.message.exceptions

/**
 * @author zhang by 11/07/2019
 * @since JDK 11
 */
import pers.acp.core.base.BaseException

class EmailException(override val message: String?) : BaseException(message)
