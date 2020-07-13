package pers.acp.test.application.conf;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pers.acp.test.application.base.BaseJpaConfig;

import java.util.Objects;

/**
 * @author zhangbin by 2018-1-15 15:29
 * @since JDK 11
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"pers.acp.test.application.repo.pg"},
        entityManagerFactoryRef = "entityManagerFactoryPg",
        transactionManagerRef = "transactionManagerPg")
public class JpaPgConfig extends BaseJpaConfig {

    @Autowired
    public JpaPgConfig(JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
        super(jpaProperties, hibernateProperties);
    }

    @Bean("dataSourcePg")
    @ConfigurationProperties(prefix = "spring.datasource.pg")
    public HikariDataSource pgDataSource() {
        return new HikariDataSource();
    }

    @Bean(name = "entityManagerFactoryPg")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary() {
        LocalContainerEntityManagerFactoryBean em = buildEntityManagerFactory(pgDataSource());
        em.setPersistenceUnitName("persistenceUnitPg");
        return em;
    }

    @Bean(name = "transactionManagerPg")
    public PlatformTransactionManager transactionManagerPrimary() {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryPrimary().getObject()));
    }

}
