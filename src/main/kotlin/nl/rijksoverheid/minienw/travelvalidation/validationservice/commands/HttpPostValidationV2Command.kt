package nl.rijksoverheid.minienw.travelvalidation.validationservice.commands

import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableType
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.ValidationAccessTokenPayload
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.validate.ConfirmationTokenPayload
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.validate.DccExtract
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.validate.ResultTokenPayload
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.validate.ValidateRequestBody
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.*
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.validation.ValidationCommand
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.validation.ValidationCommandResult
import org.bouncycastle.util.encoders.Base64
import org.bouncycastle.util.encoders.DecoderException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class HttpPostValidationV2Command(
    private val validationCommand: ValidationCommand,
    private val repo: ISessionRepository,
    private val dtp : IDateTimeProvider,
    private val dccDecryptCommand : DccDecryptCommand,
    private val responseTokenBuilder: ValidationResponseTokenBuilder,
    private val appSettings : IApplicationSettings,
    private val subjectIdGenerator: ValidationServicesSubjectIdGenerator,
)
{
    fun execute(validationAccessTokenPayload: ValidationAccessTokenPayload, body: ValidateRequestBody, subjectId: String): ResponseEntity<String>
    {
        var logger = LoggerFactory.getLogger(HttpPostValidationV2Command::class.java)

        var subjectIdValidationResult = subjectIdGenerator.validate(subjectId)
        if (subjectIdValidationResult.isNotEmpty())
        {
            logger.info("Incorrect subject id format - " + subjectIdValidationResult.joinToString("\n"))
            return ResponseEntity.badRequest().body("Incorrect subject id format - " + subjectIdValidationResult.joinToString("\n"))
        }

        if (validationAccessTokenPayload.subject != subjectId) {
            logger.info("validationAccessTokenPayload.subject and request subjectId did not match.")
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        var session = repo.find(subjectId) ?: return ResponseEntity(HttpStatus.GONE)
        if (dtp.snapshot().epochSecond > session.response.whenExpires)
        {
            logger.info("Session timed out.")
            repo.remove(subjectId)
            return ResponseEntity(HttpStatus.GONE)
        }

        val parseDccResponse = parseDcc(body, session.body.nonce, session.body.walletPublicKey!!)
        if (parseDccResponse.statusCode.isError) {
            logger.info("DCC was either not decrypted correctly or failed sig check.")
            return parseDccResponse
        }
        val args = ValidationCommandArgs(
            encodeDcc = parseDccResponse.body!!,
            trip = TripInfo(validationAccessTokenPayload.validationCondition.countryOfDeparture, validationAccessTokenPayload.validationCondition.countryOfArrival),
        )

        var verificationResult = validationCommand.execute(args)
        val resultJws = buildResult(verificationResult, validationAccessTokenPayload)
        return ResponseEntity(resultJws, HttpStatus.OK)
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
            var resultPayload = ResultTokenPayload(
                confirmation = null,
                whenIssued = whenIssued,
                whenExpires = whenExpires,
                serviceProviderUri = authorizationHeaderObject.issuingServiceProvider,
                category = authorizationHeaderObject.validationCondition.categories,
                subject = authorizationHeaderObject.subject,
                result = resultCode,

                //Business rules results
                results = verificationResult.businessRuleFailures.toTypedArray(),

                dccExtract = null
            )
            val resultJws = responseTokenBuilder.build(resultPayload)
            return resultJws
        }

        val personalInfoDccExtract = safeGetDccExtract(verificationResult)
        var resultTokenPayloadConfirmation = ConfirmationTokenPayload(
            //TODO Change these terrible names!
            id = authorizationHeaderObject.subject, //TODO Might be the original subject in contradiction to the EU spec!
            whenIssued = whenIssued,
            whenExpires = whenExpires,
            category = authorizationHeaderObject.validationCondition.categories, //TODO straight copy? Validation?
            result = resultCode,
            dccExtract = personalInfoDccExtract,
            portOfArrival = authorizationHeaderObject.validationCondition.portOfArrival,
            portOfDeparture = authorizationHeaderObject.validationCondition.portOfDeparture,
            subject = authorizationHeaderObject.subject,
            whenValidStart = authorizationHeaderObject.validationCondition.whenValidStart,
            whenValidEnd = authorizationHeaderObject.validationCondition.whenValidEnd,
            validationClock = authorizationHeaderObject.validationCondition.validationClock,
        )

        val confirmationJws = responseTokenBuilder.build(resultTokenPayloadConfirmation)

        var resultPayload = ResultTokenPayload(
            confirmation = confirmationJws,
            whenIssued = whenIssued,
            whenExpires = whenExpires,
            serviceProviderUri = authorizationHeaderObject.issuingServiceProvider,
            category = authorizationHeaderObject.validationCondition.categories,
            subject = authorizationHeaderObject.subject,

            result = resultCode,

            //Business rules results
            results = verificationResult.businessRuleFailures.toTypedArray(), //TODO Should always be empty here?

            //Added as convenience for the wallet
            dccExtract = personalInfoDccExtract
        )

        val resultJws = responseTokenBuilder.build(resultPayload)
        return resultJws
    }

    private fun safeGetDccExtract(verificationResult: ValidationCommandResult): DccExtract? {

        val dcc = verificationResult.dccWithTimes?.dcc ?: return null

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
    ): ResponseEntity<String> {

        if(!dccDecryptCommand.canExecute(body.encryptedScheme))
            return ResponseEntity("Encryption scheme is not supported.", HttpStatus.BAD_REQUEST)

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
            return ResponseEntity("Encrypted parameter format not valid.", HttpStatus.BAD_REQUEST)
        }

        var result = dccDecryptCommand.execute(body.encryptedScheme, cipherText!!, secretKey!!, iv!!)

          //e.g. SHA256withECDSA
          //Integrity check on decrypted DCC
          //val sigPrivateKey = SignatureCheckKeyProvider().find(body.encryptedDccSignatureAlgorithm);
          val checkSig = CheckSignatureCommand().isValid(
              result,
              body.encryptedDccSignature!!,
              body.encryptedDccSignatureAlgorithm!!,
              walletPublicKey //from wallet
          )

        //TODO key not saved correctly?
        //if (!checkSig)
        //    return ResponseEntity(HttpStatus.BAD_REQUEST)

        return ResponseEntity.ok(result.toString(Charsets.UTF_8))
    }
}

