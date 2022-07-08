package com.nadir.apiprodutos

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class ApiprodutosApplication

fun main(args: Array<String>) {
	runApplication<ApiprodutosApplication>(*args)
}
