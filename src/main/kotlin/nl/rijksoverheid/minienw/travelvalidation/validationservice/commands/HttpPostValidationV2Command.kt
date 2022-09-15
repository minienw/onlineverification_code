package nl.rijksoverheid.minienw.travelvalidation.validationservice.commands

import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableType
import nl.rijksoverheid.minienw.travelvalidation.api.data.*
import nl.rijksoverheid.minienw.travelvalidation.api.data.callback.*
import nl.rijksoverheid.minienw.travelvalidation.api.data.validate.*
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.*
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.validation.ValidationCommand
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.validation.ValidationCommandResult
import org.bouncycastle.util.encoders.Base64
import org.bouncycastle.util.encoders.DecoderException
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class HttpPostValidationV2Command(
    private val logger : Logger,
    private val validationCommand: ValidationCommand,
    private val repo: ISessionRepository,
    private val dtp : IDateTimeProvider,
    private val dccDecryptCommand : DccDecryptCommand,
    private val responseTokenBuilder: ValidationResponseTokenBuilder,
    private val appSettings : IApplicationSettings,
    private val subjectIdGenerator: ValidationServicesSubjectIdGenerator,
    private val checkSignatureCommand: CheckSignatureCommand
)
{
    fun execute(validationAccessTokenPayload: ValidationAccessTokenPayload, body: ValidateRequestBody, subjectId: String): ResponseEntity<String>
    {
        val subjectIdValidationResult = subjectIdGenerator.validate(subjectId)
        if (subjectIdValidationResult.isNotEmpty())
        {
            val message = subjectIdValidationResult.joinToString("\n")
            logger.info("SubjectId is not a valid GUID - \n$message")
            return ResponseEntity.badRequest().body(message)
        }

        if (validationAccessTokenPayload.sub != subjectId) {
            logger.info("validationAccessTokenPayload.subject and request subjectId did not match.")
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }

        val session = repo.find(subjectId) ?: return ResponseEntity(HttpStatus.GONE)
        if (dtp.snapshot().epochSecond > session.response.whenExpires)
        {
            logger.info("Session timed out.")
            repo.remove(subjectId)
            return ResponseEntity(HttpStatus.GONE)
        }

        val parseDccResponse = parseDcc(body, session.body.nonce, session.body.walletPublicKey!!)
        if (parseDccResponse.statusCode.isError) {
            val message = "DCC was either not present, not decrypted correctly or failed sig check - ${parseDccResponse.body}"
            logger.info(message)
            return ResponseEntity(message, HttpStatus.BAD_REQUEST)
        }

        val args = ValidationCommandArgs(
            encodeDcc = parseDccResponse.body!! as String,
            trip = TripInfo(validationAccessTokenPayload.vc.cod, validationAccessTokenPayload.vc.coa),
        )

        val verificationResult = validationCommand.execute(args)
        val resultJws = buildResult(verificationResult, validationAccessTokenPayload)
        return ResponseEntity(resultJws, HttpStatus.OK)
    }

    fun map(value: DCCFailableType) : nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType
    {
        return when (value) {
            DCCFailableType.MissingRequiredTest -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.MissingRequiredTest
            DCCFailableType.TestDateExpired -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.TestDateExpired
            DCCFailableType.TestMustBeNegative -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.TestMustBeNegative
            DCCFailableType.RedNotAllowed -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.RedNotAllowed
            DCCFailableType.NeedFullVaccination -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.NeedFullVaccination
            DCCFailableType.RecoveryNotValid -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.RecoveryNotValid
            DCCFailableType.RequireSecondTest -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.RequireSecondTest
            DCCFailableType.InvalidTestResult -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.InvalidTestResult
            DCCFailableType.InvalidTestType -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.InvalidTestType
            DCCFailableType.InvalidTargetDisease -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.InvalidTargetDisease
            DCCFailableType.InvalidVaccineHolder -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.InvalidVaccineHolder
            DCCFailableType.InvalidVaccineType -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.InvalidVaccineType
            DCCFailableType.InvalidVaccineProduct -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.InvalidVaccineProduct
            DCCFailableType.DateOfBirthOutOfRange -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.DateOfBirthOutOfRange
            DCCFailableType.InvalidCountryCode -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.InvalidCountryCode
            DCCFailableType.InvalidDateOfBirth -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.InvalidDateOfBirth
            DCCFailableType.InvalidVaccineDate -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.InvalidVaccineDate
            DCCFailableType.InvalidTestDate -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.InvalidTestDate
            DCCFailableType.InvalidRecoveryFirstTestDate -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.InvalidRecoveryFirstTestDate
            DCCFailableType.InvalidRecoveryFromDate -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.InvalidRecoveryFromDate
            DCCFailableType.InvalidRecoveryToDate -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.InvalidRecoveryToDate
            DCCFailableType.InvalidVaccine14Days -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.InvalidVaccine14Days
            DCCFailableType.UndecidableFrom -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.UndecidableFrom
            DCCFailableType.CustomFailure -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.CustomFailure
            DCCFailableType.VocRequireSecondAntigen -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.VocRequireSecondAntigen
            DCCFailableType.VocRequireSecondPCR -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.VocRequireSecondPCR
            DCCFailableType.VocRequirePCROrAntigen -> nl.rijksoverheid.minienw.travelvalidation.api.data.validate.DCCFailableType.VocRequirePCROrAntigen
            else -> { // Note the block
                throw Error("Could not map DCCFailableType.")
            }
        }
    }

    private fun buildResult(
        verificationResult: ValidationCommandResult,
        authorizationHeaderObject: ValidationAccessTokenPayload,
    ): String {


        //TODO I miss my per request injections :D
        val snapshot = dtp.snapshot()
        val whenIssued = snapshot.epochSecond
        val whenExpires = snapshot.epochSecond + appSettings.validationResultJwsLifetimeSeconds

        val resultCode = getResultCode(verificationResult)
        if (verificationResult.result != ValidationCommandResult.Result.Pass)
        {
            val resultPayload = ResultTokenPayload(
                confirmation = null,
                whenIssued = whenIssued,
                whenExpires = whenExpires,
                serviceProviderUri = authorizationHeaderObject.iss,
                category = authorizationHeaderObject.vc.category,
                subject = authorizationHeaderObject.sub,
                result = resultCode,

                //Business rules results
                results = verificationResult.businessRuleFailures.map {x -> DCCFailableItem(map(x.type), x.param1,x.param2,x.param3,x.customMessage)}.toTypedArray(),

                dccExtract = null
            )
            val resultJws = responseTokenBuilder.build(resultPayload)
            return resultJws
        }

        val personalInfoDccExtract = getDccExtract(verificationResult)
        val resultTokenPayloadConfirmation = ConfirmationTokenPayload(
            //TODO Change these terrible names!
            id = authorizationHeaderObject.iss, //TODO Might be the original subject in contradiction to the EU spec!
            whenIssued = whenIssued,
            whenExpires = whenExpires,
            category = authorizationHeaderObject.vc.category, //TODO straight copy? Validation?
            result = resultCode,
            dccExtract = personalInfoDccExtract,
            portOfArrival = authorizationHeaderObject.vc.poa,
            portOfDeparture = authorizationHeaderObject.vc.pod,
            subject = authorizationHeaderObject.sub,
            whenValidStart = authorizationHeaderObject.vc.validfrom,
            whenValidEnd = authorizationHeaderObject.vc.validTo,
            validationClock = authorizationHeaderObject.vc.validationClock,
        )

        val confirmationJws = responseTokenBuilder.build(resultTokenPayloadConfirmation)

        val resultPayload = ResultTokenPayload(
            confirmation = confirmationJws,
            whenIssued = whenIssued,
            whenExpires = whenExpires,
            serviceProviderUri = authorizationHeaderObject.iss,
            category = authorizationHeaderObject.vc.category,
            subject = authorizationHeaderObject.sub,

            result = resultCode,

            //Business rules results
            //TODO Should always be empty here?
            results = verificationResult.businessRuleFailures.map {x -> DCCFailableItem(map(x.type), x.param1,x.param2,x.param3,x.customMessage)}.toTypedArray(),

            //Added as convenience for the wallet
            dccExtract = personalInfoDccExtract
        )

        val resultJws = responseTokenBuilder.build(resultPayload)
        return resultJws
    }

    private fun getDccExtract(verificationResult: ValidationCommandResult): DccExtract {

        val dcc = verificationResult.dccWithTimes!!.dcc

        return DccExtract(
            dcc.name.familyNameTransliterated,
            dcc.name.givenNameTransliterated,
            dcc.dateOfBirth
        )
    }

    //TODO Android app maps DCCFailableType.UndecidableFrom to Undecided
    //Also, there is no direct mapping from OK/NOK/FAIL to DCCFailableItem
    // map DCCFailableType to ValidationStatusResponse.Result, especially TechnicalVerification
    // Also
    // DCCQR.processBusinessRules forces an CHK by:
    //    if (from.isIndecisive() && to.getPassType() == CountryRiskPass.NLRules || to.isIndecisive()) {
    //        return listOf(DCCFailableItem(DCCFailableType.UndecidableFrom))
    //    }

    private fun getResultCode(verificationResult: ValidationCommandResult) : String {
        if (verificationResult.result == ValidationCommandResult.Result.DccVerificationFailed)
            return "NOK"

        if (verificationResult.result == ValidationCommandResult.Result.BusinessRuleVerificationFailed) {
            //verificationResult.businessRuleFailures.isNotEmpty() &&
            if (verificationResult.businessRuleFailures.all { t -> t.type == DCCFailableType.UndecidableFrom })
                return "CHK"

            return "NOK"
        }

        return "OK"
    }

    private fun parseDcc(
        body: ValidateRequestBody,
        nonce: String?,
        walletPublicKey: String
    ): ResponseEntity<Any> {

        if(!dccDecryptCommand.canExecute(body.encryptedScheme)) {
            logger.info("Encrypted parameter format not valid.");
            return ResponseEntity("Encryption scheme is not supported.", HttpStatus.BAD_REQUEST)
        }
        
        val cipherText: ByteArray?
        val secretKey: ByteArray?
        val iv: ByteArray?
        try {
            cipherText = Base64.decode(body.encryptedDcc)
            secretKey = Base64.decode(body.encryptionKey)
            iv = Base64.decode(nonce!!)
        }
        catch (_: DecoderException)
        {
            logger.info("Encrypted parameter format not valid.");
            return ResponseEntity("Encrypted parameter format not valid.", HttpStatus.BAD_REQUEST)
        }

        val result: ByteArray
        try {
            result = dccDecryptCommand.execute(body.encryptedScheme, cipherText!!, secretKey!!, iv!!)
        }
        catch(ex: Exception) //TODO more specific!
        {
            logger.info("Unexpected error decrypting DCC: $ex");
            return ResponseEntity("Error decrypting DCC.", HttpStatus.BAD_REQUEST)
        }

        if (!checkSignatureCommand.isValid(result, body.encryptedDccSignature!!, body.encryptedDccSignatureAlgorithm!!, walletPublicKey))
        {
            logger.info("Dcc Signature not valid.");
            return ResponseEntity("Error decrypting DCC.", HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity.ok(Base64.toBase64String(result))
    }
}

