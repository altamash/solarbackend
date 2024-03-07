package com.solar.api.tenant.configuration;

import com.solar.api.AppConstants;
import com.solar.api.configuration.DynamicConnectionParams;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public final class DataSourceUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceUtil.class);

    public static DataSource createAndConfigureDataSource(MasterTenant masterTenant) {
        return new HikariDataSource(getHikariConfig(masterTenant));
    }

    /**
     * This Method added by Syed Hussain Haider
     * Dated: 2023-01-13
     *
     *
     * @param masterTenant
     * @return
     */
    private static HikariConfig getHikariConfig(MasterTenant masterTenant) {
        String url = masterTenant.getUrl();
        String[] urlParts = url.split("@");
        String jdbcUrl = urlParts[urlParts.length - 1];
        String userNamePass = url.substring(0, url.indexOf(jdbcUrl) - 1).substring(AppConstants.API_DATABASE_PREFIX.length());
        String[] userNamePassParts = userNamePass.split(":");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(AppConstants.API_DATABASE_PREFIX + jdbcUrl);
        config.setDriverClassName(masterTenant.getDriverClass());
        config.setUsername(userNamePassParts[0]);
        config.setPassword(userNamePassParts[1]);
        config.setConnectionTimeout(300000);			// 20s
//        config.setMinimumIdle(10);					// min. connection idle - ready to be used.
        config.setMaximumPoolSize(DynamicConnectionParams.MAX_POOL_SIZE);			// max. connection in pool idle - ready to be used.
//        config.setIdleTimeout(Application.maxLifetime != 0 ? Application.maxLifetime : 28800000);
        config.setMaxLifetime(DynamicConnectionParams.MAX_LIFE_TIME != 0 ? DynamicConnectionParams.MAX_LIFE_TIME : 28800000);
        config.setLeakDetectionThreshold(300000);
        config.setPoolName(masterTenant.getDbName() + "-connection-pool");
        return config;
    }
}
