package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import nl.rijksoverheid.minienw.travelvalidation.api.data.initialize.*

//TODO review contents - remove unnecessary
data class SessionInfo(
    val body: ValidationInitializeRequestBody,
    val response: ValidationInitializeResponse
)
