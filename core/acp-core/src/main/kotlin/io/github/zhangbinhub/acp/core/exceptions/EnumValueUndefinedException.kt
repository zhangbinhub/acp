package io.github.zhangbinhub.acp.core.exceptions

import io.github.zhangbinhub.acp.core.base.BaseException

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
class EnumValueUndefinedException(cls: Class<*>, override val message: String?) : BaseException(8010, "$cls undefined value $message")