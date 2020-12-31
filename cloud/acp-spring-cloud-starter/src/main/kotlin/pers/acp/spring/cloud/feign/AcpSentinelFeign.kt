package pers.acp.spring.cloud.feign

import com.alibaba.cloud.sentinel.feign.SentinelContractHolder
import feign.Contract
import feign.Feign
import feign.InvocationHandlerFactory
import feign.Target
import org.springframework.beans.BeansException
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.cloud.openfeign.FeignContext
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.util.ReflectionUtils
import org.springframework.util.StringUtils
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * 支持自动降级注入 重写 {@link com.alibaba.cloud.sentinel.feign.SentinelFeign}
 * @author zhangbin
 * @date 2020-12-31
 */
class AcpSentinelFeign {
    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }

    class Builder : Feign.Builder(), ApplicationContextAware {
        private var contract: Contract = Contract.Default()
        private var applicationContext: ApplicationContext? = null
        private var feignContext: FeignContext? = null
        override fun invocationHandlerFactory(
            invocationHandlerFactory: InvocationHandlerFactory
        ): Feign.Builder {
            throw UnsupportedOperationException()
        }

        override fun contract(contract: Contract): Builder {
            this.contract = contract
            return this
        }

        override fun build(): Feign {
            super.invocationHandlerFactory(object : InvocationHandlerFactory {
                override fun create(
                    target: Target<*>,
                    dispatch: Map<Method, InvocationHandlerFactory.MethodHandler>
                ): InvocationHandler {
                    // using reflect get fallback and fallbackFactory properties from
                    // FeignClientFactoryBean because FeignClientFactoryBean is a package
                    // level class, we can not use it in our package
                    val feignClientFactoryBean = applicationContext!!.getBean("&" + target.type().name)
                    val fallback = getFieldValue(
                        feignClientFactoryBean,
                        "fallback"
                    ) as Class<*>
                    val fallbackFactory = getFieldValue(
                        feignClientFactoryBean,
                        "fallbackFactory"
                    ) as Class<*>
                    var beanName = getFieldValue(
                        feignClientFactoryBean,
                        "contextId"
                    ) as String?
                    if (!StringUtils.hasText(beanName)) {
                        beanName = getFieldValue(feignClientFactoryBean, "name") as String?
                    }
                    val fallbackInstance: Any
                    // check fallback and fallbackFactory properties
                    if (Void.TYPE != fallback) {
                        fallbackInstance = getFromContext(
                            beanName, "fallback", fallback,
                            target.type()
                        )
                        return AcpSentinelInvocationHandler(
                            target, dispatch,
                            FallbackFactory.Default(fallbackInstance)
                        )
                    }
                    if (Void.TYPE != fallbackFactory) {
                        val fallbackFactoryInstance = getFromContext(
                            beanName, "fallbackFactory", fallbackFactory,
                            FallbackFactory::class.java
                        ) as FallbackFactory<*>
                        return AcpSentinelInvocationHandler(
                            target, dispatch,
                            fallbackFactoryInstance
                        )
                    }
                    return AcpSentinelInvocationHandler(target, dispatch)
                }

                private fun getFromContext(
                    name: String?, type: String,
                    fallbackType: Class<*>?, targetType: Class<*>
                ): Any {
                    val fallbackInstance = feignContext!!.getInstance<Any>(
                        name,
                        fallbackType
                    ) ?: throw IllegalStateException(
                        String.format(
                            "No %s instance of type %s found for feign client %s",
                            type, fallbackType, name
                        )
                    )
                    fallbackType?.let {
                        check(targetType.isAssignableFrom(it)) {
                            String.format(
                                "Incompatible %s instance. Fallback/fallbackFactory of type %s is not assignable to %s for feign client %s",
                                type, it, targetType, name
                            )
                        }
                    }
                    return fallbackInstance
                }
            })
            super.contract(SentinelContractHolder(contract))
            return super.build()
        }

        private fun getFieldValue(instance: Any, fieldName: String): Any? {
            ReflectionUtils.findField(instance.javaClass, fieldName)?.let {
                it.isAccessible = true
                try {
                    return it[instance]
                } catch (e: IllegalAccessException) {
                    // ignore
                }
            }
            return null
        }

        @Throws(BeansException::class)
        override fun setApplicationContext(applicationContext: ApplicationContext) {
            this.applicationContext = applicationContext
            feignContext = this.applicationContext!!.getBean(FeignContext::class.java)
        }
    }
}