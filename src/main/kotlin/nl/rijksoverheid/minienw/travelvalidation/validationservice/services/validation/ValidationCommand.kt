package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.validation

import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableType
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.BusinessRulesCommandArgs
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.ValidationCommandArgs
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.DccDecoder
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IDccVerificationService
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.BusinessRulesService
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.IPublicKeysProvider
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.dccverification.VerificationResponse
import org.apache.tomcat.util.codec.binary.Base64
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Component

data class DccParseResponse
(
    val dcc : String
)

data class DccParseArgs
(
    val Buffer : String
)

/*
* DCC string, image or PDF
* */
@Component
class ParseDccArtifactContentCommand(
    val appSettings: IApplicationSettings
)
{
    fun parse(base64EncodedContent: String): String?
    {
        val result = parsePossibleString(base64EncodedContent)
        if (result != null)
            return result

        val postBody = DccParseArgs(base64EncodedContent)

        val response = RestTemplateBuilder().build()
            .postForObject(appSettings.dccArtifactParsingServiceUri, postBody, DccParseResponse::class.java)

        //return response

        return response?.dcc
    }

    fun parsePossibleString(base64EncodedContent: String): String?
    {
        try{
            val possible = String(Base64.decodeBase64(base64EncodedContent), Charsets.UTF_8)
            if (possible.startsWith("HC1:") /*&& TODO And all characters are Base45 compliant*/)
                return possible
            else
                return null
        }
        catch(ex: Exception)
        {
            return null
        }
    }
}

@Component
class ValidationCommand(
    private val dccVerificationService: IDccVerificationService,
    private val businessRulesCommand: BusinessRulesService,
    private val publicKeysProvider: IPublicKeysProvider,
    private val parseDccArtifactContentCommand : ParseDccArtifactContentCommand
)
{
   fun execute(args: ValidationCommandArgs) : ValidationCommandResult
    {
        val logger = LoggerFactory.getLogger(ValidationCommand::class.java)

        val dcc = parseDccArtifactContentCommand.parse(args.encodeDcc)

        if (dcc == null) {
            //TODO log could not find a DCC in the content
            return ValidationCommandResult(
                result = ValidationCommandResult.Result.DccVerificationFailed,
                 listOf(DCCFailableItem(DCCFailableType.CustomFailure, customMessage = "Could not obtain DCC from file provided.")),
                _dccWithTimes = null
            )
        }

        var dccVerificationResult: VerificationResponse
        try {
            dccVerificationResult = dccVerificationService.verify(dcc)
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
            logger.info("Dcc with invalid signature - retrying")
            publicKeysProvider.refresh()
            try {
                dccVerificationResult = dccVerificationService.verify(dcc)
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
        val dccWithTimes = DccDecoder().parse(dcc)

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