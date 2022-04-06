package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import com.google.gson.Gson
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.PublicKeyJwk
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.identity.IdentityResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


//TODO obtain keys by GET/identity from each airline endpoint.
// If the sig fails to verify, refresh the keys of the airline the key came from

@Qualifier("local")
//@Component
class LocalFileAirlineSigningKeyProvider : IAirlineSigningKeyProvider {
    private val items: AirlineKeys

    constructor(appSettings: IApplicationSettings) {
        LoggerFactory.getLogger("argle").info("-----------> " + File(".").absolutePath)
        val configContents = File(appSettings.configFileFolderPath, "airlinePublicKeys.json").readText(Charsets.UTF_8)
        items = Gson().fromJson(configContents, AirlineKeys::class.java)
    }

    override fun get(keyId: String?, algorithm: String?): PublicKeyJwk? {
        return items.keys.find { it.key.kid == keyId && it.key.alg == algorithm }?.key
    }
}

@Qualifier("remote")
@Component
class HttpRemoteAirlineSigningKeyProvider
 : IAirlineSigningKeyProvider {

    private var items: ArrayList<PublicKeyJwk> = ArrayList<PublicKeyJwk>()
    private val logger: Logger = LoggerFactory.getLogger("HttpRemoteAirlineSigningKeyProvider")
    private val appSettings: IApplicationSettings

    constructor(appSettings: IApplicationSettings)
    {
        this.appSettings = appSettings
        refresh()
    }


    fun refresh()
    {
        val result = ArrayList<PublicKeyJwk>()
        for (i in appSettings.airlineIdentityUris) {
            val request = HttpRequest.newBuilder().uri(URI.create(i)).build()
            val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() != 200) {
                logger.warn("Failed to update airline public keys from: $i")
                continue
            }

            try {
                val doc = Gson().fromJson(response.body(), IdentityResponse::class.java)
                for (vm in doc.verificationMethod) {
                    if (vm.publicKeyJwk?.use.equals("sig", ignoreCase = true)) {
                        result.add(vm.publicKeyJwk!!)
                    }
                }
            } catch (ex: Exception) {
                logger.warn("Failed to parse identity from : $i")
                continue
            }
        }

        synchronized(this)
        {
            items = result;
        }
    }

    override fun get(keyId: String?, algorithm: String?): PublicKeyJwk? {
        return items.find { it.kid == keyId && it.alg == algorithm }
    }
}

