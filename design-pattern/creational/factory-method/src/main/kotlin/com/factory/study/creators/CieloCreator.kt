package com.factory.study.creators

import com.factory.study.processors.PaymentProcessor
import com.factory.study.processors.CieloProcessor

class CieloCreator : PaymentCreator() {
    override fun createProcessor(): PaymentProcessor = CieloProcessor()
}
