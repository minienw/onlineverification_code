package nl.rijksoverheid.minienw.travelvalidation.validationservice.configuration

import com.google.gson.Gson
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.HttpRemoteBusinessRulesSource
import org.bouncycastle.crypto.agreement.jpake.JPAKERound1Payload
import org.bouncycastle.util.encoders.Base64
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class ConfigTest {

    @Test
    fun readConfig()
    {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder().uri(URI.create("https://verifier-api.coronacheck.nl/v4/dcbs/config")).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString()).body()!!
        var responseBody = Gson().fromJson(response, HttpRemoteBusinessRulesSource.ConfigResponse::class.java)
        //TODO sig check
        var json = String(Base64.decode(responseBody.payload), Charsets.UTF_8)
    }
}