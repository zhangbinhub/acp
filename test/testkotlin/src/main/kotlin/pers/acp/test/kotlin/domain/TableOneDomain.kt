package pers.acp.test.kotlin.domain

import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.test.kotlin.entity.TableOne
import pers.acp.test.kotlin.entity.TableTwo
import pers.acp.test.kotlin.repository.MemberTwoRepository
import pers.acp.test.kotlin.repository.TableOneRepository
import pers.acp.test.kotlin.repository.TableTwoRepository
import java.util.*

/**
 * @author zhangbin by 28/04/2018 13:07
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class TableOneDomain(
    private val tableOneRepository: TableOneRepository,
    private val tableTwoRepository: TableTwoRepository,
    private val memberTwoRepository: MemberTwoRepository
) {

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