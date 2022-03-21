package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.crypto

import org.springframework.stereotype.Component
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


@Component
class AesGcmDecryptCommand
{
    companion object
    {
        private const val DataCipher = "AES/GCM/NoPadding"
        private const val DataCipherFamily = "AES"
    }

    fun execute(cipherText : ByteArray, secretKey: ByteArray, iv: ByteArray) : ByteArray
            = execute(Cipher.DECRYPT_MODE, cipherText, secretKey, iv)

    fun testEncrypt(cipherText : ByteArray, secretKey: ByteArray, iv: ByteArray) : ByteArray
            = execute(Cipher.ENCRYPT_MODE, cipherText, secretKey, iv)

    private fun execute(mode: Int, cipherText : ByteArray, secretKey: ByteArray, iv: ByteArray) : ByteArray
    {
        if (cipherText.size == 0)
            throw IllegalArgumentException("cipherText");

        if (secretKey.size != 32)
            throw IllegalArgumentException("secretKey");

        if (iv.size < 12)
            throw IllegalArgumentException("iv");

        val secretKeySpec = SecretKeySpec(secretKey, DataCipherFamily)
        val gcmParameterSpec = GCMParameterSpec(iv.size * 8, iv)
        val cipher = Cipher.getInstance(DataCipher)
        cipher.init(mode, secretKeySpec, gcmParameterSpec)
        return cipher.doFinal(cipherText)
    }
}