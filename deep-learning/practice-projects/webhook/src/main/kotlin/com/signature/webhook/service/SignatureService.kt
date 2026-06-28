package com.signature.webhook.service

import org.springframework.stereotype.Service
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.util.HexFormat

@Service
class SignatureService {
    private val secret = "minha_chave_secreta_estudo"
    private val algorithm = "HmacSHA256"

    fun calculateSignature(payload: String): String {
        val keySpec = SecretKeySpec(secret.toByteArray(), algorithm)
        val mac = Mac.getInstance(algorithm)
        mac.init(keySpec)
        val hash = mac.doFinal(payload.toByteArray())
        return HexFormat.of().formatHex(hash)
    }

    fun verifySignature(payload: String, signature: String): Boolean {
        val expectedSignatureHex = calculateSignature(payload)
        val expectedSignatureBytes = HexFormat.of().parseHex(expectedSignatureHex)
        
        val signatureBytes: ByteArray
        try {
            signatureBytes = HexFormat.of().parseHex(signature)
        } catch (e: NumberFormatException) {
            // If the signature string is not a valid hex format, it's an invalid signature
            return false
        }

        return MessageDigest.isEqual(expectedSignatureBytes, signatureBytes)
    }
}