package nl.rijksoverheid.minienw.travelvalidation.validationservice.commands

import nl.rijksoverheid.minienw.travelvalidation.api.data.initialize.*
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.CryptoKeyConverter
import org.bouncycastle.util.encoders.Base64
import org.bouncycastle.util.encoders.DecoderException
import org.springframework.stereotype.Component

@Component
class ValidationInitializeRequestBodyValidatorV2 {

    fun validate(body: ValidationInitializeRequestBody): List<String> {
        val result = ArrayList<String>()

        try {
            if (body.nonce.isNullOrEmpty()) {
                result.add("Nonce empty.")
            }
            else
            {
                if (Base64.decode(body.nonce).size != 16)
                    result.add("Nonce is not correct length (16 bytes).")
            }

        } catch (e: DecoderException) {
            result.add("IV is not valid base64.")
        }

        if (!body.walletPublicKeyAlgorithm.equals("SHA256withECDSA", ignoreCase = true ))
        {
            result.add("Wallet signing algorithm is not supported.")
        }

        try {
            CryptoKeyConverter.decodeAsn1DerPkcs1X509PemEllipticCurvePublicKey(body.walletPublicKey!!)
        }
        catch(ex: Exception)
        {
            result.add("Wallet signing key is not supported.")
        }

        return result
    }
}
