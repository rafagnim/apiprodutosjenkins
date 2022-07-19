package com.nadir.apiprodutos.service

import com.nadir.apiprodutos.components.ProdutoComponent
import com.nadir.apiprodutos.entities.Produto
import com.nadir.apiprodutos.requests.ProdutoRequest
import com.nadir.apiprodutos.services.ProdutoService
import junit.framework.Assert.assertEquals
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
@SpringBootTest
//@ActiveProfiles("test.properties")
class ProdutoServiceSemMockTest {

    @Autowired
    private lateinit var produtoService: ProdutoService

    private lateinit var produtoRequest: ProdutoRequest
    private lateinit var produtoRequest1: ProdutoRequest
    private lateinit var produto: Produto

    companion object {
        @Container
        val container = MySQLContainer<Nothing>("mysql").apply {
            withDatabaseName("apiprodutostest")
            withUsername("root")
            withPassword("")
        }

        @Container
        val redis = GenericContainer<Nothing>("redis:5.0.8-alpine3.11").apply {
            withExposedPorts(6379)
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", container::getJdbcUrl);
            registry.add("spring.datasource.password", container::getPassword);
            registry.add("spring.datasource.username", container::getUsername);
        }
    }

    @BeforeEach
    fun setup(){
        redis.start()
    }

    @AfterEach
    fun down(){
        redis.stop()
    }

    @Test
    fun `quando solicitado salva um produto no repository`() {
        produtoRequest = ProdutoComponent.createProdutoRequest()
        produto = produtoService.save(produtoRequest.toProdutoEntity(null))
        val produtoResponse: Produto = produtoService.findById(produto.id!!)
        assertEquals(produtoRequest.nome, produtoResponse.nome)

        produtoRequest1 = ProdutoComponent.createProdutoRequest()
        produto = produtoService.save(produtoRequest1.toProdutoEntity(null))
        val produtoResponse1: Produto = produtoService.findById(produto.id!!)
        assertEquals(produtoRequest1.nome, produtoResponse1.nome)
    }
}