package nl.rijksoverheid.minienw.travelvalidation.validationservice

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.CryptoKeyConverter
import nl.rijksoverheid.minienw.travelvalidation.validationservice.servicetests.CryptoKeyParserTests
import org.junit.jupiter.api.Test

class JwtTests {
    @Test
    fun roundTrip()
    {
        val kwt = io.jsonwebtoken.Jwts.builder()
            .signWith(CryptoKeyConverter.decodeAsn1DerPkcs8Base64ToPrivateKey("RSA", CryptoKeyParserTests.privateKeyPem))
            .claim("poland", "springtime")
            .compact()

        val verifed = io.jsonwebtoken.Jwts.parserBuilder().setSigningKey(
            CryptoKeyConverter.decodeAsn1DerPkcs1X509Base64ToPublicKey("RSA", CryptoKeyParserTests.publicKeyPem)
        ).build().parseClaimsJws(kwt)

        assert(verifed.body.get("poland") == "springtime")
    }
}