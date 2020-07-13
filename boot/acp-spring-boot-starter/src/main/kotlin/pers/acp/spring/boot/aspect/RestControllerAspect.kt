package pers.acp.spring.boot.aspect

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableList
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.context.request.async.DeferredResult
import org.springframework.web.context.request.async.WebAsyncTask
import pers.acp.core.CommonTools
import pers.acp.spring.boot.component.BootLogAdapter
import pers.acp.spring.boot.conf.ControllerLogConfiguration
import pers.acp.spring.boot.tools.HttpTools
import java.util.concurrent.Callable

/**
 * Controller 拦截器，记录请求日志
 *
 * @author zhangbin by 21/11/2017 10:06
 * @since JDK 11
 */
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
class RestControllerAspect(private val controllerLogConfiguration: ControllerLogConfiguration,
                           private val objectMapper: ObjectMapper) {
    private val logAdapter = BootLogAdapter()

    /**
     * 定义拦截规则
     */
    @Pointcut(value = "execution(public * *(..)) && (" +
            "@within(org.springframework.web.bind.annotation.RestController) " +
            "|| @within(org.springframework.stereotype.Controller)) && ( " +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.GetMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PatchMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.Mapping) ) ")
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
        var response: Any? = null
        val beginTime = System.currentTimeMillis()
        RequestContextHolder.getRequestAttributes()?.let {
            (it as ServletRequestAttributes).apply {
                val request = this.request
                val method = request.method
                val uri = request.requestURI
                if (needLog(controllerLogConfiguration, uri)) {
                    val startLog = StringBuilder("========== 请求开始, method: $method, Content-Type: ${request.contentType}, uri: $uri\n")
                    startLog.append("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n")
                    startLog.append("target : ").append(pjp.signature.declaringTypeName).append("\n")
                    startLog.append("-----> request: ").append(method).append("\n")
                    val headers = request.headerNames
                    if (headers.hasMoreElements()) {
                        startLog.append("      ┖---- headers: \n")
                    }
                    while (headers.hasMoreElements()) {
                        val name = headers.nextElement()
                        startLog.append("           - ").append(name).append("=").append(request.getHeader(name)).append("\n")
                    }
                    val queryString = request.queryString
                    if (!CommonTools.isNullStr(queryString)) {
                        startLog.append("      ┖---- query string: \n").append("           - ").append(queryString).append("\n")
                    }
                    val params = request.parameterNames
                    if (params.hasMoreElements()) {
                        startLog.append("      ┖---- parameter: \n")
                    }
                    while (params.hasMoreElements()) {
                        val name = params.nextElement()
                        startLog.append("           - ").append(name).append("=").append(request.getParameter(name)).append("\n")
                    }
                    startLog.append("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------")
                    logAdapter.info(startLog.toString())
                    logAdapter.info(">>>>>>>>>> 处理开始...  [method: $method, uri: $uri]")
                }
                val processBegin = System.currentTimeMillis()
                response = pjp.proceed()
                if (!(response is WebAsyncTask<*> || response is Callable<*> || response is DeferredResult<*>)) {
                    if (needLog(controllerLogConfiguration, uri)) {
                        logAdapter.info(">>>>>>>>>> 处理结束! [method: $method, uri: $uri, 处理耗时: ${System.currentTimeMillis() - processBegin} 毫秒]")
                        response?.apply {
                            val endLog = StringBuilder("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n")
                            endLog.append("-----> response: ").append(this.toString()).append("\n")
                            var responseInfo: String? = null
                            if (this is ResponseEntity<*>) {
                                val responseBody = this.body
                                responseBody?.let { body ->
                                    responseInfo = if (body is String) {
                                        body
                                    } else {
                                        objectMapper.writeValueAsString(body)
                                    }
                                }
                            } else {
                                responseInfo = objectMapper.writeValueAsString(this)
                            }
                            endLog.append("      ┖---- body: \n").append(responseInfo).append("\n")
                            endLog.append("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------")
                            logAdapter.info(endLog.toString())
                        }
                        logAdapter.info("========== 请求结束! [method: $method, uri: $uri, 总耗时: ${System.currentTimeMillis() - beginTime} 毫秒]")
                    }
                } else {
                    logAdapter.info(">>>>>>>>>> 进行异步处理...  [method: $method, uri: $uri]")
                }
            }
        }
        return response
    }

    /**
     * 匹配uri
     *
     * @param controllerLogConfiguration 配置对象
     * @param uri uri
     * @return true|false
     */
    private fun needLog(controllerLogConfiguration: ControllerLogConfiguration, uri: String): Boolean {
        if (controllerLogConfiguration.enabled) {
            for (regex in noLogUriRegular) {
                if (HttpTools.isBeIdentifiedUri(uri, regex)) {
                    return false
                }
            }
            val noLogUriRegularConfig = controllerLogConfiguration.noLogUriRegular
            for (regex in noLogUriRegularConfig) {
                if (HttpTools.isBeIdentifiedUri(uri, regex)) {
                    return false
                }
            }
            return true
        } else {
            return false
        }
    }

    private val noLogUriRegular = ImmutableList.of("/error")

}
