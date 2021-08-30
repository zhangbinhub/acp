package pers.acp.spring.boot.test

import pers.acp.spring.boot.tools.IpTools

/**
 * Create by zhangbin on 2017-12-19 11:28
 */
fun main() {
    println(IpTools.getMACAddressFromIp("fe80::3113:a293:fccc:b73"))
    println(IpTools.getMACAddressFromIp("172.26.64.1"))
}