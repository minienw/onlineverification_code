package nl.rijksoverheid.minienw.travelvalidation.validationservice.configuration

import com.google.gson.Gson
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.HttpRemoteAirlineSigningKeyProvider
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.HttpRemoteBusinessRulesSource
import org.bouncycastle.util.encoders.Base64
import org.hibernate.validator.internal.util.Contracts.assertNotEmpty
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class ConfigTest {

    @Test
    fun readConfig() {
        val client = HttpClient.newBuilder().build()
        val request =
            HttpRequest.newBuilder().uri(URI.create("https://verifier-api.coronacheck.nl/v4/dcbs/config")).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString()).body()!!
        val responseBody = Gson().fromJson(response, HttpRemoteBusinessRulesSource.ConfigResponse::class.java)
        //TODO sig check
        val json = String(Base64.decode(responseBody.payload), Charsets.UTF_8)
        assertNotEmpty(json, "Not empty")
    }

    //TODO has to have the airline stub running or change to another available endpoint
    @Test
    fun readAirlineKeys() {
        val appSettings = Mockito.mock(IApplicationSettings::class.java)
        Mockito.`when`(appSettings.airlineIdentityUris).thenReturn(arrayOf("http://localhost:8081/identity"))
        val provider = HttpRemoteAirlineSigningKeyProvider(appSettings)
        //PublicKeyJwk(x5c=[MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAp4aod1QhVQIZ5cVW05qZUIOALxaFn+sVblneSxPKuvuaW3V/OTcm8sby8AyGYuhLsIkb65/afrbn5hC1E3RMcDJu9MJByvS0PReckuVgxJZE9FzAFJLpld5OScCSIwEabCyXOEi/324IfGnzNZYxBUa3Rb69ep1J3H7Ddp1VZXeaKPcAaKQ/nvCFle0TERRuXOysAt/m1i2hOSBKNE7KtyCRwFIn1dG7HcrpHOYzONTeQIEEw1kPjRszDU6EG7F7alMibar781ZNuHD1V/jRDDPILi9u5VHV1N42MDX+60SjOlBjY+7ncet+Yem0gGvnvcQI1f+BUyN0jVUppFbX7X59+pmQSonpfWnCiDWU32qsaMdue+GCi331vk79kKOSH9QlBWPXVL1GTFnoQjb/oTiMeKy1qsdeGN/x8GwQMCJG0xt2ncqguUdk6y9uaNxZboWpB9w+heZtc8YfqdiQJuM5HAM355kCgl0cUci+9hCQJeW/YcEAGSQPUW2lZbomRtoc41AcK6k0dIzD20Ej3rDxWxvqwOBz1TDsuGnGVcYXYQcnHbFJWfRK27l+yypg0gsIVWpND5y7W+Age6kj/GMDCM2LFGra30OjatF+SEt6Mtk0Wh0Gx1JIPPEc+9PYDQ5HsfNQO00HuGS7rHtMQUbWMqpRZGJgYCaQuSXLD7cCAwEAAQ==], kid=L6TcjnbZge4=, alg=RS256, use=sig)
        provider.refresh()
        assert(provider.get("L6TcjnbZge4=","RS256") != null)
    }
}