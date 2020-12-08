package pers.acp.test.kotlin.repository

import pers.acp.test.kotlin.base.BaseRepository
import pers.acp.test.kotlin.entity.MemberTwo
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MemberTwoRepository : BaseRepository<MemberTwo, String> {

    @Modifying
    @Query("delete from MemberTwo where two.id in (select id from TableTwo where name=:name)")
    fun deleteAllByName(@Param("name") name: String)
}