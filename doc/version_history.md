## 版本更新记录
##### v6.5.0
> - Global
>   - [Upgrade] 升级 Gradle 至 6.7.1
>   - [Upgrade] gradle/dependencies.gradle 中移除 spring_boot、spring_cloud、alibaba_cloud，gradle.properties 中增加 springBootVersion、springCloudVersion、springCloudAlibabaVersion
>   - [Upgrade] 修改各模块build.gradle写法
>     - $versions.spring_boot replace to ${springBootVersion}
>     - $versions.spring_cloud replace to ${springCloudVersion}
>     - $versions.alibaba_cloud replace to ${springCloudAlibabaVersion}
>   - [Upgrade] 优化junit测试代码
> - acp-spring-boot-starter
>   - [Upgrade] controller切面中，google ImmutableList替换为Kotlin的MutableList
> - acp-spring-cloud-starter
>   - [Upgrade] AcpCloudResourceServerAutoConfiguration 中关闭 formLogin 登录界面
>   - [Upgrade] 增加依赖 org.springframework.cloud:spring-cloud-starter-bootstrap
>   - [Upgrade] 去除ribbon依赖
>   - [Upgrade] 更换security依赖
> - test
>   - [Upgrade] logback 配置属性重命名
>     - logging.pattern.rolling-file-name → logging.logback.rollingpolicy.file-name-pattern
>     - logging.file.clean-history-on-start → logging.logback.rollingpolicy.clean-history-on-start
>     - logging.file.max-size → logging.logback.rollingpolicy.max-file-size
>     - logging.file.total-size-cap → logging.logback.rollingpolicy.total-size-cap
>     - logging.file.max-history → logging.logback.rollingpolicy.max-history
>   [Upgrade] admin-server、gateway-server 去除 ribbon 依赖
> - [Upgrade] 升级依赖
>   - kotlin coroutines 1.4.2
>   - kotlin 1.4.21
>   - Spring Boot 2.4.1
>   - Spring Cloud 2020.0.0
>   - knife4j 3.0.2
>   - jupiter 5.7.0
>   - junit-platform 1.7.0
>   - netty 4.1.55.Final
>   - commons-net 3.7.2
>   - jsch 0.1.55
>   - sshd-sftp 2.5.1
>   - jackson 2.11.3
>   - joda time 2.10.8
>   - commons-codec 1.15
>   - commons-text 1.9
>   - commons-lang3 3.11
>   - xstream 1.4.14
>   - jackson 2.11.3
>   - zip4j 2.6.4
>   - mysql 8.0.22
>   - oracle 19.8.0.0
>   - sqlserver 8.4.1.jre8
>   - postgresql 42.2.18
##### v6.4.5
> - Global
>   - [Upgrade] 升级 Gradle 至 6.6.1
>   - [Upgrade] server.tomcat.max-threads 修改为 server.tomcat.threads.max
>   - [Upgrade] 修改启动脚本
> - acp-core
>   - [Upgrade] 增加 PGP 文件加密工具类
>   - [Upgrade] 优化异步任务异常处理
>   - [Upgrade] 增加数字转中文大写方法，优化金额转中文大写代码规范
>   - [Upgrade] 优化正则表达式匹配，使用Kotlin的Regex对象
>   - [Fix] 修复CommonUtils.replaceVar只替换一个变量的BUG
> - acp-ftp
>   - [Upgrade] FtpClient 增加获取文件列表方法
>   - [Upgrade] SftpClient 增加获取文件列表方法
> - acp-file
>   - [Upgrade] ExcelService 增加生成Workbook接口
>   - [Upgrade] ExcelService 增加通过Workbook循环读数据接口
>   - [Fix] ExcelService 修复读数据时，指定行数或列数超过文件内容时，仍然继续读并返回空的问题
> - acp-spring-boot-starter
>   - [Upgrade] controller切面强制使用BootLogAdapter进行日志记录
>   - [Upgrade] controller切面日志配置项变更，acp.controller-aspect变更为acp.controller-log
> - acp-spring-cloud-starter
>   - [Upgrade] 分布式锁接口增加是否可重入参数
>   - [Upgrade] 优化重复提交拦截 RestControllerRepeatAspect
>   - [Upgrade] 远程日志收集对象取消Throwable属性
    - [Fix] 防重复提交，拦截方法没有返回值时报错
