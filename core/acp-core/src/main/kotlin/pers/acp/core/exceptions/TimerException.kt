package pers.acp.core.exceptions

import pers.acp.core.base.BaseException

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
class TimerException(override val message: String?) : BaseException(message)