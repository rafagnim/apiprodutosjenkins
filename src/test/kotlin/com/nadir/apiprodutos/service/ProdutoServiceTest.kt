package com.nadir.apiprodutos.service

import com.nadir.apiprodutos.components.ProdutoComponent
import com.nadir.apiprodutos.entities.Produto
import com.nadir.apiprodutos.exceptions.EstoqueNaoZeradoException
import com.nadir.apiprodutos.exceptions.NotFoundException
import com.nadir.apiprodutos.repositories.ProdutoRepository
import com.nadir.apiprodutos.services.ProdutoService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.util.*


@SpringBootTest
@ExtendWith(MockitoExtension::class)
class ProdutoServiceTest {
    @InjectMocks
    private lateinit var produtoService: ProdutoService
    @Mock
    private lateinit var produtoRepository: ProdutoRepository

    private lateinit var produto: Produto
    private lateinit var produtoList: List<Produto>

    @BeforeEach
    fun setup() {
        produtoList = listOf(
        ProdutoComponent.createActiveProdutoEntity(),
        ProdutoComponent.createActiveProdutoEntity(),
        ProdutoComponent.createActiveProdutoEntity(),
        ProdutoComponent.createActiveProdutoEntity(),
        ProdutoComponent.createActiveProdutoEntity(),
        ProdutoComponent.createActiveProdutoEntity()
        )
    }

    @Test
    fun `quando solicita allProdutos o repositorio deve retornar uma lista populada`() {
        val listaARetornar: List<Produto>?
        `when`(produtoRepository.findAll()).thenReturn(produtoList)
        listaARetornar = produtoService.findAll()
        verify(produtoRepository, only()).findAll()
        assertEquals(produtoList, listaARetornar)
        assertEquals(produtoList.size, listaARetornar.size)
    }

    @Test
    fun `quando solicitado salva um produto no repository`() {
        produto = ProdutoComponent.createActiveProdutoEntity()
        `when`(produtoRepository.save(produto)).thenReturn(produto)
        produtoService.save(produto)
        verify(produtoRepository, only()).save(produto)
    }

    @Test
    fun `quando desativa um produto este é desativado`() {
        produto = ProdutoComponent.createActiveProdutoEntity()
        produto.quantidade = BigDecimal.ZERO
        produto.quantidadeReservadaCarrinho = BigDecimal.ZERO
        val statusAnterior: Boolean = produto.isActive
        val statusPosterior: Boolean
        val id: Long = produto.id!!

        `when`(produtoRepository.findById(id)).thenReturn(Optional.of(produto))
        `when`(produtoRepository.save(produto)).thenReturn(produto)
        statusPosterior = produtoService.disable(id).isActive

        verify(produtoRepository, atLeastOnce()).findById(id)
        verify(produtoRepository, atLeastOnce()).save(produto)

        assertEquals(true, statusAnterior)
        assertEquals(false, statusPosterior)
        assertNotEquals(statusPosterior, statusAnterior)
    }

    @Test
    fun `quando desativa um produto com estoque não zerado espera mensagem de excecao`() {
        produto = ProdutoComponent.createActiveProdutoEntity()
        produto.quantidade = BigDecimal.TEN
        produto.quantidadeReservadaCarrinho = BigDecimal.ONE
        val id: Long = produto.id!!
        `when`(produtoRepository.findById(id)).thenReturn(Optional.of(produto))
        try{
            produtoService.disable(id).isActive
            verify(produtoRepository, atLeastOnce()).findById(id)
            verify(produtoRepository, atLeastOnce()).save(produto)
        } catch(ex: EstoqueNaoZeradoException) {
            assertEquals("O estoque precisa estar zerado para desativar o produto".format(id), ex.message)
        }
    }


    @Test
    fun `quando se tenta desativar com id inexistente lanca excecao NotFound`() {
        val id: Long = 10L
        `when`(produtoRepository.findById(id)).thenReturn(Optional.empty())
        assertThrows<NotFoundException> { produtoService.disable(id) }
    }

    @Test
    fun `quando se tenta desativar com id inexistente lanca excecao`() {
        val id: Long = 10L
        `when`(produtoRepository.findById(id)).thenReturn(Optional.empty())
        try{
            produtoService.disable(id)
        } catch (ex: NotFoundException) {
            assertEquals("Produto com id %s não localizado.".format(id), ex.message)
        }
    }

    @Test
    fun `quando ativa um produto este é ativado`() {
        produto = ProdutoComponent.createInactiveProdutoEntity()
        val statusAnterior: Boolean = produto.isActive
        val statusPosterior: Boolean
        val id: Long = produto.id!!

        `when`(produtoRepository.findById(id)).thenReturn(Optional.of(produto))
        `when`(produtoRepository.save(produto)).thenReturn(produto)
        statusPosterior = produtoService.enable(id).isActive

        verify(produtoRepository, atLeastOnce()).findById(id)
        verify(produtoRepository, atLeastOnce()).save(produto)

        assertEquals(false, statusAnterior)
        assertEquals(true, statusPosterior)
        assertNotEquals(statusPosterior, statusAnterior)
    }

    @Test
    fun `quando busca um produto por id este é encontrado`() {
        produto = ProdutoComponent.createActiveProdutoEntity()
        val id: Long = produto.id!!
        `when`(produtoRepository.findById(id)).thenReturn(Optional.of(produto))
        var produtoARetornar = produtoService.findById(id)
        verify(produtoRepository, only()).findById(id)
        assertEquals(produto, produtoARetornar)
    }
}