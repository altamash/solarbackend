package com.solar.api;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheEventLogger implements CacheEventListener<Object, Object> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public void onEvent(CacheEvent<? extends Object, ? extends Object> cacheEvent) {
        LOGGER.info("Event {}, Key {}, Old value {}, New value {}", cacheEvent.getType(), cacheEvent.getKey(),
                cacheEvent.getOldValue(), cacheEvent.getNewValue());
    }
}
