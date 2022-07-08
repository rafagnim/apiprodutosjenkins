package com.nadir.apiprodutos.integration.feign.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "Apiclientes",
    url = "\${integration.apiclientes.url}"
)
interface UsuarioClient {
    @GetMapping("api/v1/usuarios/validatoken")
    fun validaToken(@RequestHeader(value = "Authorization", required = true) authorizationHeader:String): Long
}