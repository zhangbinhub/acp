package io.github.zhangbinhub.acp.boot.test

import io.github.zhangbinhub.acp.boot.tools.IpTools
import org.springframework.http.server.PathContainer
import org.springframework.util.AntPathMatcher
import org.springframework.web.util.pattern.PathPatternParser

/**
 * Create by zhangbin on 2017-12-19 11:28
 */
fun main() {
    println(IpTools.getMACAddressFromIp("fe80::3113:a293:fccc:b73"))
    println(IpTools.getMACAddressFromIp("172.26.64.1"))

    val antPathMatcher = AntPathMatcher()
    val expression = "/url/{id:a|b|c}"
    val urls = listOf("/url/a", "/url/b", "/url/c", "/url/d")
    println("模式表达式：$expression")
    urls.forEach {
        println("[AntPathMatcher] 路径：$it 匹配结果：${antPathMatcher.match(expression, it)}")
    }

    val pattern = PathPatternParser().parse(expression)
    urls.forEach {
        println("[PathPatternParser] 路径：$it 匹配结果：${pattern.matches(PathContainer.parsePath(it))}")
    }
}