package pers.acp.test

import com.thoughtworks.xstream.annotations.XStreamAlias

/**
 * 请求系统头
 */
@XStreamAlias("root")
data class TestBean(
    @XStreamAlias("fileFlag")
    var fileFlag: String? = null,
    @XStreamAlias("BODY")
    var body: Body? = Body()
) {
    data class Body(
        /**
         * 手机号码
         */
        @XStreamAlias("mobile")
        var mobile: String? = null
    )
}
