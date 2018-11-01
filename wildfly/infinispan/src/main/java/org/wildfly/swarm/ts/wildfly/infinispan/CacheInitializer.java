package org.wildfly.swarm.ts.wildfly.infinispan;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class CacheInitializer {
    @Resource(lookup = "java:jboss/infinispan/container/test-cache-container")
    private EmbeddedCacheManager cacheManager;

    @PostConstruct
    public void initCache() {
        Cache cache = cacheManager.getCache("test-cache");
        cache.put("foo", "bar");
    }
}
