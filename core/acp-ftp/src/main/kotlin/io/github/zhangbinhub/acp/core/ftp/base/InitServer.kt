package io.github.zhangbinhub.acp.core.ftp.base

import io.github.zhangbinhub.acp.core.ftp.user.UserFactory
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.ConcurrentHashMap

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
abstract class InitServer {
    companion object {

        private val userFactoryMap = ConcurrentHashMap<String, UserFactory>()

        @JvmStatic
        internal fun addUserFactory(userFactory: UserFactory) {
            userFactoryMap[userFactory.javaClass.canonicalName] = userFactory
        }

        @JvmStatic
        @Throws(
            ClassNotFoundException::class,
            NoSuchMethodException::class,
            IllegalAccessException::class,
            InvocationTargetException::class,
            InstantiationException::class
        )
        internal fun getUserFactory(className: String): UserFactory {
            var userFactory = userFactoryMap[className]
            if (userFactory == null) {
                userFactory = Class.forName(className).getDeclaredConstructor().newInstance() as UserFactory
            }
            return userFactory
        }
    }

}