package pers.acp.test.kotlin.test.nobuild

import pers.acp.test.kotlin.domain.TableOneDomain
import pers.acp.test.kotlin.entity.Member
import pers.acp.test.kotlin.entity.MemberTwo
import pers.acp.test.kotlin.entity.TableOne
import pers.acp.test.kotlin.entity.TableTwo
import pers.acp.test.kotlin.repository.MemberRepository
import pers.acp.test.kotlin.repository.MemberTwoRepository
import pers.acp.test.kotlin.repository.TableOneRepository
import pers.acp.test.kotlin.repository.TableTwoRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import pers.acp.test.kotlin.BaseTest


/**
 * @author zhangbin by 28/04/2018 17:18
 * @since JDK 11
 */
internal class JpaTest : BaseTest() {

    @Autowired
    lateinit var tableOneRepository: TableOneRepository

    @Autowired
    lateinit var tableTwoRepository: TableTwoRepository

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var memberTwoRepository: MemberTwoRepository

    @Autowired
    lateinit var tableOneDomain: TableOneDomain

    /**
     * Hibernate: insert into table1 (name, value, id) values (?, ?, ?)
     * Hibernate: insert into table1_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: insert into table1_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: insert into table1_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: update table1_member set group_id=? where id=?
     * Hibernate: update table1_member set group_id=? where id=?
     * Hibernate: update table1_member set group_id=? where id=?
     */
    @Test
    @Transactional
    @Rollback(false)
    fun testAdd() {
        tableOneRepository.save(TableOne().apply {
            this.name = "name1"
            this.value = 102.0
            this.memberSet.add(Member(loginNo = "member1"))
            this.memberSet.add(Member(loginNo = "member2"))
            this.memberSet.add(Member(loginNo = "member3"))
        })
    }

    /**
     * Hibernate: insert into table2 (name, value, id) values (?, ?, ?)
     * Hibernate: insert into table2_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: insert into table2_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: insert into table2_member (login_no, group_id, id) values (?, ?, ?)
     */
    @Test
    @Transactional
    @Rollback(false)
    fun testAddTwo() {
        for (i in 0..30) {
            tableTwoRepository.save(TableTwo().apply {
                this.name = "name$i"
                this.value = 202.0 + i
                this.memberSet.add(MemberTwo(loginNo = "memberTwo1").also { it.two = this })
                this.memberSet.add(MemberTwo(loginNo = "memberTwo2").also { it.two = this })
                this.memberSet.add(MemberTwo(loginNo = "memberTwo3").also { it.two = this })
            })
        }
    }

    /**
     * Hibernate: insert into table1_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: insert into table1_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: insert into table1_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: update table1_member set group_id=null where group_id=? and id=?
     * Hibernate: update table1_member set group_id=null where group_id=? and id=?
     * Hibernate: update table1_member set group_id=null where group_id=? and id=?
     * Hibernate: update table1_member set group_id=? where id=?
     * Hibernate: update table1_member set group_id=? where id=?
     * Hibernate: update table1_member set group_id=? where id=?
     * Hibernate: delete from table1_member where id=?
     * Hibernate: delete from table1_member where id=?
     * Hibernate: delete from table1_member where id=?
     */
    @Test
    @Transactional
    @Rollback(false)
    fun testUpdate() {
        tableOneRepository.getOne(4).let {
            memberRepository.deleteAll(it.memberSet)
            it.memberSet.clear()
            it.memberSet.add(Member(loginNo = "member4"))
            it.memberSet.add(Member(loginNo = "member5"))
            it.memberSet.add(Member(loginNo = "member6"))
            tableOneRepository.save(it)
        }
    }

    /**
     * Hibernate: insert into table2_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: insert into table2_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: insert into table2_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: update table2 set name=?, value=? where id=?
     * Hibernate: delete from table2_member where id=?
     * Hibernate: delete from table2_member where id=?
     * Hibernate: delete from table2_member where id=?
     */
    @Test
    @Transactional
    @Rollback(false)
    fun testUpdateTwo() {
        tableTwoRepository.getOne(2).let {
            memberTwoRepository.deleteAll(it.memberSet)
            it.memberSet.clear()
            it.name = "a222222"
            it.memberSet.add(MemberTwo(loginNo = "member4").apply { this.two = it })
            it.memberSet.add(MemberTwo(loginNo = "member5").apply { this.two = it })
            it.memberSet.add(MemberTwo(loginNo = "member6").apply { this.two = it })
            tableTwoRepository.save(it)
        }
    }

    /**
     * Hibernate: delete from table1_member where id=? or id=? or id=?
     * Hibernate: insert into table1_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: insert into table1_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: insert into table1_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: update table1_member set group_id=null where group_id=? and id=?
     * Hibernate: update table1_member set group_id=null where group_id=? and id=?
     * Hibernate: update table1_member set group_id=null where group_id=? and id=?
     * Hibernate: update table1_member set group_id=? where id=?
     * Hibernate: update table1_member set group_id=? where id=?
     * Hibernate: update table1_member set group_id=? where id=?
     */
    @Test
    @Transactional
    @Rollback(false)
    fun testUpdate2() {
        tableOneRepository.getOne(4).let {
            memberRepository.deleteInBatch(it.memberSet)
            it.memberSet.clear()
            it.memberSet.add(Member(loginNo = "member4"))
            it.memberSet.add(Member(loginNo = "member5"))
            it.memberSet.add(Member(loginNo = "member7"))
            tableOneRepository.save(it)
        }
    }

