package com.test.kotlin.component

import org.springframework.stereotype.Component

@Component("testComponent")
class TestComponent {
    fun match(number: Long?) =
            when {
                number == null -> {
                    "参数为空"
                }
                number <= 100 -> {
                    "小于等于100"
                }
                else -> {
                    "大于100"
                }
            }
}