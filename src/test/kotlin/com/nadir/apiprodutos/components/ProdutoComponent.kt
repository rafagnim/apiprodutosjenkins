package com.nadir.apiprodutos.components

import com.nadir.apiprodutos.entities.Produto
import com.nadir.apiprodutos.requests.ProdutoRequest
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Random

class ProdutoComponent {

    companion object {
        fun createActiveProdutoEntity(
            id: Long = (0L..1000L).random(),
            nome: String = "Produto ${Random().nextInt(1000)}",
            quantidade: BigDecimal = BigDecimal.valueOf((0L..1000L).random()),
            quantidadeReservadaCarrinho: BigDecimal = quantidade.multiply(BigDecimal.valueOf(0.05)).setScale(0, RoundingMode.DOWN),
            valorUnitario: BigDecimal = BigDecimal.valueOf((1L..10L).random()),
            descricao: String =  "Produto Descricao ${Random().nextInt(1000)}",
            isActive: Boolean = true,
        ) : Produto {
            return Produto(id, nome, quantidade, quantidadeReservadaCarrinho, valorUnitario, descricao, isActive)
        }

        fun createInactiveProdutoEntity(
            id: Long = (0L..1000L).random(),
            nome: String = "Produto ${Random().nextInt(1000)}",
            quantidade: BigDecimal = BigDecimal.ZERO,
            quantidadeReservadaCarrinho: BigDecimal = BigDecimal.ZERO,
            valorUnitario: BigDecimal = BigDecimal.valueOf((1L..10L).random()),
            descricao: String =  "Produto Descricao ${Random().nextInt(1000)}",
            isActive: Boolean = false,
        ) : Produto {
            return Produto(id, nome, quantidade, quantidadeReservadaCarrinho, valorUnitario, descricao, isActive)
        }

        fun createProdutoRequest(
            nome: String = "Produto ${Random().nextInt(1000)}",
            quantidade: BigDecimal = BigDecimal.valueOf((0L..1000L).random()),
            quantidadeReservadaCarrinho: BigDecimal = quantidade.multiply(BigDecimal.valueOf(0.05)).setScale(0, RoundingMode.DOWN),
            valorUnitario: BigDecimal = BigDecimal.valueOf((1L..10L).random()),
            descricao: String =  "Produto Descricao ${Random().nextInt(1000)}"
        ) : ProdutoRequest {
            return ProdutoRequest(nome, quantidade, quantidadeReservadaCarrinho, valorUnitario, descricao)
        }
    }
}