    /**
     * Hibernate: delete from table2_member where id=? or id=? or id=?
     * Hibernate: insert into table2_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: insert into table2_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: insert into table2_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: update table2 set name=?, value=? where id=?
     */
    @Test
    @Transactional
    @Rollback(false)
    fun testUpdateTwo2() {
        tableTwoRepository.getOne(2).let {
            memberTwoRepository.deleteInBatch(it.memberSet)
            it.memberSet.clear()
            it.name = "b222222"
            it.memberSet.add(MemberTwo(loginNo = "member4").apply { this.two = it })
            it.memberSet.add(MemberTwo(loginNo = "member5").apply { this.two = it })
            it.memberSet.add(MemberTwo(loginNo = "member7").apply { this.two = it })
            tableTwoRepository.save(it)
        }
    }

    /**
     * Hibernate: insert into table2_member (login_no, group_id, id) values (?, ?, ?)
     * Hibernate: update table2 set name=?, value=? where id=?
     * Hibernate: update table2_member set login_no=?, group_id=? where id=?
     */
    @Test
    @Transactional
    @Rollback(false)
    fun testUpdateTwo3() {
        tableTwoRepository.getOne(9).let {
            it.name = "b333333"
            it.memberSet = it.memberSet.filter { member ->
                member.loginNo.endsWith("3")
            }.toMutableSet()
            it.memberSet.forEach { member ->
                member.loginNo += "_updated"
            }
            it.memberSet.add(MemberTwo(loginNo = "memberTwo4").apply { this.two = it })
            tableTwoRepository.save(it)
        }
    }

    /**
     * Hibernate: update table1_member set group_id=null where group_id=?
     * Hibernate: delete from table1_member where id=?
     * Hibernate: delete from table1_member where id=?
     * Hibernate: delete from table1_member where id=?
     * Hibernate: delete from table1 where id=?
     */
    @Test
    @Transactional
    @Rollback(false)
    fun testDelete1() {
//        tableOneRepository.deleteByIdIn(listOf(4))
        tableOneRepository.getOne(1).let {
            tableOneRepository.deleteAll(listOf(it))
        }
    }

    /**
     * Hibernate: delete from table2_member where id=?
     * Hibernate: delete from table2_member where id=?
     * Hibernate: delete from table2_member where id=?
     * Hibernate: delete from table2 where id=?
     */
    @Test
    @Transactional
    @Rollback(false)
    fun testDeleteTwo1() {
//        tableTwoRepository.deleteByIdIn(listOf(2))
        tableTwoRepository.getOne(3).let {
            tableTwoRepository.deleteAll(listOf(it))
        }
    }

    /**
     * Hibernate: update table1_member set group_id=null where group_id=?
     * Hibernate: delete from table1_member where id=?
     * Hibernate: delete from table1_member where id=?
     * Hibernate: delete from table1_member where id=?
     * Hibernate: delete from table1 where id=?
     */
    @Test
    @Transactional
    @Rollback(false)
    fun testDelete2() {
        tableOneRepository.getOne(2).let {
            memberRepository.deleteAll(it.memberSet)
            it.memberSet.clear()
            tableOneRepository.deleteAll(listOf(it))
        }
    }

    /**
     * Hibernate: delete from table2_member where id=?
     * Hibernate: delete from table2_member where id=?
     * Hibernate: delete from table2_member where id=?
     * Hibernate: delete from table2 where id=?
     */
    @Test
    @Transactional
    @Rollback(false)
    fun testDeleteTwo2() {
        tableTwoRepository.getOne(4).let {
            memberTwoRepository.deleteAll(it.memberSet)
            it.memberSet.clear()
            tableTwoRepository.deleteAll(listOf(it))
        }
    }

    /**
     * Hibernate: update table1_member set group_id=null where group_id=?
     * Hibernate: delete from table1_member where id=?
     * Hibernate: delete from table1_member where id=?
     * Hibernate: delete from table1_member where id=?
     * Hibernate: delete from table1 where id=?
     */
    @Test
    @Transactional
    @Rollback(false)
    fun testDelete3() {
        tableOneRepository.getOne(3).let { one ->
            one.memberSet.iterator().let {
                while (it.hasNext()) {
                    val next = it.next()
                    it.remove()
                    memberRepository.delete(next)
                }
            }
            // deleteInBatch 报错，delete from table1 where id=? member有外键
//            tableOneRepository.deleteInBatch(listOf(one))
            tableOneRepository.deleteAll(listOf(one))
        }
    }

    /**
     * Hibernate: delete from table2_member where id=?
     * Hibernate: delete from table2_member where id=?
     * Hibernate: delete from table2_member where id=?
     * Hibernate: delete from table2 where id=?
     */
    @Test
    @Transactional
    @Rollback(false)
    fun testDeleteTwo3() {
        tableTwoRepository.getOne(5).let { one ->
            one.memberSet.iterator().let {
                while (it.hasNext()) {
                    val next = it.next()
                    it.remove()
                    memberTwoRepository.delete(next)
                }
            }
            tableTwoRepository.deleteAll(listOf(one))
        }
    }

    @Test
    @Rollback(false)
    fun testDeleteTwo4() {
        for (i in 0..30) {
            tableOneDomain.testSaveTwo(TableTwo().apply {
                this.name = "name$i"
                this.value = 202.0 + i
                this.memberSet.add(MemberTwo(loginNo = "memberTwo1").also { it.two = this })
                this.memberSet.add(MemberTwo(loginNo = "memberTwo2").also { it.two = this })
                this.memberSet.add(MemberTwo(loginNo = "memberTwo3").also { it.two = this })
            })
        }
        val name = "name2"
        tableOneDomain.testDeleteTwo(name)
    }

}