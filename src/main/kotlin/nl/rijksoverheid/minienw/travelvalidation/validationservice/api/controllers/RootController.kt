package nl.rijksoverheid.minienw.travelvalidation.validationservice.api.controllers

import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.ValidationAccessTokenPayload
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.validate.ConfirmationTokenPayload
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.validate.ResultTokenPayload
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Component
@RestController
class RootController {

    @GetMapping("/")
    fun GetRoot() : ResponseEntity<String>
    {
        return ResponseEntity( "<html>Swagger is <a href=\"swagger-ui//index.html\">here</a><html>",HttpStatus.OK)
    }

    @GetMapping("/fakeValidationAccessTokenPayload")
    fun showValidationAccessTokenPayload() : ResponseEntity<ValidationAccessTokenPayload>
    {
        return ResponseEntity(HttpStatus.I_AM_A_TEAPOT);
    }

    @GetMapping("/fakeResponseToken")
    fun showResultTokenPayload() : ResponseEntity<ResultTokenPayload>
    {
        return ResponseEntity(HttpStatus.I_AM_A_TEAPOT);
    }

    @GetMapping("/fakeConfirmationTokenPayload")
    fun showConfirmationTokenPayload() : ResponseEntity<ConfirmationTokenPayload>
    {
        return ResponseEntity(HttpStatus.I_AM_A_TEAPOT);
    }
}