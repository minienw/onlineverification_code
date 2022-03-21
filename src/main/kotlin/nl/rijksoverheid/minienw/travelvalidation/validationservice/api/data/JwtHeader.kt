package nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data

data class JwtHeader(
    val kid: String,
    val alg: String,
)