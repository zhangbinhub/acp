package pers.acp.test.kotlin.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModelProperty
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table(name = "table1_member")
data class Member(
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("ID")
    var id: String = "",

    @Column(length = 100, nullable = false)
    @ApiModelProperty("用户登录号")
    var loginNo: String = "",

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupId")
    var one: TableOne? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val groupMember = other as Member
        return EqualsBuilder()
            .appendSuper(super.equals(other))
            .append(id, groupMember.id)
            .append(loginNo, groupMember.loginNo)
            .isEquals
    }

    override fun hashCode(): Int =
        HashCodeBuilder(17, 37)
            .appendSuper(super.hashCode())
            .append(id)
            .append(loginNo)
            .toHashCode()

    override fun toString(): String =
        StringBuilder("Member(")
            .append("id=$id")
            .append(",loginNo=$loginNo")
            .append(")")
            .toString()
}