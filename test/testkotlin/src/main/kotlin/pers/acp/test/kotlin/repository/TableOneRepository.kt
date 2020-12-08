package pers.acp.test.kotlin.repository

import pers.acp.test.kotlin.base.BaseRepository
import pers.acp.test.kotlin.entity.TableOne
import java.util.*

/**
 * @author zhangbin by 28/04/2018 13:02
 * @since JDK 11
 */
interface TableOneRepository : BaseRepository<TableOne, Long> {
    fun findByName(name: String): Optional<TableOne>
    fun deleteByIdIn(ids: List<Long>)
}