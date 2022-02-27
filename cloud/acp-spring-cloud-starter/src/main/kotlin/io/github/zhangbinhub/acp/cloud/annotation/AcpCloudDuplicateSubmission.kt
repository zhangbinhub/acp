package io.github.zhangbinhub.acp.cloud.annotation

/**
 * @author zhang by 24/05/2019
 * @since JDK 11
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AcpCloudDuplicateSubmission(
    /**
     * key的表达式，默认"\[key\]"
     *
     * @return key
     */
    val keyExpress: String = defaultKeyExpress,
    /**
     * 超时时间，单位毫秒，默认1秒
     *
     * @return 过期时间
     */
    val expire: Long = 1000
) {
    companion object {
        const val defaultKeyExpress = "[key]"
    }
}
