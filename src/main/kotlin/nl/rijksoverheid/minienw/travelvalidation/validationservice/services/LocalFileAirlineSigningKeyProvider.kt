package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import com.google.gson.Gson
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.PublicKeyJwk
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.identity.IdentityResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.ConnectException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse



//@Qualifier("remote")
@Component
class HttpRemoteAirlineSigningKeyProvider(private val appSettings: IApplicationSettings)
 : IAirlineSigningKeyProvider {

    private var items: ArrayList<PublicKeyJwk> = ArrayList<PublicKeyJwk>()
    private val logger: Logger = LoggerFactory.getLogger("HttpRemoteAirlineSigningKeyProvider")

    override fun refresh()
    {
        val result = ArrayList<PublicKeyJwk>()
        for (i in appSettings.airlineIdentityUris) {
            logger.debug("Getting verification keys from $i")
            var responseBody:String
            try {
                val request = HttpRequest.newBuilder().uri(URI.create(i)).build()
                val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())
                responseBody = response.body()
                if (response.statusCode() != 200) {
                    logger.warn("Failed to GET from: $i with ${response.statusCode()} ${response.body()}")
                    continue
                }
            }
            catch (ex: ConnectException) {
                logger.warn("Failed GET from : $i with message : ${ex.message} and ${ex.stackTraceToString()}")
                continue
            }

            try {
                val doc = Gson().fromJson(responseBody, IdentityResponse::class.java)
                for (vm in doc.verificationMethod) {
                    if (vm.publicKeyJwk?.use.equals("sig", ignoreCase = true)) {
                        logger.info("Found verification key '${vm.publicKeyJwk?.kid}' in $i.")
                        result.add(vm.publicKeyJwk!!)
                    }
                }
            } catch (ex: Exception) {
                logger.warn("Failed to parse identity from : $i")
                continue
            }
        }
        if (result.size == 0)
            logger.error("Found ZERO public keys in airline identities!")
        else
            logger.info("Found ${result.size} public keys in airline identities.")

        synchronized(this)
        {
            items = result
        }
    }

    override fun get(keyId: String?, algorithm: String?): PublicKeyJwk? {
        return items.find { it.kid == keyId && it.alg == algorithm }
    }
}

