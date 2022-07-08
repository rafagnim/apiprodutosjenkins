package com.nadir.apiprodutos.repositories

import com.nadir.apiprodutos.entities.Produto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProdutoRepository : JpaRepository<Produto, Long> {
}