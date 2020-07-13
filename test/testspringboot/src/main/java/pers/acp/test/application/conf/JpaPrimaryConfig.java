package pers.acp.test.application.conf;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pers.acp.test.application.base.BaseJpaConfig;

import java.util.Objects;

/**
 * @author zhangbin by 2018-1-15 16:03
 * @since JDK 11
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"pers.acp.test.application.repo.primary"},
        entityManagerFactoryRef = "entityManagerFactoryPrimary",
        transactionManagerRef = "transactionManagerPrimary")
public class JpaPrimaryConfig extends BaseJpaConfig {

    @Autowired
    public JpaPrimaryConfig(JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
        super(jpaProperties, hibernateProperties);
    }

    @Primary
    @Bean("dataSourcePrimary")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public HikariDataSource primaryDataSource() {
        return new HikariDataSource();
    }

    @Primary
    @Bean(name = "entityManagerFactoryPrimary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary() {
        LocalContainerEntityManagerFactoryBean em = buildEntityManagerFactory(primaryDataSource());
        em.setPersistenceUnitName("persistenceUnitPrimary");
        return em;
    }

    @Primary
    @Bean(name = "transactionManagerPrimary")
    public PlatformTransactionManager transactionManagerPrimary() {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryPrimary().getObject()));
    }

}
