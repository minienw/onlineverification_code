package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.SigningKeyResolverAdapter
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.PublicKeyJwk
import org.springframework.stereotype.Service
import java.security.PublicKey

interface IAirlineSigningKeyProvider {
    fun get(keyId: String?, algorithm: String?): PublicKeyJwk?
}

data class AirlineKey(
    val description: String, //Which airline, etc
    val key: PublicKeyJwk
)

data class AirlineKeys(
    val keys: Array<AirlineKey>
)

@Service
class AirlineSigningKeyResolverAdapter: SigningKeyResolverAdapter {

    private val _airlineSigningKeyProvider :IAirlineSigningKeyProvider

    constructor(airlineSigningKeyProvider: IAirlineSigningKeyProvider): super() {
        _airlineSigningKeyProvider = airlineSigningKeyProvider
    }

    override fun resolveSigningKey(jwsHeader: JwsHeader<*>, claims: Claims?): PublicKey?
    {
        val found = _airlineSigningKeyProvider.get(jwsHeader.keyId, jwsHeader.algorithm)
            ?: return null

        return CryptoKeyConverter.decodeSigningJwkX5c(found)
    }
}

