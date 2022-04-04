package nl.rijksoverheid.minienw.travelvalidation.validationservice.commands

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.CryptoKeyConverter
import org.bouncycastle.util.encoders.Base64
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
        if (!signatureAlgorithm.equals("SHA256withECDSA", ignoreCase = true ))
        {
            //TODO log
            return false;
        }
        var publicKey: PublicKey
        try{
            publicKey = CryptoKeyConverter.decodeAsn1DerPkcs1X509Base64ToPublicKey("EC", publicKeyBase64)
        }
        catch (ex: Exception)
        {
            //TODO log
            return false;
        }

        try {
            var sig = Base64.decode(signatureBase64);
            var signature = Signature.getInstance("SHA256withECDSA", "BC")
            signature.initVerify(publicKey)
            signature.update(content)
            return signature.verify(sig)
        }
        catch (ex: SignatureException) //TODO narrow this down
        {
            //TODO log
            return false;
        }
        catch (ex: Exception) //TODO narrow this down
        {
            //TODO log
            return false;
        }
    }
}
