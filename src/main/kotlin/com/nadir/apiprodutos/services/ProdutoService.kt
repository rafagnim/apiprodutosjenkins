package com.nadir.apiprodutos.services

import com.nadir.apiprodutos.entities.Produto
import com.nadir.apiprodutos.exceptions.EstoqueNaoZeradoException
import com.nadir.apiprodutos.exceptions.NotFoundException
import com.nadir.apiprodutos.repositories.ProdutoRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ProdutoService (
    private val produtoRepository: ProdutoRepository
        ) {
    @CacheEvict(allEntries = true, cacheNames = ["getAll"])
    fun save(produto: Produto) = produtoRepository.save(produto)

    @Cacheable("getAll")
    fun findAll() = produtoRepository.findAll()

    @CacheEvict(allEntries = true, cacheNames = ["getAll"])
    fun disable(id: Long) = produtoRepository.findById(id).orElseThrow {
            NotFoundException("Produto com id %s não localizado.".format(id)) }.let {
            if (it.quantidade.compareTo(BigDecimal.ZERO) == 0 && it.quantidadeReservadaCarrinho.compareTo(BigDecimal.ZERO) == 0) {
                it.isActive = false
                produtoRepository.save(it)
            } else {
                throw EstoqueNaoZeradoException("O estoque precisa estar zerado para desativar o produto")
            }
    }

    @CacheEvict(allEntries = true, cacheNames = ["getAll"])
    fun enable(id: Long) = produtoRepository.findById(id).orElseThrow { NotFoundException("User with id %s not exists.".format(id)) }.let {
        it.isActive = true
        produtoRepository.save(it)
    }

    fun findById(id: Long) = produtoRepository.findById(id).orElseThrow()
}