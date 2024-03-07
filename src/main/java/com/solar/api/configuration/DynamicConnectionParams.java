package com.solar.api.configuration;

import com.solar.api.AppConstants;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScope
@Configuration
@Getter
@Setter
@ConfigurationProperties("multitenancy.saas.datasource")
public class DynamicConnectionParams {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private String connectionString;
    private int maxPoolSize;
    private int maxLifeTime;
    public static int MAX_POOL_SIZE;
    public static int MAX_LIFE_TIME;
    private static int effectiveSpindleCount;

    @PostConstruct
    public void init() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        CentralProcessor centralProcessor = hardwareAbstractionLayer.getProcessor();
        maxPoolSize = (centralProcessor.getPhysicalProcessorCount() * 2) + effectiveSpindleCount;
        maxLifeTime = calculateMaxLifeTime();
        MAX_POOL_SIZE = maxPoolSize;
        MAX_LIFE_TIME = maxLifeTime;
    }

    private Map<String, String> getConnectionMap() {
        Map<String, String> connectionMap = new HashMap<>();
        List<String> values =
                Arrays.stream(connectionString.split(";")).map(string -> string.trim()).collect(Collectors.toList());
        values.stream().forEach(value -> {
            String[] valueParts = value.split("=");
            connectionMap.put(valueParts[0], valueParts[1]);
        });
        return connectionMap;
    }

    private int calculateMaxLifeTime() {
        Map<String, String> connectionMap = getConnectionMap();
        try (Connection connection = DriverManager.getConnection(AppConstants.API_DATABASE_PREFIX + connectionMap.get("Data Source"),
                connectionMap.get("User Id"), connectionMap.get("Password"));
                Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT @@wait_timeout");
            if (resultSet.next()) {
                return (resultSet.getInt(1) * 1000) - 60000;
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return 28740000; // default wait_timout - 1 minute
    }
}
