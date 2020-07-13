package pers.acp.test.application.entity.primary;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author zhangbin by 2018-1-15 16:45
 * @since JDK 11
 */
@Entity
@Table(name = "table1")
@ApiModel("表1")
public class TableOne {

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

    @Id
    @GenericGenerator(
            name = "testSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "test_seq"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")})
    @GeneratedValue(generator = "testSequenceGenerator")
    @ApiModelProperty("主键")
    private Long id;

    @Column
    @ApiModelProperty("名称")
    private String name;

    @Column
    @ApiModelProperty("值")
    private BigDecimal value;

}
