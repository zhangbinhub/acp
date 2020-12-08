package pers.acp.test.kotlin.controller

import pers.acp.test.kotlin.domain.TableOneDomain
import pers.acp.test.kotlin.entity.TableOne
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.web.bind.annotation.*
import pers.acp.spring.boot.enums.ResponseCode
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhangbin by 28/04/2018 13:06
 * @since JDK 11
 */
@RestController
class KotlinController(private val tableOneDomain: TableOneDomain, private val log: LogAdapter) {

    @GetMapping("/query", produces = [(MediaType.APPLICATION_JSON_VALUE)])
    @Throws(ServerException::class, JpaObjectRetrievalFailureException::class)
    fun doQuery(@RequestParam name: String): ResponseEntity<TableOne> =
            try {
                tableOneDomain.query(name)
                        .let {
                            if (it.isPresent) {
                                ResponseEntity.ok(it.get())
                            } else {
                                throw ServerException(ResponseCode.DbError.value, "没有记录")
                            }
                        }
            } catch (e: Exception) {
                log.error(e.message ?: "", e)
                if (e !is ServerException) throw ServerException(ResponseCode.ServiceError)
                throw e
            }

    @PostMapping("/add", produces = [(MediaType.APPLICATION_JSON_VALUE)])
    @Throws(ServerException::class, JpaObjectRetrievalFailureException::class)
    fun doAdd(@RequestBody tableOne: TableOne): ResponseEntity<TableOne> =
            try {
                tableOneDomain.create(tableOne)
                        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
            } catch (e: Exception) {
                log.error(e.message ?: "", e)
                if (e !is ServerException) throw ServerException(ResponseCode.ServiceError)
                throw e
            }

}