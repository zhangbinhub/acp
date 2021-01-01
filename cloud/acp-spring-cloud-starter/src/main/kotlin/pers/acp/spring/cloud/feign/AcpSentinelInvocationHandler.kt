package pers.acp.spring.cloud.feign

import com.alibaba.cloud.sentinel.feign.SentinelContractHolder
import com.alibaba.csp.sentinel.Entry
import com.alibaba.csp.sentinel.EntryType
import com.alibaba.csp.sentinel.SphU
import com.alibaba.csp.sentinel.Tracer
import com.alibaba.csp.sentinel.context.ContextUtil
import com.alibaba.csp.sentinel.slots.block.BlockException
import feign.Feign
import feign.InvocationHandlerFactory
import feign.Target
import feign.Target.HardCodedTarget
import feign.Util
import org.springframework.cloud.openfeign.FallbackFactory
import java.lang.IllegalArgumentException
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * 支持自动降级注入 重写 {@link com.alibaba.cloud.sentinel.feign.SentinelInvocationHandler}
 * @author zhangbin
 * @date 2020-12-31
 */
class AcpSentinelInvocationHandler : InvocationHandler {
    companion object {
        fun toFallbackMethod(dispatch: Map<Method, InvocationHandlerFactory.MethodHandler>): Map<Method, Method> {
            val result: MutableMap<Method, Method> = LinkedHashMap()
            for (method in dispatch.keys) {
                method.isAccessible = true
                result[method] = method
            }
            return result
        }
    }

    private var target: Target<*>? = null
    private var dispatch: Map<Method, InvocationHandlerFactory.MethodHandler>? = null
    private var fallbackFactory: FallbackFactory<*>? = null

    private var fallbackMethodMap: Map<Method, Method>? = null

    constructor(
        target: Target<*>?, dispatch: Map<Method, InvocationHandlerFactory.MethodHandler>,
        fallbackFactory: FallbackFactory<*>?
    ) {
        this.target = Util.checkNotNull(target, "target")
        this.dispatch = Util.checkNotNull(dispatch, "dispatch")
        this.fallbackFactory = fallbackFactory
        fallbackMethodMap = toFallbackMethod(dispatch)
    }

    constructor(target: Target<*>?, dispatch: Map<Method, InvocationHandlerFactory.MethodHandler>) {
        this.target = Util.checkNotNull(target, "target")
        this.dispatch = Util.checkNotNull(dispatch, "dispatch")
    }

    @Throws(Throwable::class)
    override fun invoke(
        proxy: Any?,
        method: Method,
        parameters: Array<Any?>?
    ): Any {
        // other target type using default strategy
        // throw exception if fallbackFactory is null
        // shouldn't happen as method is public due to being an
        // interface
        // fallback handle
        // resource default is HttpMethod:protocol://url
        (parameters ?: emptyArray()).let { args ->
            when (method.name) {
                "equals" -> {
                    return try {
                        val otherHandler: Any? = if (args.isNotEmpty() && args[0] != null) Proxy.getInvocationHandler(
                            args[0]
                        ) else null
                        equals(otherHandler)
                    } catch (e: IllegalArgumentException) {
                        false
                    }
                }
                "hashCode" -> {
                    return hashCode()
                }
                "toString" -> {
                    return toString()
                }
                // only handle by HardCodedTarget
                else -> {
                    val result: Any
                    val methodHandler = dispatch!![method]
                    // only handle by HardCodedTarget
                    if (target is HardCodedTarget) {
                        val hardCodedTarget = target
                        val methodMetadata = SentinelContractHolder.METADATA_MAP[hardCodedTarget!!.type().name
                                + Feign.configKey(hardCodedTarget.type(), method)]
                        // resource default is HttpMethod:protocol://url
                        if (methodMetadata == null) {
                            result = methodHandler!!.invoke(args)
                        } else {
                            val resourceName = (methodMetadata.template().method().toUpperCase()
                                    + ":" + hardCodedTarget.url() + methodMetadata.template().path())
                            var entry: Entry? = null
                            try {
                                ContextUtil.enter(resourceName)
                                entry = SphU.entry(resourceName, EntryType.OUT, 1, *args)
                                result = methodHandler!!.invoke(args)
                            } catch (ex: Throwable) {
                                // fallback handle
                                if (!BlockException.isBlockException(ex)) {
                                    Tracer.trace(ex)
                                }
                                return if (fallbackFactory != null) {
                                    try {
                                        fallbackMethodMap!![method]?.invoke(fallbackFactory!!.create(ex), *args)
                                            ?: throw IllegalAccessException("not find method: $method")
                                    } catch (e: IllegalAccessException) {
                                        // shouldn't happen as method is public due to being an
                                        // interface
                                        throw AssertionError(e)
                                    } catch (e: InvocationTargetException) {
                                        throw AssertionError(e.cause)
                                    }
                                } else {
                                    // throw exception if fallbackFactory is null
                                    throw ex
                                }
                            } finally {
                                entry?.exit(1, *args)
                                ContextUtil.exit()
                            }
                        }
                    } else {
                        // other target type using default strategy
                        result = methodHandler!!.invoke(args)
                    }
                    return result
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is AcpSentinelInvocationHandler) {
            return target == other.target
        }
        return false
    }

    override fun hashCode(): Int {
        return target.hashCode()
    }

    override fun toString(): String {
        return target.toString()
    }
}