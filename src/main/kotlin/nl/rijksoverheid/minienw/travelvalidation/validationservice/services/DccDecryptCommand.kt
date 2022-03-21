package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.crypto.RsaOaepWithSha256AesCbcSchemeDecryptCommand
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.crypto.RsaOaepWithSha256AesGcmSchemeDecryptCommand
import org.springframework.stereotype.Component

/**
 * Router for crypto schemes
 * */
@Component
class DccDecryptCommand
(
    //TODO should be list...
    val _Scheme1 : RsaOaepWithSha256AesCbcSchemeDecryptCommand,
    val _Scheme2 : RsaOaepWithSha256AesGcmSchemeDecryptCommand,
)
{
    fun canExecute(scheme: String) : Boolean
    {
        return scheme?.equals(_Scheme1.name, ignoreCase = true) || scheme?.equals(_Scheme2.name, ignoreCase = true)
    }

    fun execute(scheme: String, cipherText : ByteArray, secretKey: ByteArray, iv: ByteArray):ByteArray
    {
        if (scheme == _Scheme1.name)
            return _Scheme1.execute(cipherText, secretKey, iv)

        if (scheme == _Scheme2.name)
            return _Scheme2.execute(cipherText, secretKey, iv)

        throw IllegalArgumentException("Scheme is not supported.")
    }
}