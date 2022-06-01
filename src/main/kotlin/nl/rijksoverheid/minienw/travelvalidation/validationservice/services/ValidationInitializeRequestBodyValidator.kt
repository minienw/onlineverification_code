package nl.rijksoverheid.minienw.travelvalidation.validationservice.commands

import nl.rijksoverheid.minienw.travelvalidation.api.data.initialize.*
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.CryptoKeyConverter
import org.bouncycastle.util.encoders.Base64
import org.bouncycastle.util.encoders.DecoderException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class ValidationInitializeRequestBodyValidatorV2 {
    val result = ArrayList<String>()

    fun validate(body: ValidationInitializeRequestBody): List<String> {
        validateNonce(body.nonce)

        if (!body.walletPublicKeyAlgorithm.equals("SHA256withECDSA", ignoreCase = true ))
        {
            //TODO log
            result.add("Wallet signing algorithm is not supported.")
        }

        try {
            CryptoKeyConverter.decodeAsn1DerPkcs1X509PemEllipticCurvePublicKey(body.walletPublicKey!!);
        }
        catch(ex: Exception)
        {
            result.add("Wallet signing key is not supported.")
        }

        return result;
    }

    private fun validateNonce(nonce:String?){
        try {
            if (nonce.isNullOrEmpty()) {
                result.add("Nonce empty.")
                return;
            }

            if (Base64.decode(nonce).size != 16)
                result.add("Nonce is not correct length (16 bytes).")

        } catch (e: DecoderException) {
            result.add("IV is not valid base64.")
        }
    }

}