> - [Upgrade] 依赖降级
>   - jsch 0.1.54
> - [Upgrade] 升级依赖
>   - kotlin 1.4.10
>   - Spring Boot 2.3.4.RELEASE
>   - Spring Cloud Hoxton.SR8
>   - Spring Cloud Alibaba 2.2.3.RELEASE
>   - Spring Boot Admin 2.3.0
>   - jupiter 5.6.2
>   - junit platform 1.6.2
>   - commons-codec 1.14
>   - commons-lang3 3.10
>   - jackson 2.11.1
>   - hikaricp 3.4.5
>   - zip4j 2.6.1
>   - kotlin coroutines 1.3.8
>   - netty 4.1.52.Final
>   - okhttp 3.14.9
>   - knife4j 2.0.4
##### v6.4.4
> - Global
>   - [Upgrade] 修改 gradle 脚本，release 任务根据参数 -Pactive 打包不同的环境配置
> - acp-core
>   - [Upgrade] LogFactory 记录日志之前增加判断
> - acp-spring-boot-starter
>   - [Upgrade] 使用 knife4j 替换 Swagger 生成Api文档
> - acp-spring-cloud-starter
>   - [Upgrade] CloudLogAdapter 记录日志之前增加判断
> - acp-ftp
>   - [Upgrade] 修改 sftp server 代码
> - [Upgrade] 升级依赖
>   - Spring Boot 2.2.7.RELEASE
>   - Spring Boot Admin 2.2.3
>   - okhttp 3.14.8
>   - Postgresql 42.2.12
>   - Mysql 8.0.20
>   - Joda Time 2.10.6
>   - Netty 4.1.49.Final
>   - Jackson 2.10.4
>   - Zip4j 2.5.2
>   - Hikaricp 3.4.3
>   - Flying Saucer 9.1.20
>   - Jsoup 1.13.1
>   - batik 1.13
>   - poi 4.1.2
>   - sshd-sftp 2.4.0
>   - xstream 1.4.12
##### v6.4.3
> - [Upgrade] 升级 Gradle 至 6.3
> - [Upgrade] acp-core 中 BaseException 继承的类由 Exception 替换为 RuntimeException
> - [Upgrade] 修改 nacos 命名空间id
> - [Upgrade] feign 关闭 sentinel
> - [Upgrade] 注释 sentinel nacos 数据源配置，待 sentinel 支持 nacos 1.2+ 动态数据源时再开启
> - [Upgrade] test:oauth-server 新增自定义token验证接口，security.oauth2.resource.token-info-uri 配置指向新的接口
> - [Fix] 修复 CalendarTools 中季度内月号的计算
> - [Upgrade] 升级依赖项
>   - Spring Boot 2.2.6.RELEASE
>   - Spring Cloud Hoxton.SR4
>   - Spring Cloud Alibaba 2.2.1.RELEASE
>   - okhttp 3.14.7
>   - Postgresql 42.2.11
>   - Kotlin Coroutines 1.3.5
>   - Kotlin 1.3.72
>   - Freemarker 2.3.30
>   - Netty 4.1.48.Final
>   - Jackson 2.10.3
>   - Zip4j 2.5.1
##### v6.4.2
> - [Upgrade] 自动配置类使用@Configuration(proxyBeanMethods=false)
> - [Upgrade] 升级 Spring Cloud 至 Hoxton.SR2
> - [Upgrade] 升级 Gradle 至 6.2
> - [Upgrade] acp-spring-boot-starter 显示依赖 spring-boot-starter-validation
> - [Fix] 修复使用cloud日志服务客户端时，本地记录日志定位不准确的问题
##### v6.4.1
> - [Upgrade] 调整数据库驱动，按需自行引入
> - [Upgrade] 升级 Spring Boot 至 2.2.4.RELEASE
> - [Upgrade] Spring Boot Admin 升级至 2.2.2
> - [Upgrade] 升级 Gradle 至 6.1.1
> - [Upgrade] Spring Cloud Alibaba 升级至 2.2.0.RELEASE
> - [Upgrade] gateway 中启用 spring-cloud-loadbalancer
> - [Upgrade] Spring Boot Admin 中启用 spring-cloud-loadbalancer
> - [Upgrade] 升级依赖项
>   - netty to 4.1.45.Final
>   - okhttp to 3.14.6
>   - mysql to 8.0.19
>   - postgresql to 42.2.9
>   - slf4j to 1.7.30
>   - jackson to 2.10.2
>   - hikaricp to 3.4.2
> - [Fix] 修复读取文件内容方法中，部分字符截断导致乱码的问题
##### v6.4.0
> - [Upgrade] 升级 Spring Boot 至 2.2.2.RELEASE
> - [Upgrade] 升级 Spring Cloud 至 Hoxton.SR1
> - [Upgrade] 修改application.yaml中logging配置
> - [Upgrade] 去除application.yaml中对hibernate.dialect方言的显示配置
> - [Upgrade] MediaType.APPLICATION_JSON_UTF8_VALUE 替换为 MediaType.APPLICATION_JSON_VALUE
> - [Upgrade] 修改test依赖
> - [Upgrade] cloud中使用spring-cloud-loadbalancer替换ribbon
> - [Upgrade] cloud中修改feign相关配置
> - [Upgrade] 升级依赖项
>   - jupiter to 5.5.2
>   - junit_platform to 1.5.2
>   - kotlin_coroutines to 1.3.3
>   - commons_codec to 1.13
>   - bouncycastle to 1.64
>   - jackson to 2.10.1
>   - zip4j to 2.2.8
>   - hikaricp to 3.4.1
>   - mssqljdbc to 7.4.1.jre8
>   - spring boot admin to 2.2.1
>   - flying_saucer to 9.1.19
>   - okhttp to 3.12.0
##### v6.3.4
> - [Upgrade] 修改@Api写法
> - [Upgrade] 读取文件内容方法，放入CommonTools中
> - [Upgrade] CommonTools 中增加写入文件的方法
> - [Upgrade] FileDownLoadHandle 支持自定义延迟删除文件等待时间参数，单位毫秒
> - [Upgrade] 优化xml转bean，忽略未知属性
##### v6.3.3
> - [Upgrade] 修改注释
> - [Upgrade] 修改 test 中 feign 写法
> - [Upgrade] 升级 Spring Boot 至 2.1.11.RELEASE
> - [Upgrade] 升级 kotlin 至 1.3.61
> - [Upgrade] 升级依赖项
>   - zip4j to 2.2.7
>   - sshd-sftp to 2.3.0
>   - freemarker to 2.3.29
>   - batik to 1.12
>   - poi to 4.1.1
##### v6.3.2
> - [Upgrade] 修改注释
> - [Upgrade] 使用安全的方式进行泛型转换
> - [Upgrade] 修改防重请求异常处理逻辑
> - [Upgrade] 静态常量使用object替换interface
##### v6.3.1
> - [Upgrade] 升级 spring-cloud-alibaba 至 2.1.1.RELEASE
> - [Upgrade] 升级 kotlin 至 1.3.60
> - [Upgrade] 修改 cloud 测试模块中的 nacos 配置
> - [Upgrade] 更新 Spring Cloud 至 Greenwich.SR4
> - [Upgrade] junit5 降级至 5.3.2
> - [Upgrade] 升级 gradle 至 6.0.1
##### v6.3.0
> - [Upgrade] 修改防重请求验证时，获取分布式锁的默认超时时间为1秒
> - [Upgrade] test中修改feign对应配置参数
> - [Upgrade] 升级 Spring Boot 至 2.1.10.RELEASE
> - [Upgrade] 升级 gradle 至 6.0
> - [Upgrade] 去除 jaxb-runtime 依赖
> - [Upgrade] 取消单独的jdk8分支，代码及工程配置统一
> - [Upgrade] 取消依赖 api "org.jolokia:jolokia-core"
> - [Upgrade] 升级依赖项
>   - junit5 to 5.5.2
>   - joda time to 2.10.5
>   - commons-text to 1.8
>   - slf4j to 1.7.29
>   - jackson to 2.9.10
>   - zip4j to 2.2.4
>   - mysql to 8.0.18
>   - netty to 4.1.43.Final
##### v6.2.2
> - [Upgrade] 修改 build.gradle，支持 junit5 自动化测试
> - [Upgrade] 优化文件下载工具类，支持断点续传
> - [Upgrade] 版本号升级为6.2.2
> - [Upgrade] 升级 gradle 至 5.6.3
> - [Upgrade] 规范文件绝对路径写法，使用canonicalPath
> - [Upgrade] 优化 RestControllerAspect 日志
> - [Fix] 修复CloudLogAdapter中调用类和行号获取不正确的问题
##### v6.2.1
> - [Upgrade] 升级 Spring Boot 至 2.1.9.RELEASE
> - [Upgrade] 升级依赖项
>   - joda time to "2.10.4"
>   - kotlinx-coroutines-core to "1.3.2"
>   - Postgresql to "42.2.8"
##### v6.2.0
> - [Upgrade] Cloud 服务注册发现中心更换为 Nacos
> - [Upgrade] Cloud 配置更换为 Nacos
> - [Upgrade] acp-spring-cloud-starter 中去除 spring-cloud-config 中心相关配置，调整 feignHystrixConcurrencyStrategy 实例化策略
> - [Upgrade] Cloud 中删除 eureka-server 和 config-server
> - [Upgrade] 修改 Cloud 中各服务的配置文件，增加 nacos 相关配置，规范文件扩展名
> - [Upgrade] Cloud Hystrix 更换为 Sentinel
##### v6.1.6
> - [Upgrade] 升级 Spring Boot 至 2.1.8.RELEASE
> - [Upgrade] 升级 Spring Cloud 至 Greenwich.SR3
> - [Upgrade] acp-spring-cloud-starter 中增加配置类 AcpCloudMethodSecurityConfig，方法上 @PreAuthorize / @PostAuthorize 注解支持表达式 #oauth2.hasScope
> - [Upgrade] core 中删除 acp-core-orm 模块
> - [Upgrade] 修改异步任务，默认使用无限制协程 Dispatchers.Unconfined
> - [Upgrade] 升级依赖项
>   - netty to "4.1.39.Final"
>   - kotlinx-coroutines-core to "1.3.1"
>   - commons-codec to "1.12"
>   - slf4j to "1.7.28"
> - [Fix] 修复 feign 请求开启 hystrix 时，出现 kotlin null 异常的问题
##### v6.1.5
> - [Upgrade] 升级 gradle 至 5.6.2
> - [Upgrade] 优化 acp-file 中 Excel 处理
> - [Upgrade] 优化重复请求拦截处理
> - [Upgrade] 优化Aspect执行顺序
##### v6.1.4
> - [Upgrade] 升级 kotlin 至 1.3.50
##### v6.1.3
> - [Upgrade] 优化gradle脚本，build任务之后将打好的jar包放入项目根路径下的release目录
> - [Upgrade] 优化自动配置，自动配置中不使用LogAdapter
> - [Upgrade] HttpClientBuilder 增加 readTimeOut 和 writeTimeOut 配置
> - [Upgrade] HttpClientBuilder 中 retryOnConnectionFailure 默认改为 true，解决部分场景中连接失败的问题
##### v6.1.2
> - [Upgrade] 优化 gradle 脚本，每个模块重新设置 group
> - [Upgrade] 优化自动配置顺序
> - [Upgrade] 升级 Spring Boot 至 2.1.7.RELEASE
> - [Upgrade] 升级依赖项
>   - netty to "4.1.38.Final"
>   - joda time to "2.10.3"
>   - mysql-connector-java to "8.0.17"
>   - postgresql to "42.2.6"
##### v6.1.1
> - [Upgrade] 优化 gradle 脚本
> - [Upgrade] acp-spring-cloud-starter 增加分布式锁接口
> - [Upgrade] 增加基于分布式锁的请求防重注解 pers.acp.spring.cloud.annotation.AcpCloudDuplicateSubmission
> - [Fix] 修复压缩文件时仅有文件夹报错的问题
##### v6.1.0
> - [Upgrade] 调整 gradle 脚本
> - [Upgrade] 优化部分代码
> - [Upgrade] 调整 acp-spring-boot-starter 及 acp-spring-cloud-starter 自动配置写法，去除 ComponentScan 包扫描
> - [Upgrade] acp-spring-boot-starter 中增加日志适配器接口，根据不同场景（独立SpringBoot项目SpringCloud项目）进行更换日志输出方式，并在 acp-spring-boot-starter 及 acp-spring-cloud-starter 中分别实现了日志适配器
> - [Upgrade] 优化自动配置写法，方便扩展
##### v6.0.1
> - [Upgrade] 优化 RestControllerAspect 切面日志处理
> - [Upgrade] testspringboot 增加异步请求接口demo
> - [Upgrade] 升级 docker 中间件 elasticsearch 相关组件为 7.2.0
> - [Upgrade] zip 文件压缩和解压使用 zip4j
##### v6.0.0
> - [Upgrade] 删除 acp-spring-boot-starter-ws 和 acp-webservice 模块，彻底废弃 webservice 相关功能的支持
> - [Upgrade] java 1.8 环境中删除 testscala 模块
> - [Upgrade] 升级 kotlin 版本至 1.3.41，至此已兼容 java 11
> - [Upgrade] java 11 环境下增加 testkotlin 模块
> - [Upgrade] 大部分底层代码使用 kotlin 重写，与 java 并存，提升执行效率，去除冗余
> - [Upgrade] 更新 gradle 至 5.5.1
> - [Upgrade] gradle 脚本中 bootJar 内增加 Kotlin Version
> - [Upgrade] Spring Boot Starter 中 annotationProcessor 替换为 kapt
> - [Upgrade] 优化 gradle 脚本
> - [Upgrade] 更新 Spring Boot Admin 至 2.1.6
> - [Upgrade] 修改部分配置参数
>   - acp.controller-aspect.no-log-uri-regexes 修改为 acp.controller-aspect.no-log-uri-regular
>   - acp.schedule.crons 修改为 acp.schedule.cron
##### v5.2.1
> - [Upgrade] 重写 AcpClient ，底层将 Apache HttpClient 替换为 OKHttp
> - [Upgrade] acp-spring-cloud-starter 中资源服务器客户端修改 AcpClient 参数
> - [Upgrade] acp-spring-cloud-starter 中配置服务器客户端修改 AcpClient 参数
> - [Upgrade] 更新 gradle 至 5.5
> - [Upgrade] 修改 test/cloud 下各模块配置文件，HttpClient 替换为 OKHttp
> - [Upgrade] 升级版本号为 5.2.1
> - [Upgrade] 更新文档
##### v5.2.0
> - [Upgrade] testspringboot 增加 Map 接收参数的 Demo
> - [Upgrade] testspringboot 增加使用 RestTemplate 进行 post 方式的 form 提交单元测试
> - [Upgrade] 升级 Spring Boot 至 2.1.6.RELEASE
> - [Upgrade] 升级 Spring Cloud 至 Greenwich.SR2
> - [Upgrade] acp-spring-boot-start 模块从 core 模块中移入 boot 模块，包名更新为 pers.acp.spring.boot
> - [Upgrade] acp-spring-boot-start-common 模块更名为 acp-spring-boot-start，包名更新为 pers.acp.spring.cloud
> - [Upgrade] 修改 ftp、sftp、webservice 服务启动方式及配置方式，同时在使用 spring boot 的情况下去除 acp.properties 配置文件
> - [Upgrade] 增加 acp-spring-boot-starter-ftp 模块，自动配置ftp及sftp服务
> - [Upgrade] 增加 acp-spring-boot-starter-ws 模块，自动配置webservice服务
> - [Upgrade] 升级依赖项
>   - Apache HttpClient to 4.5.9
>   - jackson to 2.9.9
>   - sshd-sftp to 2.2.0
> - [Upgrade] 优化源代码，增加大量注释，并更新文档
##### v5.1.5
> - [Upgrade] 升级 Spring Boot 至 2.1.5.RELEASE
> - [Upgrade] 升级 Spring Boot Admin 至 2.1.5
> - [Upgrade] 升级依赖项
> - [Upgrade] 升级 gradle 至 5.4.1
> - [Upgrade] cloud 下的 demo 模块全部移入 test/cloud 中
> - [Upgrade] 更新文档
> - [Upgrade] 优化 acp-file 模块，去除无用代码
> - [Upgrade] 优化 acp-core 中线程池相关参数及注释
> - [Upgrade] 使用 joda-time 替换 jdk 日期时间及日历处理
> - [Upgrade] 优化代码执行效率
> - [Upgrade] 优化核心任务基类，实现Callable接口，替换Runnable接口
##### v5.1.4.2
> - [Upgrade] 文件压缩解压公共方法从 acp-file 迁移到 acp-core，使用 CommonTools 类
> - [Upgrade] 升级 gradle 至 5.4
##### v5.1.4.1
> - [Upgrade] 升级 docker 镜像版本
> - [Upgrade] 优化 testspringboot 中多数据源配置写法
> - [Upgrade] 更新文档
> - [Upgrade] 修复通过模板文件生成 excel 时，偶尔出现 NullPointException 的问题
##### v5.1.4
> - [Upgrade] 升级 Spring Boot 至 2.1.4.RELEASE
> - [Upgrade] 更新依赖包版本：
>   - jaxb_runtime (2.3.2)
>   - httpclient (4.5.8)
##### v5.1.3.2
> - [Upgrade] 升级 Spring Cloud 至 Greenwich.SR1
> - [Upgrade] 升级 gradle 至 5.3.1
> - [Fix] 修复定时任务线程池使用，兼容最新版本
##### v5.1.3.1
> - [Upgrade] 升级 gradle 至 5.3
> - [Upgrade] 优化重写 acp-spring-cloud-starter-common 中日志消息处理
> - [Upgrade] 增加日志服务相关配置
> - [Upgrade] 修改 demo 中日志服务相关配置
> - [Upgrade] jdk11 版本中移除 scala 和 kotlin 相关测试代码
> - [Upgrade] 更新文档
##### v5.1.3
> - [Upgrade] kotlin 更新至 1.3.21
> - [Upgrade] tcp/udp server 相关配置移入 yml 中
> - [Upgrade] 增加资源服务器开关配置，默认为 true
> - [Upgrade] 增加资源服务器不进行认证的url配置
> - [Upgrade] core:acp-file gradle脚本中删除冗余的依赖
> - [Upgrade] gradle 更新至 5.2.1
> - [Upgrade] 更新 SpringBoot 至 2.1.3.RELEASE
> - [Upgrade] 更新依赖包版本：
>   - spring boot admin (2.1.3)
>   - httpclient (4.5.7)
>   - jaxws-rt (2.3.2)
>   - mysql-connector-java (8.0.15)
> - [Upgrade] gradle 公共脚本中增加部署至 maven 仓库任务配置（包含本地和远程）
> - [Upgrade] cloud 下新增配置中心demo，其余服务连接配置中心获取配置信息，并通过bus进行动态刷新
> - [Upgrade] acp-spring-cloud-starter-common 添加自定义 PropertySourceLocator，供 config-client 获取配置信息
> - [Upgrade] 自定义的 RestTemplate 使用 HttpClient，且 feign 使用 httpclient 配置
> - [Upgrade] 规范各 demo 的 yml 配置文件书写
> - [Upgrade] 优化 acp-client 中 HttpClientBuilder
> - [Upgrade] 更新文档
> - [Upgrade] 优化线程池、任务代码，最少开销的情况下保证原子性
> - [Upgrade] 优化线程池使用
> - [Upgrade] acp-client 中 tcp 和 udp client 使用 netty 替换 mina
> - [Upgrade] acp-spring-boot-starter 中 tcp 和 udp 服务端 使用 netty 替换 mina
> - [Upgrade] acp-spring-cloud-starter-common 增加自定义 token 异常和权限异常响应配置
> - [Upgrade] 优化代码
> - [Delete] 删除无用测试代码
> - [Fix] 规范全局异常处理，调整对应异常响应的 HttpStatus
##### v5.1.2
> - 更新 SpringBoot 至 2.1.2.RELEASE
> - 更新 SpringCloud 至 Greenwich.RELEASE
> - 更新依赖包版本：
>   - spring boot admin (2.1.2)
>   - commons_text (1.6)
>   - xstream (1.4.11.1)
>   - jackson (2.9.8)
>   - poi (4.0.1)
>   - flying-saucer-pdf-itext5 (9.1.16)
> - 更新 gradle 至 5.1.1
> - 更新 kotlin 至 1.3.11
> - cloud 模块下 docker-compose-base.yml 文件修改，修改 zookeeper 端口号，增加 kafka-manager
> - 更新 spring-data-jpa 数据库连接配置及多数据源写法
> - 修改 spring boot 中 logback 配置
> - 优化定时器配置
> - acp-file 去除 jxl 相关依赖，不再封装 jxl 相关操作
> - 剥离订制工程 [management](https://github.com/zhangbin1010/acp-ace-php-back)
> - 修改核心模块名称
> - cloud 模块下 gateway-server 支持跨域
> - cloud 模块下，oauth-server 优化 token 服务配置，方便自定义扩展
> - 优化全局异常处理，增加 ErrorVO
> - 优化 oauth2 demo
> - cloud helloworld 中增加使用 RestTemplate 通过服务名调用远程服务的例子
> - 优化 REST 日志切片
> - 修改 jdbc mysql 链接字符串，支持 utf8mb4 字符集
> - 修改 DockerFile 内容，通过 kafka 传递信息给 zipkin server，gateway-server 去除 zipkin 相关依赖
> - 增加 swagger 开关配置
> - jackson 工具类增加 propertyNamingStrategy 参数
> - 自定义 spring boot start 中使用内置 jackson 进行 json 操作
> - 去除 Http Request 和 Response 的包裹封装，重写文件下载 controller
> - 核心工具类增加驼峰和下划线命名规则之间互相转化的方法
> - 规范配置项 acp.*
> - cloud 模块增加日志服务配置项
> - acp-spring-cloud-starter-common 增加 acp.cloud.oauth.oauth-server 配置项，oauth-server 可直接引用并修改配置项，不再需要单独编写自己的 ResourceServerConfiguration
> - 去除无用的依赖
> - cloud:oauth-server 中增加 authorization_code 方式配置 demo
> - 更新 cloud 下所有服务的 eureka 相关配置
> - 更新 spring boot admin 相关配置
> - 更新 docker-compose-base.yml 文件，集成 ELK 收集、监控日志
> - acp-spring-boot-starter 中去除 /download endpoint
> - 优化 CommonTools 工具类中文件删除方法的逻辑
> - Feign 传递 Authorization 时，Authorization 为空优化
> - acp-file 修复zip压缩和解压文件夹时的bug
> - 优化代码，减少冗余计算
> - 优化全局异常捕获
> - 优化文件下载 FileDownLoadHandle
> - 优化文件下载正则表达式匹配
##### v5.0.1
> - 更新 SpringBoot 至 2.0.6.RELEASE
> - 更新 SpringCloud 至 Finchley.SR2
> - 更新依赖包版本
> - cloud 模块下 docker-compose-base.yml 文件修改，注释暂时无用又占资源的服务
> - gateway 去除自定义 HiddenHttpMethodFilter ，官方已解决“Only one connection receive subscriber allowed” 的问题
> - 更新 cloud 中资源服务器配置，负载均衡直连 oauth2 进行认证，不经过 gateway
##### v5.0.0
> - 更新 gradle 至 4.10.2
> - 更新 kotlin 至 1.2.71
> - 优化系统停止时的过程，不使用单独的线程释放资源
> - 升级至 java 11，各依赖关系、兼容性、过时api改写
> - 剥离 webservice 服务端和客户端为单独的模块 acp-webservice，可选依赖
> - 剥离 ftp/sftp 服务端和客户端为单独的模块 acp-ftp，可选依赖
> - 删除 acp-spring-boot-starter-common 模块，代码合并入 acp-spring-boot-starter 模块
> - 增加链路监控，基于 zipkin 和 elasticsearch
> - cloud 模块下增加 dokerfile 文件夹
> - 更新依赖包版本
> - 优化 gateway 断路器
> - 更新 SpringBoot 至 2.0.5.RELEASE
> - 优化 gateway，增加 HiddenHttpMethodFilter，解决升级 SpringBoot 后 WebFlux 导致 “Only one connection receive subscriber allowed” 的问题
##### v4.0.8
> - 优化完善 acp:acp-spring-boot-starter 中的工具类
   pers/acp/springboot/core/tools/IpTools.java
> - BaseXML.java 中增加转换为 String 的方法
> - 优化 XStream 调用方式
> - 优化改造 socket 服务端和客户端
> - 优化代码书写
> - 优化线程池，支持自定义工作线程
> - 增加单机 SpringBoot 应用启停脚本
> - Cloud 完善监控服务，使用 Spring Boot Admin 2.0.2
> - 更新 gradle 至 4.10.1
> - 更新 scala 至 2.12.6
> - 更新 kotlin 至 1.2.70
##### v4.0.7
> - SpringCloud 升级至 Finchley.SR1
> - gradle 构建工具更新至 4.9
        gradlew wrapper --gradle-version=4.9 --distribution-type=all
> - 更新依赖包版本
> - kotlin 更新至 1.2.60
##### v4.0.6
> - SpringBoot 升级至 2.0.4.RELEASE
> - 更新依赖包版本
##### v4.0.5
> - kotlin 更新至 1.2.51
> - cloud 模块下新增 log-server，统一记录日志服务，从 kafka 消费记录日志的消息
> - cloud 模块下的 acp-spring-cloud-starter-common 新增记录日志实例，向 kafka 发送记录日志的消息
##### v4.0.4
> - SpringCloud 升级至 Finchley.RELEASE
> - gateway-server 增加限流配置
> - 更新 TODO.md 文档
##### v4.0.3
> - SpringBoot 升级至 2.0.3.RELEASE
> - 更新 jupiter 版本为 5.2.0
> - 修改 httpSecurity 策略配置
> - 优化 Feign 客户端访问资源服务器的认证配置
##### v4.0.2
> - SpringBoot 升级至 2.0.2.RELEASE
> - 修改数据源配置，连接池改用 HikariCP，去除 tomcat 连接池依赖
> - 升级 spring-cloud 版本为 Finchley.RC2
> - 增加相关注释
> - 修复 acp-client 中 webservice client 调用异常的bug，增加 webservice client 用法 demo
> - oauth-server 中，token 持久化到 redis
> - 修改 bouncycastle 依赖版本
##### v4.0.0
> - 优化 gradle 脚本，spring cloud 版本号写入 dependencies.gradle；删除 cloud 模块下的 build.gradle
> - 升级 spring-cloud 版本为 Finchley.RC1
> - 优化 hystrix 断路监控
> - 优化 feign oauth 验证
> - 升级 spring-boot 版本为 2.0.1.RELEASE
> - 更新各依赖包版本
> - gateway 增加自定义断路器
> - 修改 Eureka Server 及 Client 配置，优化服务注册/发现感知度
> - 优化 Controller 切片日志，重命名切片类
> - kotlin 版本升级至 1.2.41，增加 kotlin demo，test:testkotlin
> - 集成 junit5 单元测试，并增加测试用例编写，test:testspringboot、test:testkotlin
> - 取消 specification-arg-resolver 集成
> - 增加 scala demo, test:testscala
##### v3.9.0
> - 升级 spring-cloud 版本为 Finchley.M9
> - spring cloud 网关服务由 zuul 更换为 spring-cloud-gateway
> - gateway-server 增加路由配置
> - docker 打包脚本 demo，test:testspringboot
> - 修改 logback.xml 配置，输出到文件改为异步输出
> - acp-core 新增 hmac 加密工具类
> - 修改 acp-spring-boot-starter 包名
> - 修改 acp-spring-cloud-starter-common 包名
> - 资源服务器修改认证策略，改为 http 进行远程调用认证服务认证
> - acp-spring-cloud-starter-common 增加 feign 自定义并发策略，用以传递 ThreadLocal 中的信息，实现单点登录统一认证
> - cloud 原子服务、gateway 服务增加断路器超时和 feign 超时设置
> - cloud 原子服务、gateway 服务开启懒加载
> - 优化 gradle 构建脚本
> - kotlin 版本升级为 1.2.31
> - acp-client 优化 httpclient 写法
> - 增加 controller-aspect 配置项
> - 拆分 acp-core 中的 orm 自定义部分，新增 acp-core-orm 模块
> - 增加文档
##### v3.8.0
> - cloud 下新增 acp-spring-cloud-starter-common 模块
> - 优化 httpclient 封装
> - 升级 spring-cloud 版本为 Finchley.M8，完美兼容 spring-boot 2.0.0.RELEASE
> - 去除 zipkin-server 模块
> - 更新数据库驱动包版本
##### v3.7.0
> - PackageTools 从 acp-common-spring-boot-starter 模块移入 acp-spring-boot-starter 模块中
> - gateway 增加路由熔断逻辑
> - 增加熔断聚合监控
> - systemControl.initialization() 移入 acp-common-spring-boot-starter 模块，自动执行
> - 删除 turbine-server 模块，聚合监控合并入 admin-server 模块
> - 完善 admin-server 模块，监控各节点服务的健康状态
> - 增加 TODO.md 内容
> - 优化 acp-core 配置文件加载、读取
> - acp-common-spring-boot-starter 名称变更为 acp-spring-boot-starter-common
> - 优化 acp-spring-boot-starter-common 模块初始化预加载逻辑，找不到配置文件的情况下日志提醒，并且不开启其他服务，开发任何springboot应用都可直接引用
##### v3.6.0
> - test:testspringboot 模块增加 docker 镜像打包 demo
> - 优化cloud下各组建 demo
##### v3.5.0
> - cloud 增加 admin-server 模块
> - 统一设置 cloud 模块下各子模块依赖的 spring-boot 版本号
> - 调整acp下各模块依赖关系和代码
##### v3.0.0
> - 升级 spring-boot 版本至 1.5.9.RELEASE
> - 增加模块 acp-common-spring-boot-starter
##### v2.0.0
> - 整合spring-boot