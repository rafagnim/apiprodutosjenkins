package com.nadir.apiprodutos.controllers

import com.nadir.apiprodutos.entities.Produto
import com.nadir.apiprodutos.exceptions.AuthenticationException
import com.nadir.apiprodutos.integration.feign.client.UsuarioClient
import com.nadir.apiprodutos.requests.EstoqueRequest
import com.nadir.apiprodutos.requests.ProdutoRequest
import com.nadir.apiprodutos.services.ProdutoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("api/v1/produtos")
class ProdutoController (
    private val produtoService: ProdutoService,
    private val usuarioClient: UsuarioClient
        ) {

    @PostMapping
    fun create(@RequestHeader(value = "Authorization", required = true) authorizationHeader:String, @RequestBody @Valid request: ProdutoRequest) =
        usuarioClient.validaToken(authorizationHeader).run { ResponseEntity(produtoService.save(request.toProdutoEntity(null)), HttpStatus.CREATED) }

    @GetMapping
    fun getAll(@RequestHeader(value = "Authorization", required = true) authorizationHeader:String) =
        usuarioClient.validaToken(authorizationHeader).run { ResponseEntity.ok(produtoService.findAll()) }

    @GetMapping("/{id}")
    fun getById(@RequestHeader(value = "Authorization", required = true) authorizationHeader:String, @PathVariable id : Long) =
        usuarioClient.validaToken(authorizationHeader).run { ResponseEntity.ok(produtoService.findById(id)) }


    @PostMapping("verificaestoque")
    fun verificaEstoque(@RequestHeader(value = "Authorization", required = true) authorizationHeader:String, @RequestBody @Valid request: EstoqueRequest): Boolean {
        usuarioClient.validaToken(authorizationHeader).run {
            request.let {
                val produto: Produto = produtoService.findById(it.idProduto)
                if (produto.quantidade >= it.qtdItensComprados) {
                    produto.quantidade = produto.quantidade.minus(it.qtdItensComprados)
                    produto.quantidadeReservadaCarrinho = produto.quantidadeReservadaCarrinho.plus(it.qtdItensComprados)
                    produtoService.save(produto)
                    return true
                }
                return false
            }
        }
    }

    @PatchMapping("/disable/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun disable(@RequestHeader(value = "Authorization", required = true) authorizationHeader:String, @PathVariable id : Long): Produto{
        usuarioClient.validaToken(authorizationHeader).run {
            if (this == 1L) {
                return produtoService.disable(id)
            } else throw AuthenticationException("Usuário não autorizado")
        }
    }

    @PatchMapping("/enable/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun enable(@RequestHeader(value = "Authorization", required = true) authorizationHeader:String, @PathVariable id : Long): Produto{
        usuarioClient.validaToken(authorizationHeader).run {
            if (this == 1L) {
                return produtoService.enable(id)
            } else throw AuthenticationException("Usuário não autorizado")
        }
    }
}