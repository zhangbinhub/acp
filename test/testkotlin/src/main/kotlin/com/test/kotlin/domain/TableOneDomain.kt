package com.test.kotlin.domain

import com.test.kotlin.entity.TableOne
import com.test.kotlin.entity.TableTwo
import com.test.kotlin.repository.MemberTwoRepository
import com.test.kotlin.repository.TableOneRepository
import com.test.kotlin.repository.TableTwoRepository
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * @author zhangbin by 28/04/2018 13:07
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class TableOneDomain(private val tableOneRepository: TableOneRepository,
                     private val tableTwoRepository: TableTwoRepository,
                     private val memberTwoRepository: MemberTwoRepository) {

    @Transactional
    fun create(tableOne: TableOne): TableOne = tableOneRepository.save(tableOne)

    fun query(name: String): Optional<TableOne> = tableOneRepository.findByName(name)

    fun all(specification: Specification<TableOne>?): MutableList<TableOne> = tableOneRepository.findAll(specification)

    @Transactional
    fun testDeleteTwo(name: String) {
        memberTwoRepository.deleteAllByName(name)
        tableTwoRepository.deleteAllByName(name)
    }


    @Transactional
    fun testSaveTwo(tableTwo: TableTwo) {
        tableTwoRepository.save(tableTwo)
    }
}