package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.SigningKeyResolverAdapter
import nl.rijksoverheid.minienw.travelvalidation.api.data.*
import org.slf4j.Logger
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
class AirlineSigningKeyResolverAdapter(private val logger : Logger, private val airlineSigningKeyProvider: IAirlineSigningKeyProvider) :
    SigningKeyResolverAdapter() {

    init {
        airlineSigningKeyProvider.refresh()
    }

    override fun resolveSigningKey(jwsHeader: JwsHeader<*>, claims: Claims?): PublicKey?
    {
        val found = airlineSigningKeyProvider.get(jwsHeader.keyId, jwsHeader.algorithm)

        if (found == null) {
            logger.info("Could not find public signing key ${jwsHeader.keyId} for ${jwsHeader.algorithm}.")
            return null
        }

        logger.info("Found public signing key ${jwsHeader.keyId} for ${jwsHeader.algorithm}.")
        return CryptoKeyConverter.decodeSigningJwkX5c(found)
    }
}

