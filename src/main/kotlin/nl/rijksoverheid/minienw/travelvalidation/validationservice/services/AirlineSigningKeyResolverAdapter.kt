package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.SigningKeyResolverAdapter
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.PublicKeyJwk
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.security.PublicKey

interface IAirlineSigningKeyProvider {
    fun get(keyId: String?, algorithm: String?): PublicKeyJwk?
    fun refresh()
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
    private val logger : Logger = LoggerFactory.getLogger(AirlineSigningKeyResolverAdapter::class.java)

    constructor(airlineSigningKeyProvider: IAirlineSigningKeyProvider): super() {
        _airlineSigningKeyProvider = airlineSigningKeyProvider
        _airlineSigningKeyProvider.refresh()
    }

    override fun resolveSigningKey(jwsHeader: JwsHeader<*>, claims: Claims?): PublicKey?
    {
        val found = _airlineSigningKeyProvider.get(jwsHeader.keyId, jwsHeader.algorithm)
            ?: return null

        if (found == null)
            logger.warn("Could not find public signing key ${jwsHeader.keyId} for ${jwsHeader.algorithm}.")
        else
            logger.info("Found public signing key ${jwsHeader.keyId} for ${jwsHeader.algorithm}.")

        return CryptoKeyConverter.decodeSigningJwkX5c(found)
    }
}

