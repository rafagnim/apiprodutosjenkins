package com.nadir.apiprodutos.configs

import com.fasterxml.jackson.databind.ObjectMapper
import com.nadir.apiprodutos.utils.QUEUEPARAPRODUTO
import org.springframework.amqp.core.Queue
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    @Bean
    fun messageConverter(objectMapper: ObjectMapper) : MessageConverter {
        return Jackson2JsonMessageConverter(objectMapper)
    }

    @Bean
    fun createCompraQueue() : Queue?{
        return Queue(QUEUEPARAPRODUTO, true)
    }
}