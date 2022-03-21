package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.validation

import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.dccverification.VerificationResponse
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.DccQrCode

class ValidationCommandResult(
    //private var explicitUndecided: Boolean,
    val result: Result,
    val businessRuleFailures: List<DCCFailableItem>,
    _dccVerificationResult: VerificationResponse?,
    _dccWithTimes: DccQrCode?
)
{
    enum class Result {
        Pass, DccVerificationFailed, BusinessRuleVerificationFailed
    }

    val dccVerificationResult = _dccVerificationResult ?: throw Exception()
    val dccWithTimes = _dccWithTimes ?: throw Exception()

    companion object
    {
        //fun createPass() = NlValidationCommandResult(Result.Pass, listOf())
        fun createDccVerificationFailed() = ValidationCommandResult(
            Result.DccVerificationFailed,
            listOf(),
            _dccVerificationResult= null,
            _dccWithTimes = null
        )

        fun createFromBusinessRuleVerificationResult(
            items: List<DCCFailableItem>,
            dccVerificationResult: VerificationResponse,
            dccWithTimes: DccQrCode
        ) = ValidationCommandResult(if (items.isEmpty()) Result.Pass else Result.BusinessRuleVerificationFailed , items, dccVerificationResult, dccWithTimes)

        fun createExplicitUndecidedBusinessRuleVerificationFailed() = ValidationCommandResult(
            Result.BusinessRuleVerificationFailed,
            listOf(),
            _dccVerificationResult= null,
            _dccWithTimes = null
        )
    }

    val isExplicitBusinessRuleVerificationFailure: Boolean get() = result == Result.BusinessRuleVerificationFailed && businessRuleFailures.isEmpty()
}