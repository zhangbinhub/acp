package pers.acp.spring.boot.exceptions

import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pers.acp.spring.boot.enums.ResponseCode
import pers.acp.spring.boot.tools.PackageTools
import pers.acp.core.exceptions.EnumValueUndefinedException
import pers.acp.spring.boot.interfaces.LogAdapter

import javax.validation.ConstraintViolationException

/**
 * Create by zhangbin on 2017-8-10 16:26
 */
@ControllerAdvice
class RestExceptionHandler(private val logAdapter: LogAdapter) : ResponseEntityExceptionHandler() {

    private fun doLog(ex: Throwable) {
        logAdapter.error(ex.message, ex)
    }

    /**
     * 处理自定义异常
     *
     * @param ex 异常类
     * @return 响应对象
     */
    @ExceptionHandler(ServerException::class, ConstraintViolationException::class)
    fun handleServerException(ex: Exception): ResponseEntity<Any> =
            try {
                doLog(ex)
                if (ex is ServerException) {
                    ResponseCode.getEnum(ex.code ?: 99999)
                } else if (ex is ConstraintViolationException || ex is MethodArgumentNotValidException) {
                    ResponseCode.InvalidParameter
                } else {
                    ResponseCode.OtherError
                }
            } catch (e: EnumValueUndefinedException) {
                ResponseCode.OtherError
            }.let {
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .body(PackageTools.buildErrorResponsePackage(it, ex.message))
            }

    /**
     * 处理 MethodArgumentNotValidException 异常，参数校验不通过
     *
     * @param ex      MethodArgumentNotValidException
     * @param headers 请求头
     * @param status  请求状态
     * @param request 请求对象
     * @return 响应对象
     */
    override fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> =
            ex.bindingResult.allErrors.let {
                doLog(ex)
                val errorMsg = StringBuilder()
                it.forEach { error -> errorMsg.append(error.defaultMessage).append(";") }
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .body(PackageTools.buildErrorResponsePackage(ResponseCode.InvalidParameter, errorMsg.toString()))
            }

    /**
     * 处理@RequestParam错误, 即参数不足
     *
     * @param ex      异常类
     * @param headers 请求头
     * @param status  请求状态
     * @param request 请求对象
     * @return 响应对象
     */
    override fun handleMissingServletRequestParameter(ex: MissingServletRequestParameterException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        doLog(ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(PackageTools.buildErrorResponsePackage(ResponseCode.InvalidParameter, ex.message))
    }

    /**
     * 处理参数类型转换失败
     *
     * @param ex      异常类
     * @param headers 请求头
     * @param status  请求状态
     * @param request 请求对象
     * @return 响应对象
     */
    override fun handleTypeMismatch(ex: TypeMismatchException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        doLog(ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(PackageTools.buildErrorResponsePackage(ResponseCode.InvalidParameter, ex.message))
    }

    /**
     * 处理通用异常
     *
     * @param ex      异常类
     * @param body    协议体
     * @param headers 请求头
     * @param status  请求状态
     * @param request 请求对象
     * @return 响应对象
     */
    override fun handleExceptionInternal(ex: Exception, body: Any?, headers: HttpHeaders?, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        doLog(ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(PackageTools.buildErrorResponsePackage(ResponseCode.OtherError, ex.message))
    }

}
