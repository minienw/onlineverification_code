package nl.rijksoverheid.minienw.travelvalidation.validationservice.api.controllers

import com.google.gson.Gson
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.validate.ValidateRequestBody
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.initialize.ValidationInitializeRequestBody
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.HttpPostValidationInitialiseV2Command
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.*
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.ValidationAccessTokenParser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*


@Component
@RestController
class ValidationControllerV2(
    val travellerTripJwsParser : ValidationAccessTokenParser, //TODO make once per request
    val httpPostValidationInitialiseCommand : HttpPostValidationInitialiseV2Command, //TODO make once per request
    val httpPostValidationCommand : HttpPostValidationV2Command, //TODO make once per request
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
        println("Init >>>>>")
        println(authorizationHeader)
        println(Gson().toJson(body))
        println(subjectId)
        println("<<<<< Init")

        var parsed = travellerTripJwsParser.parse(authorizationHeader)
        if(parsed.statusCode != HttpStatus.OK)
            return ResponseEntity(parsed.statusCode)

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
        println("Val >>>>>")
        println(authorizationHeader)
        println(Gson().toJson(body))
        println(subjectId)
        println("<<<< Val")

        var parsed = travellerTripJwsParser.parse(authorizationHeader)

        if(parsed.statusCode != HttpStatus.OK)
            return ResponseEntity(parsed.statusCode)

        return httpPostValidationCommand.execute(parsed.body!!, body, subjectId)
    }
}