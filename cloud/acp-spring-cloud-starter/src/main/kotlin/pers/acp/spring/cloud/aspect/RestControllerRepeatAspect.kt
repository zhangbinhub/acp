package pers.acp.spring.cloud.aspect

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import pers.acp.core.security.Md5Encrypt
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.cloud.annotation.AcpCloudDuplicateSubmission
import pers.acp.spring.cloud.lock.DistributedLock
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * controller拦截器
 *
 * @author zhangbin by 21/11/2017 10:06
 * @since JDK 11
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
class RestControllerRepeatAspect(private val distributedLock: DistributedLock, private val objectMapper: ObjectMapper) {

    /**
     * 定义拦截规则
     */
    @Pointcut(value = "execution(public * *(..)) && @annotation(pers.acp.spring.cloud.annotation.AcpCloudDuplicateSubmission)")
    fun executeService() {
    }

    /**
     * 拦截器具体实现
     *
     * @param pjp 拦截对象
     * @return Object（被拦截方法的执行结果）
     */
    @Around("executeService()")
    @Throws(Throwable::class)
    fun doAround(pjp: ProceedingJoinPoint): Any? {
        val signature = pjp.signature as MethodSignature
        val method = signature.method
        val duplicateSubmission = method.getAnnotation(AcpCloudDuplicateSubmission::class.java)
        val key = getKey(signature.declaringTypeName + "." + method.name, duplicateSubmission.keyExpress, pjp.args)
        val expire = duplicateSubmission.expire
        return if (distributedLock.getLock(key, key, expire, false)) {
            try {
                pjp.proceed()
            } finally {
                distributedLock.releaseLock(key, key)
            }
        } else {
            throw ServerException("请勿重复请求")
        }
    }

    private fun getKey(prefix: String, keyExpress: String, args: Array<Any>): String {
        val builder = StringBuilder()
        for (arg in args) {
            if (arg !is HttpServletRequest && arg !is HttpServletResponse) {
                builder.append(",")
                if (arg is Int || arg is Long
                        || arg is Float || arg is Double || arg is Boolean
                        || arg is String || arg is Char || arg is Byte) {
                    builder.append(arg.toString())
                } else {
                    try {
                        builder.append(objectMapper.writeValueAsString(arg))
                    } catch (e: JsonProcessingException) {
                        builder.append(arg.toString())
                    }
                }
            }
        }
        val keyValue = prefix + ":" + Md5Encrypt.encrypt(builder.toString())
        return keyExpress.replace(AcpCloudDuplicateSubmission.defaultKeyExpress, keyValue)
    }

}
