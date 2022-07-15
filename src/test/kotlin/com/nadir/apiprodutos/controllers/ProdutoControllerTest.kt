package com.nadir.apiprodutos.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.nadir.apiprodutos.components.ProdutoComponent
import com.nadir.apiprodutos.entities.Produto
import com.nadir.apiprodutos.exceptions.AuthenticationException
import com.nadir.apiprodutos.integration.feign.client.UsuarioClient
import com.nadir.apiprodutos.repositories.ProdutoRepository
import com.nadir.apiprodutos.requests.EstoqueRequest
import com.nadir.apiprodutos.requests.ProdutoRequest
import com.nadir.apiprodutos.services.ProdutoService
import com.nadir.apiprodutos.util.AbstractTest
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal


private const val AUTHORIZATIONHEADER: String = "Bearer eh..."

@ExtendWith(MockitoExtension::class)
class ProdutoControllerTest(): AbstractTest() {


    private lateinit var produtoAtivo: Produto
    private lateinit var produtoInativo: Produto
    private var id: Long = 0
    private lateinit var produtoLista: List<Produto>
    private lateinit var produtoRequest: ProdutoRequest

    @MockBean
    private lateinit var usuarioClient: UsuarioClient

    @MockBean
    private lateinit var produtoService: ProdutoService

    @SpyBean
    private lateinit var produtoController: ProdutoController

    @Captor
    private lateinit var captor: ArgumentCaptor<Long>

    @BeforeEach
    fun setup() {
        produtoAtivo = ProdutoComponent.createActiveProdutoEntity()
        produtoInativo = ProdutoComponent.createInactiveProdutoEntity()
        produtoLista = listOf(
            produtoAtivo, produtoInativo
        )
        produtoRequest = ProdutoComponent.createProdutoRequest()

        `when`(usuarioClient.validaToken(AUTHORIZATIONHEADER)).thenReturn(1L )

    }

//    @AfterEach
//    fun apagaDB() {
//        //produtoRepository.deleteAll()
//    }

    @Test
    fun `quando getAll produtos do DB`() {
        `when`(produtoService.findAll()).thenReturn(produtoLista)

        this.mockMvc.perform(
            get("/api/v1/produtos")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATIONHEADER)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].id").value(produtoAtivo.id))

        `when`(usuarioClient.validaToken(AUTHORIZATIONHEADER)).thenReturn(10L);
        assertEquals(10L, usuarioClient.validaToken(AUTHORIZATIONHEADER))

        verify(produtoController).getAll(AUTHORIZATIONHEADER)

        var ordem: InOrder = inOrder(produtoController)
        ordem.verify(produtoController).getAll(AUTHORIZATIONHEADER)

        verify(produtoController, times(1)).getAll(ArgumentMatchers.anyString())
    }

    @Test
    fun `quando nao ha interacoes na Controller`() {
        verifyNoInteractions(produtoController)
    }

    @Test
    fun `quando se busca um produto por id`() {
        `when`(produtoService.findById(1)).thenReturn(produtoAtivo)

        this.mockMvc.perform(
            get("/api/v1/produtos/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATIONHEADER)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.id").value(produtoAtivo.id))

        verify(produtoController, times(1)).getById(AUTHORIZATIONHEADER, 1L)

        verify(produtoService).findById(captor.capture())
        val idCapturado = captor.value
        assertEquals(1, idCapturado)
    }

    @Test
    fun `quando cria um produto retorna um produto criado, com id`() {
        `when`(produtoService.save(produtoRequest.toProdutoEntity(null))).thenReturn(
            produtoRequest.toProdutoEntity(null).also { it.id = 100L}
        )

        this.mockMvc.perform(
            post("/api/v1/produtos")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATIONHEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(produtoRequest)))
                .andDo(print())
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.descricao").value(produtoRequest.descricao))
    }

    @Test
    fun `quando verifica estoque e este existe deve retornar true`() {
        val estoqueRequest = EstoqueRequest(produtoAtivo.id!!, BigDecimal.TEN)
        produtoAtivo.quantidade = BigDecimal.TEN
        `when`(produtoService.findById(estoqueRequest.idProduto)).thenReturn(produtoAtivo)

        this.mockMvc.perform(
            post("/api/v1/produtos/verificaestoque")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATIONHEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(estoqueRequest)))
                .andDo(print())
                .andExpect(jsonPath("$").value(true))
    }
    
    @Test
    fun `quando verifica estoque e este não existe deve retornar false`() {
        val estoqueRequest = EstoqueRequest(produtoAtivo.id!!, BigDecimal.TEN)
        produtoAtivo.quantidade = BigDecimal.ONE
        `when`(produtoService.findById(estoqueRequest.idProduto)).thenReturn(produtoAtivo)

        this.mockMvc.perform(
            post("/api/v1/produtos/verificaestoque")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATIONHEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(estoqueRequest)))
            .andDo(print())
            .andExpect(jsonPath("$").value(false))
    }

    @Test
    fun `quando desativa um produto o isActive deve retornar false`() {
        `when`(produtoService.disable(ArgumentMatchers.anyLong())).thenReturn(produtoAtivo.also { it.isActive = false })
        this.mockMvc.perform(
            patch("/api/v1/produtos/disable/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATIONHEADER)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.isActive").value(false))
    }

    @Test
    @Throws(Exception::class)
    fun `quando desativa um produto e o usuario logado nao tem autorizacao deve lancar excecao`() {
        `when`(usuarioClient.validaToken(AUTHORIZATIONHEADER)).thenReturn(2L )
        this.mockMvc.perform(
            patch("/api/v1/produtos/disable/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATIONHEADER)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isUnauthorized)
            .andExpect { result -> assertTrue(result.resolvedException is AuthenticationException) }
            .andExpect { result -> assertEquals("Usuário não autorizado", result.resolvedException?.message )}
    }

    @Test
    fun `quando ativa um produto o isActive deve retornar true`() {
        `when`(produtoService.enable(ArgumentMatchers.anyLong())).thenReturn(produtoInativo.also { it.isActive = true })
        this.mockMvc.perform(
            patch("/api/v1/produtos/enable/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATIONHEADER)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.isActive").value(true))
    }

    @Test
    @Throws(Exception::class)
    fun `quando ativa um produto e o usuario logado nao tem autorizacao deve lancar excecao`() {
        `when`(usuarioClient.validaToken(AUTHORIZATIONHEADER)).thenReturn(2L )
        this.mockMvc.perform(
            patch("/api/v1/produtos/enable/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATIONHEADER)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isUnauthorized)
            .andExpect { result -> assertTrue(result.resolvedException is AuthenticationException) }
            .andExpect { result -> assertEquals("Usuário não autorizado", result.resolvedException?.message )}
    }
}