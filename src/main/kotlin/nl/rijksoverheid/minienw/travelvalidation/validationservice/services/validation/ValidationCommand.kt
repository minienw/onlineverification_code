package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.validation

import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.BusinessRulesCommandArgs
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.HttpPostValidationV2Command
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.ValidationCommandArgs
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.DccDecoder
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IDccVerificationService
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.BusinessRulesService
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.IPublicKeysProvider
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.dccverification.VerificationResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ValidationCommand(
    private val dccVerificationService: IDccVerificationService,
    private val businessRulesCommand: BusinessRulesService,
    private val publicKeysProvider: IPublicKeysProvider,
)
{
   fun execute(args: ValidationCommandArgs) : ValidationCommandResult
    {
        var logger = LoggerFactory.getLogger(ValidationCommand::class.java)
        var dccVerificationResult: VerificationResponse
        try {
            dccVerificationResult = dccVerificationService.verify(args.encodeDcc)
        }
        catch(ex: Exception)
        {
            logger.error("Verification service error: ${ex.message}, ${ex.stackTraceToString()}")
            throw ex
        }

        if (!dccVerificationResult.validSignature) {
            //Refresh the dcc signing public keys and try again.
            logger.warn("Dcc with invalid signature - retrying")
            publicKeysProvider.refresh()
            try {
                dccVerificationResult = dccVerificationService.verify(args.encodeDcc)
            }
            catch(ex: Exception)
            {
                logger.error("Verification service error: ${ex.message}, ${ex.stackTraceToString()}")
                throw ex
            }
        }

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