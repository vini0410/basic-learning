package com.factory.study.models

import com.factory.study.creators.*

enum class PaymentMethod(val creator: PaymentCreator) {
    STRIPE(StripeCreator()),
    PAYPAL(PayPalCreator()),
    CIELO(CieloCreator());

    companion object {
        fun allowedValues(): String = entries.joinToString(", ")

        fun invalidMessage(value: String): String =
            "Método de pagamento '$value' inválido. Valores aceitos: ${allowedValues()}"
    }
}
