package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.crypto

import org.springframework.stereotype.Component
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.MGF1ParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

@Component
class RsaEcbOaepWithSha256DecryptCommand
{
    companion object {
        private const val KeyCipher = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
        //private const val KeyCipherFamily = "RSA"
        private const val MessageDigestFunction = "SHA-256"
        private const val MaskGenerationFunction = "MGF1"
    }

    fun execute(cipherText: ByteArray, privateKey: PrivateKey): ByteArray
    {
        if (cipherText.size == 0)
                throw IllegalArgumentException()

        val (keyCipher, parameter) = getCipher()
        keyCipher.init(Cipher.DECRYPT_MODE, privateKey, parameter)
        return keyCipher.doFinal(cipherText)
    }

    fun testEncrypt(plainText: ByteArray, publicKey: PublicKey) : ByteArray
    {
        if (plainText.size == 0)
            throw IllegalArgumentException()

        val (keyCipher, parameter) = getCipher()
        keyCipher.init(Cipher.ENCRYPT_MODE, publicKey, parameter)
        return keyCipher.doFinal(plainText)
    }

    private fun getCipher(): Pair<Cipher, OAEPParameterSpec> {
        val keyCipher = Cipher.getInstance(KeyCipher)
        val parameter = OAEPParameterSpec(
            MessageDigestFunction,
            MaskGenerationFunction,
            MGF1ParameterSpec.SHA256,
            PSource.PSpecified.DEFAULT
        )
        return Pair(keyCipher, parameter)
    }
}