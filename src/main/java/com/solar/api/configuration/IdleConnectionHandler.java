package com.solar.api.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Profile({"local", "batch"})
@Component
@EnableScheduling
public class IdleConnectionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdleConnectionHandler.class);
    private final JdbcTemplate jdbcTemplate;
    private static Properties prop;
    private static String userId;

    static {
        if ("local".equals(System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME)) ||
                "batch".equals(System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME))) {
            try {
                prop = new Properties();
                InputStream is = IdleConnectionHandler.class.getResourceAsStream("/application-local.yml");
                prop.load(is);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public IdleConnectionHandler(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void setUrlAndCredential() {
        String connectionString = (String) prop.get("connectionString");
        Map<String, String> connectionMap = new HashMap<>();
        List<String> values =
                Arrays.stream(connectionString.split(";")).map(String::trim).collect(Collectors.toList());
        values.forEach(value -> {
            String[] valueParts = value.split("=");
            connectionMap.put(valueParts[0], valueParts[1]);
        });
        userId = connectionMap.get("User Id");
    }

    @Scheduled(initialDelay = 5 * 1000, fixedDelay = 30 * 60 * 1000)
    public void scheduleFixedDelayTask() {
        try {
            LOGGER.info(">>>>>>>>>>> Reclaiming idle connections");
            jdbcTemplate.execute("call saas_schema.kill_all_sleep_connections_by_user('" + userId + "')");
            LOGGER.info("<<<<<<<<<<< Reclaiming idle connections done");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
