package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.initialize.ValidationInitializeRequestBody
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.initialize.ValidationInitializeResponse


//TODO review contents - remove unnecessary
data class SessionInfo(
    val body: ValidationInitializeRequestBody,
    val response: ValidationInitializeResponse
)
