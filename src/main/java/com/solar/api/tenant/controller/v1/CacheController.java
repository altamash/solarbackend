package com.solar.api.tenant.controller.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("CacheController")
@Profile({"dev", "stage"})
@RequestMapping(value = "/cache")
public class CacheController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/name/{name}")
    public void clearCache(@PathVariable("name") String name) {
        cacheManager.getCache(name).clear();
        LOGGER.info("Cleared cache {}", name);
    }

    @GetMapping("/clear")
    public void clear() {
        cacheManager.getCacheNames().forEach(name -> {
            cacheManager.getCache(name).clear();
            LOGGER.info("Cleared cache {}", name);
        });
    }

    @GetMapping("/names")
    public Collection<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }
}
