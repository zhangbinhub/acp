package pers.acp.test.kotlin.entity

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import javax.persistence.*

/**
 * @author zhangbin by 28/04/2018 12:57
 * @since JDK 11
 */
@Entity
@Table(name = "table1")
class TableOne {

    @GenericGenerator(
        name = "tableSequenceGenerator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = [
            (Parameter(name = "sequence_name", value = "table_seq")),
            (Parameter(name = "initial_value", value = "1")),
            (Parameter(name = "increment_size", value = "1"))]
    )
    @Id
    @GeneratedValue(generator = "tableSequenceGenerator")
    @Column(updatable = false)
    var id: Long = 0

    var name: String = ""

    var value: Double = 0.00

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "groupId")
    var memberSet: MutableSet<Member> = mutableSetOf()

}