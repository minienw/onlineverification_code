package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import nl.rijksoverheid.dcbs.verifier.models.DCC

data class DccQrCode
(
    val dcc: DCC,
    val issuedAt: Long,
    val expirationTime: Long
)