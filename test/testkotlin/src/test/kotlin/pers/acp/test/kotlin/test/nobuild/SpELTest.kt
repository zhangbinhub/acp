package pers.acp.test.kotlin.test.nobuild

import pers.acp.test.kotlin.component.BeanBeanResolver
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import pers.acp.test.kotlin.test.TestData
import pers.acp.test.kotlin.BaseTest


/**
 * @author zhangbin by 28/04/2018 17:18
 * @since JDK 11
 */
internal class SpELTest : BaseTest() {

    @Autowired
    lateinit var beanBeanResolver: BeanBeanResolver

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