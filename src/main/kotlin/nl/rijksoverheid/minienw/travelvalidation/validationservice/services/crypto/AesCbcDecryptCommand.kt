package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.crypto

import org.springframework.stereotype.Component
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class AesCbcDecryptCommand {
    companion object
    {
        private const val DataCipher = "AES/CBC/PKCS5Padding"
        private const val DataCipherFamily = "AES"
    }

    fun execute(cipherText : ByteArray, secretKey: ByteArray, iv: ByteArray) : ByteArray
    {
        return execute(Cipher.DECRYPT_MODE, cipherText, secretKey, iv)
    }

    fun testEncrypt(cipherText : ByteArray, secretKey: ByteArray, iv: ByteArray) : ByteArray
    {
        return execute(Cipher.ENCRYPT_MODE, cipherText, secretKey, iv)
    }

    private fun execute(mode: Int, cipherText: ByteArray, secretKey: ByteArray, iv: ByteArray): ByteArray
    {
        if (cipherText.size == 0)
            throw IllegalArgumentException();
        if (secretKey.size != 32)
            throw IllegalArgumentException();
        if (iv.size != 16)
            throw IllegalArgumentException();

        val secretKeySpec = SecretKeySpec(secretKey, DataCipherFamily)

        val cipher = Cipher.getInstance(DataCipher)
        cipher.init(mode, secretKeySpec, IvParameterSpec(iv))
        return cipher.doFinal(cipherText)
    }
}