package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.dccverification

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

    //TODO inject instead
    override fun verify(encodedDcc: String): VerificationResponse
    {
        if (appSettings.demoModeOn && appSettings.demoModePassAllDccs) {
            var dcc = DccDecoder().parse(encodedDcc)
            return VerificationResponse(
                validSignature = true,
                verificationError = "",
                healthCertificate = HealthCertificate(
                        credentialVersion = 123,
                        issuer = "issuer...",
                        issuedAt = Instant.now().epochSecond - 1000,
                        expirationTime = Instant.now().epochSecond + 3600,
                        dcc = dcc.dcc
                )
            )
        }

        try
        {
            var postBody = "{\"credential\" : \"$encodedDcc\"}"
            return RestTemplateBuilder().build()
                .postForObject(appSettings.dccVerificationServiceUri , postBody, VerificationResponse::class.java)
                ?: throw DccVerificationException("Null return from POST.")
        }
        catch(e: RestClientResponseException)
        {
            throw DccVerificationException("Wrapped exception from POST.", e)
        }
    }
}
