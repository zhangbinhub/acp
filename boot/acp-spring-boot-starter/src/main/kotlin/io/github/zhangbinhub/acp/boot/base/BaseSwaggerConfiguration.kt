package io.github.zhangbinhub.acp.boot.base

import io.github.zhangbinhub.acp.boot.conf.SwaggerConfiguration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import springfox.documentation.builders.*
import springfox.documentation.schema.ScalarType
import springfox.documentation.service.*
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
abstract class BaseSwaggerConfiguration(
    private val version: String?,
    private val swaggerConfiguration: SwaggerConfiguration
) {

    protected fun globalRequestParameter(): List<RequestParameter> = mutableListOf(
        RequestParameterBuilder()
            .name("Authorization")
            .description("认证信息")
            .required(false)
            .`in`(ParameterType.HEADER)
            .query { q ->
                q.model { m ->
                    m.scalarModel(ScalarType.STRING)
                }
            }.build()
    )

    private fun globalResponse(): List<Response> = mutableListOf(
        ResponseBuilder().code(HttpStatus.OK.value().toString()).description("请求成功").build(),
        ResponseBuilder().code(HttpStatus.CREATED.value().toString()).description("资源创建成功").build(),
        ResponseBuilder().code(HttpStatus.BAD_REQUEST.value().toString()).description("业务异常").build(),
        ResponseBuilder().code(HttpStatus.UNAUTHORIZED.value().toString()).description("权限验证失败").build(),
        ResponseBuilder().code(HttpStatus.FORBIDDEN.value().toString()).description("权限不足").build(),
        ResponseBuilder().code(HttpStatus.NOT_FOUND.value().toString()).description("找不到资源").build(),
        ResponseBuilder().code(HttpStatus.INTERNAL_SERVER_ERROR.value().toString()).description("系统内部错误").build()
    )

    private fun buildApiInfo(
        title: String,
        description: String,
        contactName: String = "",
        contactUrl: String = "",
        contactEmail: String = ""
    ): ApiInfo = ApiInfoBuilder()
        //页面标题
        .title(title)
        //创建人
        .contact(Contact(contactName, contactUrl, contactEmail))
        //版本号
        .version(version)
        //描述
        .description(description)
        .build()

    protected fun buildDocket(
        basePackage: String,
        title: String,
        description: String,
        contactName: String = "",
        contactUrl: String = "",
        contactEmail: String = ""
    ): Docket = Docket(DocumentationType.OAS_30)
        .enable(swaggerConfiguration.enabled)
        .apiInfo(buildApiInfo(title, description, contactName, contactUrl, contactEmail))
        .select()
        .apis(RequestHandlerSelectors.basePackage(basePackage))
        .paths(PathSelectors.any())
        .build()
        .globalResponses(HttpMethod.HEAD, globalResponse())
        .globalResponses(HttpMethod.GET, globalResponse())
        .globalResponses(HttpMethod.POST, globalResponse())
        .globalResponses(HttpMethod.OPTIONS, globalResponse())
        .globalResponses(HttpMethod.PATCH, globalResponse())
        .globalResponses(HttpMethod.PUT, globalResponse())
        .globalResponses(HttpMethod.DELETE, globalResponse())
        .globalResponses(HttpMethod.TRACE, globalResponse())
}
