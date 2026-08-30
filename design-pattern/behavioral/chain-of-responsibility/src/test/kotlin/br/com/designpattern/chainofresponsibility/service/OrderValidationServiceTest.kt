package br.com.designpattern.chainofresponsibility.service

import io.mockk.*
every { anyConstructed<BalanceCheckHandler>().handle(any()) } returns ValidationResult(true, "OK")
every { anyConstructed<StockCheckHandler>().handle(any()) } returns ValidationResult(true, "OK")
every { anyConstructed<FraudCheckHandler>().handle(any()) } returns ValidationResult(true, "OK")
every { anyConstructed<AddressCheckHandler>().handle(any()) } returns ValidationResult(true, "OK")

@SpringBootTest
class OrderValidationServiceTest {
    @MockBean
    private lateinit var balanceCheckHandler: BalanceCheckHandler

    @MockBean
    private lateinit var stockCheckHandler: StockCheckHandler

    @MockBean
    private lateinit var fraudCheckHandler: FraudCheckHandler

    @MockBean
    private lateinit var addressCheckHandler: AddressCheckHandler

    @Autowired
    private lateinit var orderValidationService: OrderValidationService

    @Test
    fun `should validate order successfully when all handlers approve`() {
        val order = Order(customerId = 1, productId = 100, quantity = 2, totalAmount = BigDecimal("200.00"))
        
        val result = orderValidationService.validateOrder(order)
        
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun `should reject order when balance check fails`() {
        every { balanceCheckHandler.handle(any()) } returns ValidationResult(false, "Saldo insuficiente")
        
        val order = Order(customerId = 1, productId = 100, quantity = 2, totalAmount = BigDecimal("50.00"))
        
        val result = orderValidationService.validateOrder(order)
        
        assertThat(result.isValid).isFalse()
        assertThat(result.message).isEqualTo("Saldo insuficiente")
    }

    @Test
    fun `should reject order when stock check fails`() {
        every { balanceCheckHandler.handle(any()) } returns ValidationResult(true, "OK")
        every { stockCheckHandler.handle(any()) } returns ValidationResult(false, "Estoque insuficiente")
        
        val order = Order(customerId = 1, productId = 100, quantity = 100, totalAmount = BigDecimal("200.00"))
        
        val result = orderValidationService.validateOrder(order)
        
        assertThat(result.isValid).isFalse()
        assertThat(result.message).isEqualTo("Estoque insuficiente")
    }
}
