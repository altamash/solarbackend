package com.solar.api.saas.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class MasterDataSourceFactory implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterDataSourceFactory.class);
    private static DataSource masterDataSource;
    private static DataSource dataSource;
    private static Connection connection;

    MasterDataSourceFactory(@Qualifier("masterDataSource") DataSource masterDataSource) {
        this.masterDataSource = masterDataSource;
        this.dataSource = masterDataSource;
        try {
            connection = masterDataSource.getConnection();
            LOGGER.info("DataSourceClass: {}", this.dataSource.getClass().getName());
            LOGGER.info("MasterDataSourceClass: {}", this.masterDataSource.getClass().getName());
            LOGGER.info("connection: {}", connection);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.info("DatabaseConnectionError", e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                if (dataSource == null) {
                    connection = masterDataSource.getConnection();
                }
                connection = dataSource.getConnection();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return connection;
    }

    @Override
    public void afterPropertiesSet() {
        dataSource = masterDataSource;
    }
}
