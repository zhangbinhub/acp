package pers.acp.test.kotlin.repository

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import pers.acp.test.kotlin.base.BaseRepository
import pers.acp.test.kotlin.entity.TableTwo
import java.util.*

/**
 * @author zhangbin by 28/04/2018 13:02
 * @since JDK 11
 */
interface TableTwoRepository : BaseRepository<TableTwo, Long> {
    fun findByName(name: String): Optional<TableTwo>
    fun deleteByIdIn(ids: List<Long>)

    @Modifying
    @Query("delete from TableTwo where name=:name")
    fun deleteAllByName(@Param("name") name: String)
}