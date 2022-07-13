package com.nadir.apiprodutos.service

import com.nadir.apiprodutos.components.ProdutoComponent
import com.nadir.apiprodutos.components.ProdutoComponent.Companion.getListActiveProdutoEntity
import com.nadir.apiprodutos.entities.Produto
import com.nadir.apiprodutos.exceptions.EstoqueNaoZeradoException
import com.nadir.apiprodutos.exceptions.NotFoundException
import com.nadir.apiprodutos.repositories.ProdutoRepository
import com.nadir.apiprodutos.services.ProdutoService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.util.*

@Testcontainers
@SpringBootTest
@ExtendWith(MockKExtension::class)
class ProductServiceTestContainer {

    @InjectMockKs
    private lateinit var productService: ProdutoService

    @MockK
    private lateinit var productRepository: ProdutoRepository

    private lateinit var product: Produto
    private lateinit var productList: List<Produto>

    companion object {
        @Container
        val container = MySQLContainer<Nothing>("mysql").apply {
            withDatabaseName("apiprodutos")
            withUsername("root")
            withPassword("admin")
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
    fun setup() {
        productList = getListActiveProdutoEntity(5)
    }

    @Test
    fun `quando solicita allProdutos o repositorio deve retornar uma lista populada`() {
        every { productRepository.findAll() } returns productList

        val products = productService.findAll()

        verify(exactly = 1) { productRepository.findAll() }
        Assertions.assertEquals(productList, products)
        Assertions.assertEquals(productList.size, products.size)
    }

    @Test
    fun `quando solicitado salva um produto no repository`() {
        product = ProdutoComponent.createActiveProdutoEntity()
        every { productRepository.save(product) } returns product

        val product = productService.save(product)

        verify(exactly = 1) { productRepository.save(any())}
        Assertions.assertEquals(product, this.product)
    }

    @Test
    fun `quando desativa um produto este é desativado`() {
        product = ProdutoComponent.createActiveProdutoEntity()
        product.quantidade = BigDecimal.ZERO
        product.quantidadeReservadaCarrinho = BigDecimal.ZERO
        val statusAnterior: Boolean = product.isActive
        val statusPosterior: Boolean
        val id: Long = product.id!!


        every { productRepository.findById(id) } returns Optional.of(product)
        every { productRepository.save(product) } returns product

        statusPosterior = productService.disable(id).isActive

        verify(exactly = 1) { productRepository.save(any())}
        verify(exactly = 1) { productRepository.findById(id)}


        Assertions.assertEquals(true, statusAnterior)
        Assertions.assertEquals(false, statusPosterior)
        Assertions.assertNotEquals(statusPosterior, statusAnterior)
    }

    @Test
    fun `quando desativa um produto com estoque não zerado espera mensagem de excecao`() {
        product = ProdutoComponent.createActiveProdutoEntity()
        product.quantidade = BigDecimal.TEN
        product.quantidadeReservadaCarrinho = BigDecimal.ONE
        val id: Long = product.id!!

        every { productRepository.findById(id) } returns Optional.of(product)
        try{
            productService.disable(id).isActive
            Mockito.verify(productRepository, Mockito.atLeastOnce()).findById(id)
            Mockito.verify(productRepository, Mockito.atLeastOnce()).save(product)
        } catch(ex: EstoqueNaoZeradoException) {
            Assertions.assertEquals("O estoque precisa estar zerado para desativar o produto".format(id), ex.message)
        }
    }


    @Test
    fun `quando se tenta desativar com id inexistente lanca excecao NotFound`() {
        product = ProdutoComponent.createInactiveProdutoEntity()

        val id = 10L
        every { productRepository.findById(id) } returns Optional.empty()
        every { productRepository.save(product) } returns product
        assertThrows<NotFoundException> { productService.disable(id) }
    }

    @Test
    fun `quando se tenta desativar com id inexistente lanca excecao`() {
        product = ProdutoComponent.createInactiveProdutoEntity()

        val id: Long = product.id!!

        every { productRepository.findById(id) } returns Optional.of(product)
        every { productRepository.save(product) } returns product

        try{
            productService.disable(id)
        } catch (ex: NotFoundException) {
            Assertions.assertEquals("Produto com id %s não localizado.".format(id), ex.message)
        }
    }

    @Test
    fun `quando ativa um produto este é ativado`() {
        product = ProdutoComponent.createInactiveProdutoEntity()
        val statusAnterior: Boolean = product.isActive
        val statusPosterior: Boolean
        val id: Long = product.id!!

        every { productRepository.findById(id) } returns Optional.of(product)
        every { productRepository.save(product) } returns product


        statusPosterior = productService.enable(id).isActive

        verify(exactly = 1) { productRepository.findById(any()) }
        verify(exactly = 1) { productRepository.save(product) }

        Assertions.assertEquals(false, statusAnterior)
        Assertions.assertEquals(true, statusPosterior)
        Assertions.assertNotEquals(statusPosterior, statusAnterior)
    }

    @Test
    fun `quando busca um produto por id este é encontrado`() {
        product = ProdutoComponent.createActiveProdutoEntity()
        val id: Long = product.id!!

        every{ productRepository.findById(id)} returns Optional.of(product)

        val productReturn = productService.findById(id)
        verify(exactly = 1) { productRepository.findById(any()) }
        Assertions.assertEquals(product, productReturn)
    }




}
