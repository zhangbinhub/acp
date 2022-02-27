package pers.acp.test.application.entity.pg;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author zhangbin by 2018-1-15 16:45
 * @since JDK 11
 */
@Entity
@Table(name = "table2")
public class TableTwo {

    @Id
    @GenericGenerator(
            name = "testSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "test_seq"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")})
    @GeneratedValue(generator = "testSequenceGenerator")
    private Long id;
    @Column
    private String name;
    @Column
    private BigDecimal value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

}
