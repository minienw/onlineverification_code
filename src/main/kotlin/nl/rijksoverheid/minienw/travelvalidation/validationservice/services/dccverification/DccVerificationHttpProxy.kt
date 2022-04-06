package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.dccverification

import com.google.gson.Gson
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.DccDecoder
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IDccVerificationService
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientResponseException
import java.time.Instant

@Component
class DccVerificationHttpProxy(
    val appSettings: IApplicationSettings
) : IDccVerificationService
{
    override fun verify(encodedDcc: String): VerificationResponse
    {
        try
        {
            var postBody = "{\"credential\" : \"$encodedDcc\"}"

            var responseString = RestTemplateBuilder().build()
                .postForObject(appSettings.dccVerificationServiceUri , postBody, String::class.java) //VerificationResponse::class.java
                ?: throw DccVerificationException("Null return from POST.")

            return Gson().fromJson(responseString,VerificationResponse::class.java)
        }
        catch(e: RestClientResponseException)
        {
            throw DccVerificationException("Wrapped exception from POST.", e)
        }
    }
}
