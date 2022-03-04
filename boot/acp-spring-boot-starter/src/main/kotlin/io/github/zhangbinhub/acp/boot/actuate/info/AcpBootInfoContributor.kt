package io.github.zhangbinhub.acp.boot.actuate.info

import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor

class AcpBootInfoContributor : InfoContributor {
    override fun contribute(builder: Info.Builder) {
        builder.withDetail("acp", version())
    }

    private fun version(): Map<String, String?> {
        val info: MutableMap<String, String?> = HashMap()
        info["version"] = getVersion()
        return info
    }

    private fun getVersion(): String? {
        return AcpBootInfoContributor::class.java.getPackage()?.implementationVersion
    }
}