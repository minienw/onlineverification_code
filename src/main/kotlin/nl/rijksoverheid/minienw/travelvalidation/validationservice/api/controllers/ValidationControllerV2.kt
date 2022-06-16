package nl.rijksoverheid.minienw.travelvalidation.validationservice.api.controllers

import nl.rijksoverheid.minienw.travelvalidation.api.Headers
import nl.rijksoverheid.minienw.travelvalidation.api.data.initialize.ValidationInitializeRequestBody
import nl.rijksoverheid.minienw.travelvalidation.api.data.validate.ValidateRequestBody
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.HttpPostValidationInitialiseV2Command
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.*
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.ValidationAccessTokenParser
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*


@Component
@RestController
class ValidationControllerV2(
    private val logger : Logger,
    private val travellerTripJwsParser : ValidationAccessTokenParser,
    private val httpPostValidationInitialiseCommand : HttpPostValidationInitialiseV2Command,
    private val httpPostValidationCommand : HttpPostValidationV2Command,
) {
    @PostMapping(
        "initialize/{subjectId}",
        headers = [Headers.AcceptJson, Headers.MustBeVersion2],
        produces = [Headers.Json]
    )
    fun initialize(
        @RequestBody body: ValidationInitializeRequestBody,
        @RequestHeader(Headers.Authorization) authorizationHeader: String,
        @PathVariable("subjectId") subjectId: String
//        @RequestHeader(Headers.EncryptValidationInitResponse) encryptResponse: Boolean, //TODO are these being honoured?
//        @RequestHeader(Headers.SignValidationInitResponse) signResponse: Boolean, //TODO are these being honoured?
    ): ResponseEntity<Any>
    {
        logger.info("POST initialize, subject Id '$subjectId'")
        val parsed = travellerTripJwsParser.parse(authorizationHeader)
        if(parsed.statusCode != HttpStatus.OK) {
            logger.info("POST initialize, subject Id - '$subjectId' failed validation - ${parsed.statusCode} because '${parsed.body}'}")
            return ResponseEntity(parsed.statusCode)
        }
        return httpPostValidationInitialiseCommand.execute(parsed.body!!, body, subjectId)
    }

    @PostMapping("validate/{subjectId}",
        headers = [Headers.AcceptJson, Headers.MustBeVersion2],
        produces = [Headers.Json]
    )
    @ResponseStatus(HttpStatus.OK)
    fun validateV2(
        @RequestHeader(Headers.Authorization) authorizationHeader: String,
        @RequestBody body: ValidateRequestBody,
        @PathVariable ("subjectId") subjectId: String
    ): ResponseEntity<String> {

        logger.info("POST validate, subject Id - '$subjectId'")
        val parsed = travellerTripJwsParser.parse(authorizationHeader)

        if(parsed.statusCode != HttpStatus.OK) {
            logger.info("POST validate, subject Id - '$subjectId' failed validation - ${parsed.statusCode} because '${parsed.body}'}")
            return ResponseEntity(parsed.statusCode)
        }
        return httpPostValidationCommand.execute(parsed.body!!, body, subjectId)
    }
}