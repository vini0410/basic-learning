package com.abstract.study.products

import com.abstract.study.email.EmailFormatter
import com.abstract.study.push.PushFormatter
import com.abstract.study.sms.SmsFormatter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FormatterTest {

    @Test
    fun `EmailFormatter deve adicionar tags HTML`() {
        val formatter = EmailFormatter()
        val result = formatter.format("Olá")
        assertEquals("<html><body>Olá</body></html>", result)
    }

    @Test
    fun `SmsFormatter deve converter para uppercase e truncar em 160 chars`() {
        val formatter = SmsFormatter()
        val longText = "a".repeat(200)
        val result = formatter.format(longText)
        
        assertEquals(160, result.length)
        assertTrue(result.all { it.isUpperCase() })
    }

    @Test
    fun `PushFormatter deve retornar um payload JSON`() {
        val formatter = PushFormatter()
        val result = formatter.format("Teste")
        assertEquals("{\"title\": \"Aviso\", \"body\": \"Teste\"}", result)
    }
}
