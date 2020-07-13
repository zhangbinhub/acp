package pers.acp.test.application.base;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * @author zhang by 10/04/2019
 * @since JDK 11
 */
public class BaseJpaConfig {

    private final JpaProperties jpaProperties;

    private final HibernateProperties hibernateProperties;

    public BaseJpaConfig(JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
        this.jpaProperties = jpaProperties;
        this.hibernateProperties = hibernateProperties;
    }

    protected LocalContainerEntityManagerFactoryBean buildEntityManagerFactory(HikariDataSource hikariDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(hikariDataSource);
        em.setPackagesToScan(hikariDataSource.getDataSourceProperties().getProperty("scanpackage").split(","));

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaPropertyMap(hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings()));
        return em;
    }

}
