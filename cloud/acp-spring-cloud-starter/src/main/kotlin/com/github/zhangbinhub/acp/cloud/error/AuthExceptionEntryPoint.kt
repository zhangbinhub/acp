package com.github.zhangbinhub.acp.cloud.error

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import com.github.zhangbinhub.acp.boot.enums.ResponseCode
import com.github.zhangbinhub.acp.boot.vo.ErrorVo

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException

/**
 * @author zhang by 05/03/2019
 * @since JDK 11
 */
class AuthExceptionEntryPoint(private val objectMapper: ObjectMapper) : AuthenticationEntryPoint {

    @Throws(IOException::class, ServletException::class)
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
        val errorVO = ErrorVo(
                code = ResponseCode.AuthError.value,
                error = "权限验证失败",
                errorDescription = "权限验证失败"
        )
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        objectMapper.writeValue(response.outputStream, errorVO)
    }
}
