package pers.acp.test

import io.github.zhangbinhub.acp.core.packet.xml.XmlPacket
import org.junit.jupiter.api.Test

class TestXml {
    @Test
    fun testXmlToObj() {
        val xmlStr = """
            <?xml version="1.0" encoding="UTF-8"?>
            <root>
                <fileFlag>123</fileFlag>
                <BODY>
                    <mobile>33333333</mobile>
                </BODY>
            </root>
        """.trimIndent()
        val obj = XmlPacket.xmlToObject(xmlStr, TestBean::class.java)
        println(obj)
        println(XmlPacket.objectToXML(obj!!))
    }
}