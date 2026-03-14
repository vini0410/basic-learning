package com.abstract.study.factories

import com.abstract.study.email.*
import com.abstract.study.sms.*
import com.abstract.study.push.*
import kotlin.test.Test
import kotlin.test.assertTrue

class NotificationFactoryTest {

    @Test
    fun `EmailFactory deve criar produtos da familia Email`() {
        val factory = EmailFactory
        
        assertTrue(factory.createSender() is EmailSender)
        assertTrue(factory.createFormatter() is EmailFormatter)
        assertTrue(factory.createLogger() is EmailLogger)
    }

    @Test
    fun `SmsFactory deve criar produtos da familia SMS`() {
        val factory = SmsFactory
        
        assertTrue(factory.createSender() is SmsSender)
        assertTrue(factory.createFormatter() is SmsFormatter)
        assertTrue(factory.createLogger() is SmsLogger)
    }

    @Test
    fun `PushFactory deve criar produtos da familia Push`() {
        val factory = PushFactory
        
        assertTrue(factory.createSender() is PushSender)
        assertTrue(factory.createFormatter() is PushFormatter)
        assertTrue(factory.createLogger() is PushLogger)
    }
}
