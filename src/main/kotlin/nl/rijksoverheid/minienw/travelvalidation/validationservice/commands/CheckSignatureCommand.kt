package nl.rijksoverheid.minienw.travelvalidation.validationservice.commands

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.CryptoKeyConverter
import org.bouncycastle.util.encoders.Base64
import org.slf4j.LoggerFactory
import java.security.*
import java.security.spec.ECParameterSpec
import java.security.spec.X509EncodedKeySpec

class CheckSignatureCommand {
    fun isValid(
        content: ByteArray,
        signatureBase64: String,
        signatureAlgorithm: String,
        publicKeyBase64: String
    ): Boolean {

        val logger = LoggerFactory.getLogger(CheckSignatureCommand::class.java)

        if (!signatureAlgorithm.equals("SHA256withECDSA", ignoreCase = true ))
        {
            logger.info("Unsupported dcc signature algorithm.")
            return false;
        }
        var publicKey: PublicKey
        try{
            publicKey = CryptoKeyConverter.decodeAsn1DerPkcs1X509Base64ToPublicKey("EC", publicKeyBase64)
        }
        catch (ex: Exception)
        {
            logger.error("Could not parse wallet public key.")
            return false;
        }

        try {
            var sig = Base64.decode(signatureBase64);
            var signature = Signature.getInstance("SHA256withECDSA", "BC")
            signature.initVerify(publicKey)
            signature.update(content)
            return signature.verify(sig)
        }
        catch (ex: SignatureException)
        {
            logger.warn("SignatureException while validation signature: ${ex.message}")
            return false;
        }
        catch (ex: Exception)
        {
            logger.warn("Exception while validation signature: ${ex.message}")
            return false;
        }
    }
}
