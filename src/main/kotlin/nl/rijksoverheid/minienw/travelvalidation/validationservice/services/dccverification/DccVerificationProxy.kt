package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.dccverification

import nl.rijksoverheid.dcbs.verifier.models.DCC

data class VerificationResponse
(
    val validSignature: Boolean,
    val verificationError: String,
    val healthCertificate: HealthCertificate
)

data class HealthCertificate (
    val credentialVersion:Int,
    val issuer           : String,
    val issuedAt         : Long,
    val expirationTime   : Long,
    val dcc              : DCC
)
