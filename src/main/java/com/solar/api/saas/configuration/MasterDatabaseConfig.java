package com.solar.api.saas.configuration;

import com.solar.api.configuration.DynamicConnectionParams;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
/*@ComponentScan(basePackages = {
                                "com.solar.api.saas.model",
                                "com.solar.api.saas.repository"})*/
@EnableJpaRepositories(basePackages = {
        "com.solar.api.saas.model",
        "com.solar.api.saas.repository",
        "com.solar.api.saas.module.com.solar.scheduler.repository"},
        entityManagerFactoryRef = "masterEntityManagerFactory",
        transactionManagerRef = "masterTransactionManager")
public class MasterDatabaseConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MasterDatabaseConfig.class);
    private static HikariDataSource hikariDataSource;
    @Autowired
    private MasterDatabaseConfigProperties masterDbProperties;
    @Autowired
    private DynamicConnectionParams dynamicConnectionParams;

    //Create Master Data Source using master properties and also configure HikariCP
    @Bean(name = "masterDataSource")
    public DataSource masterDataSource() {
        hikariDataSource = new HikariDataSource();
        hikariDataSource.setUsername(masterDbProperties.getUsername());
        hikariDataSource.setPassword(masterDbProperties.getPassword());
        hikariDataSource.setJdbcUrl(masterDbProperties.getUrl());
        hikariDataSource.setDriverClassName(masterDbProperties.getDriverClassName());
        hikariDataSource.setConnectionTimeout(masterDbProperties.getConnectionTimeout());
        hikariDataSource.setMaximumPoolSize(dynamicConnectionParams.getMaxPoolSize());
//        hikariDataSource.setMinimumIdle(masterDbProperties.getMinIdle());
//        hikariDataSource.setIdleTimeout(Application.maxLifetime != 0 ? Application.maxLifetime : 28800000);
        hikariDataSource.setMaxLifetime(dynamicConnectionParams.getMaxLifeTime() != 0 ? dynamicConnectionParams.getMaxLifeTime() : 28800000);
        hikariDataSource.setLeakDetectionThreshold(masterDbProperties.getLeakDetectionThreshold());
        hikariDataSource.setPoolName(masterDbProperties.getPoolName());
        LOG.info("Setup of masterDataSource succeeded.");
        return hikariDataSource;
    }

    //    @Primary
//    @PersistenceContext(unitName = "masterEntityManager")
    @Bean(name = "masterEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        // Set the master data source
        em.setDataSource(masterDataSource());
        // The master tenant entity and repository need to be scanned
//        em.setPackagesToScan(new String[]{MasterTenant.class.getPackage().getName(), MasterTenantRepository.class
//        .getPackage().getName()});
        em.setPackagesToScan(
                "com.solar.api.saas.model",
                "com.solar.api.saas.repository",
                "com.solar.api.saas.module.com.solar.scheduler.model");
        // Setting a name for the persistence unit as Spring sets it as
        // 'default' if not defined
        em.setPersistenceUnitName("masterdb-persistence-unit");
        // Setting Hibernate as the JPA provider
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        // Set the hibernate properties
        em.setJpaProperties(hibernateProperties());
        LOG.info("Setup of masterEntityManagerFactory succeeded.");
        return em;
    }

    @Bean(name = "masterTransactionManager")
    public JpaTransactionManager masterTransactionManager(@Qualifier("masterEntityManagerFactory") EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    //Hibernate configuration properties
    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put(org.hibernate.cfg.Environment.DIALECT, "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put(Environment.PHYSICAL_NAMING_STRATEGY, "com.vladmihalcea.hibernate.type.util" +
                ".CamelCaseToSnakeCaseNamingStrategy");
        properties.put(org.hibernate.cfg.Environment.SHOW_SQL, true);
        properties.put(org.hibernate.cfg.Environment.FORMAT_SQL, true);
        properties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, "none");
        return properties;
    }
}
