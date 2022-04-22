package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules

import com.google.gson.Gson
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import org.bouncycastle.util.encoders.Base64
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class HttpRemoteBusinessRulesSource (
    private val appConfig: IApplicationSettings,
) : IStringReader
{
    private val logger: Logger = LoggerFactory.getLogger("HttpRemoteBusinessRulesSource")

    override fun read(name: String): String {
        val uri = getUri(name)
        logger.debug("Updating business rules - $name from ${uri}.")
        val request = HttpRequest.newBuilder().uri(URI.create(uri)).build()
        val response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString()).body()!!
        val responseBody = Gson().fromJson(response, ConfigResponse::class.java)
        //TODO CMS sig check - which cert(s)? appConfig.configSignaturePublicKey - how many?
        val result = String(Base64.decode(responseBody.payload), Charsets.UTF_8)
        logger.info("Updated business rules - $name from ${uri}.")
        return result
    }

    private fun getUri(name: String): String {
        when(name) {
            "config" -> return appConfig.configUri
            "business_rules" -> return appConfig.businessRulesUri
            "value_sets" -> return appConfig.valueSetsUri
            "custom_business_rules" -> return appConfig.customBusinessRulesUri
            "public_keys" -> return appConfig.publicKeysUri
        }
        throw Error("Value not recognised.")
    }

    class ConfigResponse (
        val signature: String,
        val payload: String
    )
}