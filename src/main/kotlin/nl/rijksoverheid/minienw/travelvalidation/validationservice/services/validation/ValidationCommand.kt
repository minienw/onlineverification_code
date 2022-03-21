package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.validation

import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.BusinessRulesCommandArgs
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.ValidationCommandArgs
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.DccDecoder
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IDccVerificationService
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.BusinessRulesService
import org.springframework.stereotype.Component

@Component
class ValidationCommand(
    private val dccVerificationService: IDccVerificationService,
    private val businessRulesCommand: BusinessRulesService
)
{
   fun execute(args: ValidationCommandArgs) : ValidationCommandResult
    {
        val dccVerificationResult = dccVerificationService.verify(args.encodeDcc)

        if (!dccVerificationResult.validSignature)
            return ValidationCommandResult.createDccVerificationFailed()

        //TODO use DCC returned by verification server
        val dccWithTimes = DccDecoder().parse(args.encodeDcc)

        var businessRulesCommandTripArgs = BusinessRulesCommandArgs (
            trip = args.trip,
            issuedAt = dccWithTimes.issuedAt,
            expirationTime = dccWithTimes.expirationTime,
            dcc= dccWithTimes.dcc
            )

        if (!businessRulesCommand.canExecute(businessRulesCommandTripArgs))
            return ValidationCommandResult.createExplicitUndecidedBusinessRuleVerificationFailed()

        var fails = businessRulesCommand.execute()

        return ValidationCommandResult.createFromBusinessRuleVerificationResult(
            fails,
            dccVerificationResult,
            dccWithTimes
        )
    }
}