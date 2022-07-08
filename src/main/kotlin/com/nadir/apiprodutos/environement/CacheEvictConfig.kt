package com.nadir.apiprodutos.environement

import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
class CacheEvictConfig(
    private val cacheManager: RedisCacheManager
) {

    //@Scheduled( fixedRate = 30000)
    fun clearCaches() {
//        cacheManager.cacheNames.stream()
//            .forEach { cacheName: String? ->
//                cacheManager.getCache(cacheName!!)!!
//                    .clear()
//            }

        cacheManager.cacheNames.map {
            cacheManager.getCache(it)?.clear()
        }
    }
}
