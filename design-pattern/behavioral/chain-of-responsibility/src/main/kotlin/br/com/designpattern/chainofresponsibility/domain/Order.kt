package br.com.designpattern.chainofresponsibility.domain

import java.math.BigDecimal
import javax.persistence.*

@Entity
data class Order(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val customerId: Long,
    val productId: Long,
    val quantity: Int,
    val totalAmount: BigDecimal,
    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.PENDING
) {
    enum class OrderStatus {
        PENDING, APPROVED, REJECTED
    }
}
