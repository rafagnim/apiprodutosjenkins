package com.nadir.apiprodutos.requests

import com.nadir.apiprodutos.entities.Produto
import java.math.BigDecimal
import javax.validation.constraints.NotEmpty

data class ProdutoRequest (
    @field:NotEmpty(message = "Obrigatório informar o nome")
    val nome: String,
    //@field:NotEmpty(message = "Obrigatório informar a quantidade")
    val quantidade: BigDecimal,
    val quantidadeReservadaCarrinho: BigDecimal?,
    //@field:NotEmpty(message = "Obrigatório informar o valor unitário")
    val valorUnitario: BigDecimal,
    @field:NotEmpty(message = "Obrigatório informar a descrição")
    val descricao: String,
        ){
    fun toProdutoEntity(produto: Produto?): Produto {
        return if (produto == null)
            Produto(
                nome = this.nome,
                quantidade = this.quantidade,
                quantidadeReservadaCarrinho = BigDecimal.ZERO,
                valorUnitario = this.valorUnitario,
                descricao = this.descricao,
                isActive = true
            )
        else
            Produto(
                id = produto.id,
                nome = produto.nome,
                quantidade = produto.quantidade,
                quantidadeReservadaCarrinho = produto.quantidadeReservadaCarrinho,
                valorUnitario = produto.valorUnitario,
                descricao = produto.descricao,
                isActive =  produto.isActive
            )
    }
}
