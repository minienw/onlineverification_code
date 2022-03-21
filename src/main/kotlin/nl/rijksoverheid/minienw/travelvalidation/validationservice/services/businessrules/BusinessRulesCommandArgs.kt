package nl.rijksoverheid.minienw.travelvalidation.validationservice.commands

import nl.rijksoverheid.dcbs.verifier.models.DCC

data class BusinessRulesCommandArgs (

    val trip: TripInfo,
    val issuedAt: Long, // When was this QR issued at in seconds
    val expirationTime: Long, // When does this QR expire in seconds
    val dcc: DCC //When do we get this?
)

data class TripInfo(
    val countryFrom: String,
    val countryTo: String,
)

data class ValidationCommandArgs (
    val encodeDcc: String,
    val trip: TripInfo,
)

