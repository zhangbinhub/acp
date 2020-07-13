package com.test.kotlin.repository

import com.test.kotlin.base.BaseRepository
import com.test.kotlin.entity.TableOne
import java.util.*

/**
 * @author zhangbin by 28/04/2018 13:02
 * @since JDK 11
 */
interface TableOneRepository : BaseRepository<TableOne, Long> {
    fun findByName(name: String): Optional<TableOne>
    fun deleteByIdIn(ids: List<Long>)
}