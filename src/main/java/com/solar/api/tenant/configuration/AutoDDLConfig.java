package com.solar.api.tenant.configuration;

import com.solar.api.AppConstants;
import com.solar.api.configuration.DynamicConnectionParams;
import com.solar.api.helper.Utility;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class AutoDDLConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoDDLConfig.class);

    @Value("${tenantSchemas.update}")
    private boolean updateSchemas;

    private HikariConfig configuration;
    private List<DataSource> dataSources = new ArrayList<>();

    @Autowired
    private MasterTenantRepository masterTenantRepository;

    @Bean
    public void initSchema() {
        if (!updateSchemas) {
            return;
        }
        LocalContainerEntityManagerFactoryBean emfBean;
        StringBuilder step = new StringBuilder();
        long startTime = System.currentTimeMillis();
        long startTimeTenant;
        List<MasterTenant> masterTenants = masterTenantRepository.findAll().stream()
                .filter(tenant -> tenant.getEnabled() && tenant.getValid())
                .collect(Collectors.toList());
        for (MasterTenant masterTenant : masterTenants) {
            try {
                startTimeTenant = System.currentTimeMillis();
                step.append("1/4 createAndConfigureDataSource(masterTenant)");
                DataSource dataSource = createAndConfigureDataSourceNew(masterTenant);
                step.setLength(0);
                emfBean = new LocalContainerEntityManagerFactoryBean();
                step.append("2/4 emfBean.setDataSource(dataSource)");
                emfBean.setDataSource(dataSource);
                step.setLength(0);
                emfBean.setPackagesToScan("com.solar.api.tenant", "com.solar.api.configuration",
                        "com.solar.api.saas.configuration");
                emfBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
                emfBean.setPersistenceUnitName("tenantdb-persistence-unit");
                emfBean.setJpaPropertyMap(getJpaPropertyMap());
                emfBean.setPersistenceUnitName(dataSource.toString());
                step.append("3/4 emfBean.afterPropertiesSet()");
                emfBean.afterPropertiesSet();
                step.setLength(0);
                LOGGER.info("\t" + masterTenant.getDbName() + " updated in: " + Utility.getFormattedMillis(System.currentTimeMillis() - startTimeTenant));
                step.append("4/4 dataSource.getConnection().close()");
//                dataSource.getConnection().close();
            } catch (Exception e) {
                LOGGER.error("Error in AutoDDLConfig#initSchema at step " + step + "; tenant:" + masterTenant.getDbName(), e);
            }
        }
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>> AutoDDLConfig#initSchema took: " + Utility.getFormattedMillis(System.currentTimeMillis() - startTime));
    }

    private DataSource createAndConfigureDataSourceNew(MasterTenant masterTenant) {
        return new HikariDataSource(setHikariConfig(masterTenant));
    }

    private HikariConfig setHikariConfig(MasterTenant masterTenant) {
        String url = masterTenant.getUrl();
        String[] urlParts = url.split("@");
        String jdbcUrl = urlParts[urlParts.length - 1];
        String userNamePass =
                url.substring(0, url.indexOf(jdbcUrl) - 1).substring(AppConstants.API_DATABASE_PREFIX.length());
        String[] userNamePassParts = userNamePass.split(":");

        this.configuration = new HikariConfig();
        configuration.setJdbcUrl(AppConstants.API_DATABASE_PREFIX + jdbcUrl);
        configuration.setDriverClassName(masterTenant.getDriverClass());
        configuration.setUsername(userNamePassParts[0]);
        configuration.setPassword(userNamePassParts[1]);

        // using the default values of these below.
        configuration.setConnectionTimeout(30000);		// 5 seconds
//        configuration.setMinimumIdle(5);				// min. idle connections.
        configuration.setMaximumPoolSize(DynamicConnectionParams.MAX_POOL_SIZE);  			// max. idle connections.
//        configuration.setIdleTimeout(Application.maxLifetime != 0 ? Application.maxLifetime : 28800000);
        configuration.setMaxLifetime(DynamicConnectionParams.MAX_LIFE_TIME != 0 ? DynamicConnectionParams.MAX_LIFE_TIME : 28800000);
        configuration.setLeakDetectionThreshold(300000);
        configuration.setPoolName(masterTenant.getDbName() + "-connection-pool");
        return configuration;
    }

    /**
     *
     * Old Code
     *
     * @param masterTenant
     * @param dss
     * @return
     */
    private DataSource createAndConfigureDataSource(MasterTenant masterTenant, DataSource dss) {
        HikariDataSource ds = new HikariDataSource();
        String url = masterTenant.getUrl();
        String[] urlParts = url.split("@");
        String jdbcUrl = urlParts[urlParts.length - 1];
        String userNamePass =
                url.substring(0, url.indexOf(jdbcUrl) - 1).substring(AppConstants.API_DATABASE_PREFIX.length());
        String[] userNamePassParts = userNamePass.split(":");


        ds.setUsername(userNamePassParts[0]);
        ds.setPassword(userNamePassParts[1]);
        jdbcUrl = AppConstants.API_DATABASE_PREFIX + jdbcUrl;
        ds.setJdbcUrl(jdbcUrl);
        ds.setDriverClassName(masterTenant.getDriverClass());
        /** HikariCP settings - could come from the master_tenant table but
         hardcoded here for brevity
         Maximum waiting time for a connection from the pool **/
        ds.setConnectionTimeout(240000);

        /** Minimum number of idle connections in the pool **/
        ds.setMinimumIdle(5);

        /** Maximum number of actual connection in the pool **/
        ds.setMaximumPoolSize(250);

        /** Maximum time that a connection is allowed to sit idle in the pool
         ds.setIdleTimeout(300000); **/
        ds.setIdleTimeout(1800);// The HikariCP idleTimeout should be less than or equal to the MySQL wait_timeout
        //        ds.setIdleTimeout(1500000);
        //        ds.setIdleTimeout(120000);//
        //        ds.setConnectionTimeout(20000);
        //        ds.setMaxLifetime(1800000);//Added
        // Setting up a pool name for each tenant datasource
        String tenantConnectionPoolName = masterTenant.getDbName() + "-connection-pool";
        ds.setPoolName(tenantConnectionPoolName);
        return ds;
    }

    private Map<String, Object> getJpaPropertyMap() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put(Environment.PHYSICAL_NAMING_STRATEGY, "com.vladmihalcea.hibernate.type.util" +
                ".CamelCaseToSnakeCaseNamingStrategy");
        properties.put(Environment.SHOW_SQL, true);
        properties.put(Environment.FORMAT_SQL, true);
        properties.put(Environment.HBM2DDL_AUTO, "none");
        return properties;
    }

    @PreDestroy
    public void preDestroy() {
        LOGGER.info(">>>>>>>>>>>>>>>>> About to close all connections in AutoDDLConfig#preDestroy()");
        this.dataSources.forEach(dataSource -> {
            try {
                dataSource.getConnection().close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
    }

}