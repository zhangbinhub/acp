package pers.acp.test.kotlin

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * @author zhangbin by 28/04/2018 17:17
 * @since JDK 11
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [(TestKotlinApplication::class)], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseTest {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

}