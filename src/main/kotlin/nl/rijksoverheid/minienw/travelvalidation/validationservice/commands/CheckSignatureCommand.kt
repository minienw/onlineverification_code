package nl.rijksoverheid.minienw.travelvalidation.validationservice.commands

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.CryptoKeyConverter
import org.bouncycastle.util.encoders.Base64
import org.slf4j.Logger
import org.springframework.stereotype.Component
import java.security.*

private const val SignatureAlgorithmName = "SHA256withECDSA"
private const val KeyType = "EC"
private const val SecurityProviderName = "BC"

@Component
class CheckSignatureCommand(
    val logger: Logger
) {
    fun isValid(
        content: ByteArray,
        signatureBase64: String,
        signatureAlgorithm: String,
        publicKeyBase64: String
    ): Boolean {

        if (!signatureAlgorithm.equals(SignatureAlgorithmName, ignoreCase = true ))
        {
            logger.info("Unsupported dcc signature algorithm.")
            return false
        }
        val publicKey: PublicKey
        try{
            publicKey = CryptoKeyConverter.decodeAsn1DerPkcs1X509Base64ToPublicKey(KeyType, publicKeyBase64)
        }
        catch (ex: Exception)
        {
            logger.info("Could not parse wallet public key.")
            return false
        }

        try {
            val sig = Base64.decode(signatureBase64)
            val signature = Signature.getInstance(SignatureAlgorithmName, SecurityProviderName)
            signature.initVerify(publicKey)
            signature.update(content)
            return signature.verify(sig)
        }
        catch (ex: SignatureException)
        {
            logger.info("SignatureException while validation signature: ${ex.message}")
            return false
        }
        catch (ex: Exception)
        {
            logger.info("Exception while validation signature: ${ex.message}")
            return false
        }
    }
}
