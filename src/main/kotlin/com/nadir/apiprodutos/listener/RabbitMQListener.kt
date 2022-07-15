package com.nadir.apiprodutos.listener

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.nadir.apiprodutos.entities.Entrega
import com.nadir.apiprodutos.entities.Produto
import com.nadir.apiprodutos.services.ProdutoService
import com.nadir.apiprodutos.utils.QUEUEPARAPRODUTO
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.stereotype.Service

@Service
class RabbitMQListener (
    private val messageConverter: MessageConverter,
    private val produtoService: ProdutoService
        ) {
    private val log = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = [QUEUEPARAPRODUTO])
    fun receiveUserModelMessage(message : Message){
        log.info("receiving message from entrega model queue ${message.messageProperties.consumerQueue}")
        val entrega = messageConverter.fromMessage(message).toString()
        var mapper = jacksonObjectMapper()
        val baixaEstoque = mapper.readValue<Entrega>(entrega)
        log.info("$baixaEstoque")
        var produto: Produto = produtoService.findById(baixaEstoque.idProduto)
        produto.quantidadeReservadaCarrinho = produto.quantidadeReservadaCarrinho.minus(baixaEstoque.qtdItensComprados)
        produtoService.save(produto)
    }
}