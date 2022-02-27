package io.github.zhangbinhub.acp.cloud.oauth.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController @Autowired
constructor(private val checkTokenEndpoint: CheckTokenEndpoint) {
    @RequestMapping(value = ["/open/inner/check-token"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun checkToken(@RequestParam("token") value: String): ResponseEntity<Map<String, *>> =
        ResponseEntity.ok(checkTokenEndpoint.checkToken(value))
}