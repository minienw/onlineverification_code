package nl.rijksoverheid.minienw.travelvalidation.validationservice.api.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import nl.rijksoverheid.minienw.travelvalidation.api.Headers
import nl.rijksoverheid.minienw.travelvalidation.api.data.identity.IdentityResponse
import nl.rijksoverheid.minienw.travelvalidation.api.data.initialize.ValidationInitializeRequestBody
import nl.rijksoverheid.minienw.travelvalidation.api.data.initialize.ValidationInitializeResponse
import nl.rijksoverheid.minienw.travelvalidation.api.data.validate.ValidateRequestBody
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.HttpPostValidationInitialiseV2Command
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.*
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.ValidationAccessTokenParser
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*


@Api
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

    @ApiOperation(value = "Called by the airline/server providers POST /token to initialize some of the parameters of the subsequent POST /validate call by passing the AES IV and Subject Id.")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200", description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE
                , schema = Schema(implementation = ValidationInitializeResponse::class, description = "Response contains the encryption key to use when encrypting the DCC Artifact during the Validate call.")
            )])
        , ApiResponse(responseCode = "400", description = "Invalid request body.", content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = Schema(implementation = String::class))])
        , ApiResponse(responseCode = "401", description = "Authorization token signature invalid or payload invalid.")
        , ApiResponse(responseCode = "500", description = "Server Error.", content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = Schema(implementation = String::class))])
    ])

    fun initialize(
        @RequestBody body: ValidationInitializeRequestBody,
        @ApiParam(name=Headers.Authorization, required = true, example = "bearer notreallyanexample")
        @RequestHeader(Headers.Authorization) authorizationHeader: String,
        @PathVariable("subjectId") subjectId: String
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
    @ApiOperation(value = "Performs the verification of a DCC and evaluates the business rules. Returns the JWT of ResultTokenPayload.")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "OK",
            content = [Content(
                mediaType = MediaType.TEXT_PLAIN_VALUE, schema = Schema(implementation = String::class)
            )]
        )
        , io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body including invalid Response Token signature.", content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
        , io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authorization token signature invalid or payload invalid.", content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
        , io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "410", description = "Subject does not exist.")
        , io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server Error.", content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    ])
    fun validateV2(
        @ApiParam(name=Headers.Authorization, required = true, example = "bearer notreallyanexample")
        @RequestHeader(Headers.Authorization) authorizationHeader: String,
        @RequestBody body: ValidateRequestBody,
        @ApiParam(name="subjectId", required = true, example = "ABABABABABABA etc...")
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