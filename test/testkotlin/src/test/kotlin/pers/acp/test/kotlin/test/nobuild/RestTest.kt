package pers.acp.test.kotlin.test.nobuild

import pers.acp.test.kotlin.component.BeanBeanResolver
import pers.acp.test.kotlin.entity.TableOne
import pers.acp.test.kotlin.repository.MemberRepository
import pers.acp.test.kotlin.repository.TableOneRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.http.HttpStatus
import pers.acp.test.kotlin.test.TestData
import pers.acp.test.kotlin.BaseTest


/**
 * @author zhangbin by 28/04/2018 17:18
 * @since JDK 11
 */
internal class RestTest : BaseTest() {

    @Autowired
    lateinit var beanBeanResolver: BeanBeanResolver

    @Autowired
    lateinit var tableOneRepository: TableOneRepository

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Test
    fun testQuery200() {
        val name = "abc"
        val result = testRestTemplate.getForEntity("/query?name={1}", TableOne::class.java, name)
        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(result.body!!.name, name)
    }

    @Test
    fun testAdd201() {
        val name = "fsdafa"
        val tableOne = TableOne().apply {
            this.name = name
            this.value = 101.0
        }
        val result = testRestTemplate.postForEntity("/add", tableOne, TableOne::class.java)
        Assertions.assertEquals(HttpStatus.CREATED, result.statusCode)
        Assertions.assertEquals(result.body!!.name, name)
    }

    @Test
    fun testSpEL() {
        val parser = SpelExpressionParser()
        val context = StandardEvaluationContext()
        context.beanResolver = beanBeanResolver
        val testData = TestData(test2 = 100)
        val exp2 = parser.parseExpression("@testComponent.match(test2)")
        println("Expression Value: ${exp2.getValue(context, testData)}")
        println(testData)
    }

}