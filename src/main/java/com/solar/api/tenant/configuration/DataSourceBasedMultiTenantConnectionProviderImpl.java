package com.solar.api.tenant.configuration;

import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.MasterTenantRepository;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Configuration
public class DataSourceBasedMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceBasedMultiTenantConnectionProviderImpl.class);

    private static final long serialVersionUID = 1L;

    private Map<String, DataSource> dataSourcesMtApp = new TreeMap<>();

    @Autowired
    private MasterTenantRepository masterTenantRepository;

    /*MasterTenant masterTenant = MasterTenant.builder()
            .url("jdbc:mysql://dataadmin@sidevdb:solaR@dmiN1235@sidevdb.mysql.database.azure.com:3306/ec1001?reconnect=true&createDatabaseIfNotExist=true&zeroDateTimeBehavior=convertToNull&serverTimezone=UTC")
            .driverClass("com.mysql.cj.jdbc.Driver")
            .dbName("ec1001").build();*/

    @Override
    protected DataSource selectAnyDataSource() {
        // This method is called more than once. So check if the data source map
        // is empty. If it is then rescan master_tenant table for all tenant
        if (dataSourcesMtApp.isEmpty()) {
            List<MasterTenant> masterTenants = masterTenantRepository.findAll().stream()
                    .filter(tenant -> tenant.getEnabled() && tenant.getValid())
                    .collect(Collectors.toList());
            LOGGER.info("selectAnyDataSource() method call...Total tenants:" + masterTenants.size());
            for (MasterTenant masterTenant : masterTenants) {
                dataSourcesMtApp.put(masterTenant.getDbName(),
                        DataSourceUtil.createAndConfigureDataSource(masterTenant));
            }
        }
//        return this.dataSourcesMtApp.values().isEmpty() ? null : this.dataSourcesMtApp.values().iterator().next();
        return this.dataSourcesMtApp.values().iterator().next();
//        return DataSourceUtil.createAndConfigureDataSource(masterTenant);
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        // If the requested tenant id is not present check for it in the saas_schema 'master_tenant' table
        tenantIdentifier = initializeTenantIfLost(tenantIdentifier);
        if (tenantIdentifier == null) {
            LOGGER.error("tenantIdentifier == null");
            return null;
        }
        if (!this.dataSourcesMtApp.containsKey(tenantIdentifier)) {
            List<MasterTenant> masterTenants = masterTenantRepository.findAll().stream()
                    .filter(tenant -> tenant.getEnabled() && tenant.getValid())
                    .collect(Collectors.toList());
            LOGGER.info("selectDataSource() method call...Tenant:" + tenantIdentifier + " Total tenants:" + masterTenants.size());
            for (MasterTenant masterTenant : masterTenants) {
                dataSourcesMtApp.put(masterTenant.getDbName(),
                        DataSourceUtil.createAndConfigureDataSource(masterTenant));
            }
        }
        //check again if tenant exist in map after rescan saas_schema, if not, throw UsernameNotFoundException
        if (!this.dataSourcesMtApp.containsKey(tenantIdentifier)) {
            LOGGER.warn("Trying to get tenant:" + tenantIdentifier + " which was not found in master db after rescan");
            throw new UsernameNotFoundException(String.format("Tenant not found after rescan, " + " tenant=%s",
                    tenantIdentifier));
        }
        return this.dataSourcesMtApp.get(tenantIdentifier);
//        return DataSourceUtil.createAndConfigureDataSource(masterTenant);
    }

    private String initializeTenantIfLost(String tenantIdentifier) {
        if (!tenantIdentifier.equals(DBContextHolder.getTenantName())) {
            tenantIdentifier = DBContextHolder.getTenantName();
        }
        return tenantIdentifier;
    }

    @PreDestroy
    public void preDestroy() {
        LOGGER.info(">>>>>>>>>>>>>>>>> About to close all connections in DataSourceBasedMultiTenantConnectionProviderImpl#preDestroy()");
        this.dataSourcesMtApp.values().forEach(dataSource -> {
            try {
                dataSource.getConnection().close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
    }
}
