package pers.acp.test.kotlin.base

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean

/**
 * @author zhangbin by 28/04/2018 13:04
 * @since JDK 11
 */
@NoRepositoryBean
interface BaseRepository<T, ID> : JpaSpecificationExecutor<T>, JpaRepository<T, ID>