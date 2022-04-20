package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.validation

import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.BusinessRulesCommandArgs
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
        val logger = LoggerFactory.getLogger(ValidationCommand::class.java)
        var dccVerificationResult: VerificationResponse
        try {
            dccVerificationResult = dccVerificationService.verify(args.encodeDcc)
            if (dccVerificationResult.validSignature)
                logger.info("DCC Verification passed (error: ${dccVerificationResult.verificationError}).")
            else
                logger.info("DCC Verification failed with error: ${dccVerificationResult.verificationError}.")
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
                if (dccVerificationResult.validSignature)
                    logger.info("DCC Verification (attempt 2) passed (Message: ${dccVerificationResult.verificationError}).")
                else {
                    logger.info("DCC Verification (attempt 2) failed with error: ${dccVerificationResult.verificationError}.")
                    return ValidationCommandResult.createDccVerificationFailed()
                }
            }
            catch(ex: Exception)
            {
                logger.error("Verification service error (attempt 2): ${ex.message}, ${ex.stackTraceToString()}")
                throw ex
            }
        }

        //TODO use DCC returned by verification server
        val dccWithTimes = DccDecoder().parse(args.encodeDcc)

        val businessRulesCommandTripArgs = BusinessRulesCommandArgs (
            trip = args.trip,
            issuedAt = dccWithTimes.issuedAt,
            expirationTime = dccWithTimes.expirationTime,
            dcc= dccWithTimes.dcc
            )

        logger.info("Verifying Trip - From:${args.trip.countryFrom} To:${args.trip.countryTo}")
        if (!businessRulesCommand.canExecute(businessRulesCommandTripArgs))
        {
            logger.warn("Could not execute verification - From:${args.trip.countryFrom} To:${args.trip.countryTo}")
            return ValidationCommandResult.createExplicitUndecidedBusinessRuleVerificationFailed()
        }

        val fails = businessRulesCommand.execute()

        logger.info("Verification complete - Failed business rule count: ${fails.size}")
        return ValidationCommandResult.createFromBusinessRuleVerificationResult(
            fails,
            dccVerificationResult,
            dccWithTimes
        )
    }
}