package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.dccverification.VerificationResponse

interface IDccVerificationService {
    fun verify(encodedDcc: String) : VerificationResponse
}