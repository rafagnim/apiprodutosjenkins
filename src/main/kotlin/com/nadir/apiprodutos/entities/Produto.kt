package com.nadir.apiprodutos.entities

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import javax.persistence.*

@Entity
@Table(name = "tb_produto")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Produto (

    @Id
    @Column(name = "produto_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "nome")
    var nome: String,

    @Column(name = "quantidade")
    var quantidade: BigDecimal,

    @Column(name = "quantidade_reservada_carrinho")
    var quantidadeReservadaCarrinho: BigDecimal = BigDecimal.ZERO,

    @Column(name = "vlr_unitario")
    var valorUnitario: BigDecimal,

    @Column(name = "descricao")
    var descricao: String,

    @Column(name = "is_active")
    var isActive: Boolean

//    -> Nome;
//    -> Quantidade; (Estoque)
//    -> Valor unitário;
//    -> Descrição; (Breve descrição sobre o produto, não deve passar de 100 caracteres)
//    -> IsActive;
) {

}