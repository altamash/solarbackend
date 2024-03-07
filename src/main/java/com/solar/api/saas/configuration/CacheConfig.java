package com.solar.api.saas.configuration;

import com.solar.api.Cache;
import com.solar.api.CacheEventLogger;
import lombok.Setter;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheEventListenerConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.event.EventType;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;
import java.util.List;

@Configuration
@Profile({"local", "dev", "stage"})
@EnableCaching
@PropertySource("classpath:application-cache.yml")
@ConfigurationProperties("cache")
@Setter
public class CacheConfig {

    private List<Cache> config;

    @Bean
    JCacheCacheManager getCacheManager() throws ClassNotFoundException {
        return new JCacheCacheManager(cacheManager());
    }

    @Bean(name = "ehCacheMngr", destroyMethod = "close")
    CacheManager cacheManager() throws ClassNotFoundException {
        final CachingProvider provider = Caching.getCachingProvider();
        final CacheManager cacheManager = provider.getCacheManager();
        for (Cache cache : config) {
            //noinspection unchecked
            cacheManager.createCache(cache.getName(), Eh107Configuration
                    .fromEhcacheCacheConfiguration(getConfiguration(cache)
                            .withService(getListener())));
        }
        return cacheManager;
    }

    private final CacheConfigurationBuilder getConfiguration(Cache cache) throws ClassNotFoundException {
        //noinspection unchecked
        CacheConfigurationBuilder builder = CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Class.forName(cache.getKeyClass()),
                Class.forName(cache.getValueClass()),
                ResourcePoolsBuilder
                        .heap(cache.getHeap())
                        .offheap(cache.getOffHeap(), MemoryUnit.MB));
        ExpiryPolicy<Object, Object> policy;
        switch (cache.getExpiryPolicy()) {
            case "none":
                policy = ExpiryPolicyBuilder.noExpiration();
                break;
            case "time_to_idle":
                policy = ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(cache.getTtlOrtti()));
                break;
            case "time_to_live":
                policy = ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(cache.getTtlOrtti()));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + cache.getExpiryPolicy());
        }
        return builder.withExpiry(policy);
    }

    private final CacheEventListenerConfigurationBuilder getListener() {
        return CacheEventListenerConfigurationBuilder
                .newEventListenerConfiguration(CacheEventLogger.class, EventType.CREATED, EventType.UPDATED,
                        EventType.EXPIRED, EventType.REMOVED, EventType.EVICTED)
//                .eventOrdering(EventOrdering.UNORDERED)
//                .firingMode(EventFiring.ASYNCHRONOUS)
                .unordered().asynchronous();
    }

}
