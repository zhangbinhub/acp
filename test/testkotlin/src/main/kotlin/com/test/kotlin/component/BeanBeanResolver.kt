package com.test.kotlin.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.expression.AccessException
import org.springframework.expression.BeanResolver
import org.springframework.expression.EvaluationContext
import org.springframework.stereotype.Component

@Component
class BeanBeanResolver @Autowired
constructor(private val componentMap: Map<String, TestComponent>) : BeanResolver {
    @Throws(AccessException::class)
    override fun resolve(context: EvaluationContext, beanName: String): Any {
        if (componentMap.containsKey(beanName)) {
            return componentMap.getValue(beanName)
        } else {
            throw AccessException("找不到bean【$beanName】")
        }
    }
}