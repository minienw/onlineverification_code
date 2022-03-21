package nl.rijksoverheid.minienw.travelvalidation.validationservice

import com.google.gson.Gson
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.PublicKeyJwk
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.CryptoKeyConverter
import org.bouncycastle.util.encoders.Base64
import org.junit.jupiter.api.Test

class TestAirlineSigningKeyPairGenerator
{
    @Test
    fun formatNewKeyPair_ES256() = format(SignatureAlgorithm.ES256)

    @Test
    fun formatNewKeyPair_RS256() = format(SignatureAlgorithm.RS256)

    @Test
    fun formatNewKeyPair_PS256() = format(SignatureAlgorithm.PS256)

    private fun format(alg: SignatureAlgorithm) {
        Keys.keyPairFor(alg)
        var kp = Keys.keyPairFor(alg)
        var pub = PublicKeyJwk(
            use = "sig",
            alg = alg.name,
            kid = CryptoKeyConverter.getKid(kp.public.encoded), //TODO 8 bytes feels a bit short?
            x5c = arrayOf(Base64.toBase64String(kp.public.encoded))
        )

        var pubJson = Gson().toJson(pub)
        println(pubJson)
        var privBase64 = Base64.toBase64String(kp.private.encoded)
        println(privBase64)
    }

}