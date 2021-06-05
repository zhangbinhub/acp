package pers.acp.spring.boot.tools

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.core.env.Environment
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import pers.acp.core.CommonTools
import pers.acp.spring.boot.vo.ErrorVo
import pers.acp.spring.boot.enums.ResponseCode

/**
 * Created by Shepherd on 2016-08-05.
 * 报文消息工具类
 */
object PackageTools {

    /**
     * 根据环境配置，构建 mapper
     *
     * @param environment 环境配置
     * @return mapper
     */
    @JvmStatic
    fun buildJacksonObjectMapper(environment: Environment): ObjectMapper {
        val jacksonProperties = JacksonProperties()
        jacksonProperties.propertyNamingStrategy =
            environment.getProperty("spring.jackson.property-naming-strategy", "")
        if ("non_null" == environment.getProperty("spring.jackson.default-property-inclusion", "").lowercase()) {
            jacksonProperties.defaultPropertyInclusion = JsonInclude.Include.NON_NULL
        }
        return buildJacksonObjectMapper(jacksonProperties)
    }

    /**
     * 根据配置文件，构建 mapper
     *
     * @param jacksonProperties 配置信息
     * @return mapper
     */
    @JvmStatic
    fun buildJacksonObjectMapper(jacksonProperties: JacksonProperties): ObjectMapper {
        val builder = Jackson2ObjectMapperBuilder.json()
        var propertyNamingStrategyDefault = PropertyNamingStrategy()
        if ("SNAKE_CASE" == jacksonProperties.propertyNamingStrategy.uppercase()) {
            propertyNamingStrategyDefault = PropertyNamingStrategies.SnakeCaseStrategy()
        }
        builder.propertyNamingStrategy(propertyNamingStrategyDefault)
        if (jacksonProperties.defaultPropertyInclusion != null) {
            builder.serializationInclusion(jacksonProperties.defaultPropertyInclusion)
        }
        return builder.propertyNamingStrategy(propertyNamingStrategyDefault).build()
    }

    /**
     * 构建响应报文
     *
     * @param responseCode 响应代码
     * @param msg          响应信息
     * @return 响应报文JSON对象
     */
    @JvmStatic
    fun buildErrorResponsePackage(responseCode: ResponseCode, msg: String?): ErrorVo {
        var message = msg
        if (CommonTools.isNullStr(message)) {
            message = responseCode.description
        }
        return buildErrorResponsePackage(responseCode.value, message)
    }

    /**
     * 构建响应报文
     *
     * @param code 响应代码
     * @param msg  响应信息
     * @return 响应报文JSON对象
     */
    @JvmStatic
    fun buildErrorResponsePackage(code: Int, msg: String?): ErrorVo {
        val errorVO = ErrorVo()
        errorVO.code = code
        errorVO.error = msg
        errorVO.errorDescription = msg
        return errorVO
    }

}
