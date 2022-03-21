package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.crypto

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.CryptoKeyConverter
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import org.springframework.stereotype.Component

@Component
class RsaOaepWithSha256AesGcmSchemeDecryptCommand
    (
    private val appSettings : IApplicationSettings,
    private val keyDecryptionCommand: RsaEcbOaepWithSha256DecryptCommand,
    private val payloadDecryptionCommand: AesGcmDecryptCommand,
)
{
    companion object{
        val Name: String = "RsaOaepWithSha256AesGcmScheme"
    }
        val name: String get() = Name

        fun execute(cipherText : ByteArray, secretKey: ByteArray, iv: ByteArray) : ByteArray
        {
            val secretKeyParameter = keyDecryptionCommand.execute(secretKey,
                CryptoKeyConverter.decodeAsn1DerPkcs8Base64ToPrivateKey("RSA", appSettings.dccEncryptionRsaPrivateKey)
            )
            return payloadDecryptionCommand.execute(cipherText, secretKeyParameter, iv)
        }

}