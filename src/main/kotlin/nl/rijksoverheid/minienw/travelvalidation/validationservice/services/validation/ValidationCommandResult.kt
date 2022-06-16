package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.validation

import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.dccverification.VerificationResponse
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.DccQrCode

class ValidationCommandResult(
    //private val explicitUndecided: Boolean,
    val result: Result,
    val businessRuleFailures: List<DCCFailableItem>,
    _dccWithTimes: DccQrCode?
)
{
    enum class Result {
        Pass, DccVerificationFailed, BusinessRuleVerificationFailed
    }

    val dccWithTimes = _dccWithTimes

    companion object
    {
        //fun createPass() = NlValidationCommandResult(Result.Pass, listOf())
        fun createDccVerificationFailed() = ValidationCommandResult(
            Result.DccVerificationFailed,
            listOf(),
            _dccWithTimes = null
        )

        fun createFromBusinessRuleVerificationResult(
            items: List<DCCFailableItem>,
            dccVerificationResult: VerificationResponse, //TODO confirm this text is never used in the response - currently a failed sig has to be inferred.
            dccWithTimes: DccQrCode
        ) = ValidationCommandResult(if (items.isEmpty()) Result.Pass else Result.BusinessRuleVerificationFailed , items, dccWithTimes)

        fun createExplicitUndecidedBusinessRuleVerificationFailed() = ValidationCommandResult(
            Result.BusinessRuleVerificationFailed,
            listOf(),
            _dccWithTimes = null
        )
    }
}