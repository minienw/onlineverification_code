package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.dccverification

import com.google.gson.Gson
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IDccVerificationService
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Component

@Component
class DccVerificationHttpProxy(
    val appSettings: IApplicationSettings
) : IDccVerificationService
{
    override fun verify(encodedDcc: String): VerificationResponse
    {
        val postBody = "{\"credential\" : \"$encodedDcc\"}"

        val responseString = RestTemplateBuilder().build()
            .postForObject(appSettings.dccVerificationServiceUri , postBody, String::class.java) //VerificationResponse::class.java

        return Gson().fromJson(responseString,VerificationResponse::class.java)
    }
}
