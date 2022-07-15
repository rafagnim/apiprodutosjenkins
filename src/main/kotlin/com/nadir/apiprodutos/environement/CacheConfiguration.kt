package com.nadir.apiprodutos.environement

import com.fasterxml.jackson.databind.ObjectMapper
import com.nadir.apiprodutos.entities.Produto
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import java.time.Duration


@Configuration
class CacheConfiguration(
    private val objectMapper: ObjectMapper
) {
    @Bean
    fun cacheConfig(): RedisCacheConfiguration? {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(1))
            .disableCachingNullValues()
            .serializeValuesWith(SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer()))
    }

//    @Bean
//    fun redisCacheManagerBuilderCustomizer(): RedisCacheManagerBuilderCustomizer? {
//        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(Produto::class.java).apply {
//            this.setObjectMapper(objectMapper)
//        }
//        return RedisCacheManagerBuilderCustomizer { builder: RedisCacheManagerBuilder ->
//            builder
//                .withCacheConfiguration(
//                    "getAll",
//                    RedisCacheConfiguration.defaultCacheConfig()
//                        .entryTtl(Duration.ofMinutes(5))
//                        .disableCachingNullValues()
//                        .serializeValuesWith(SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer()))
//                )
//        }
//    }


//    @Bean
//    fun cacheManager(): CacheManager {
//        val redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory)
//
//        val cache1MinuteConfig = RedisCacheConfiguration.defaultCacheConfig()
//            .entryTtl(Duration.ofMinutes(1))
//            .computePrefixWith { name -> "UserApi:$name:" }
//
//        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(User::class.java).apply {
//            this.setObjectMapper(objectMapper)
//        }
//        val cache1MinuteConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(1))
//                .computePrefixWith { name -> "UserApi:$name:" }
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
//
//
//
////        val cache60MinuteConfiguration = RedisCacheConfiguration.defaultCacheConfig()
////                .entryTtl(Duration.ofMinutes(60))
////                .computePrefixWith { name -> "UserApi:$name:" }
////                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
//
//        return RedisCacheManager.RedisCacheManagerBuilder.fromCacheWriter(redisCacheWriter)
//                .withCacheConfiguration("getAll", cache1MinuteConfig)
////                .withCacheConfiguration("count", cache1MinuteConfiguration)
//                .withCacheConfiguration("findById", cache1MinuteConfiguration)
//                .build()
//    }
}