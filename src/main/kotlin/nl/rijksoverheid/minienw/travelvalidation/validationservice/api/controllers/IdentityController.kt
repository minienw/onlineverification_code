package nl.rijksoverheid.minienw.travelvalidation.validationservice.api.controllers

//import io.swagger.v3.oas.annotations.Operation
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.Authorization
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import nl.rijksoverheid.minienw.travelvalidation.api.data.identity.IdentityResponse
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import nl.rijksoverheid.minienw.travelvalidation.validationservice.swagger.Examples
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.io.File


/**
NOPE! 1 per session - ValidationServiceEncKey-X Public key for encrypting the content send to the validation validationservice
NOPE! see above ValidationServiceEncSchemeKey-{EncryptionScheme} Verification Method - definition of available encryption schemes. Contains no public key. The Encryption Scheme is later used in the Validation Request.
ValidationServiceSignKey-X Public key of the key pair of the validation provider to sign the result token
*/
@Api
@RestController()
class IdentityController(
    val appSettings : IApplicationSettings
) {
    @GetMapping("/identity")
    @ApiOperation(value = "Get the configuration information about the hosted services.", authorizations = arrayOf<Authorization>())
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "OK"
            , content = [Content(mediaType= MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = IdentityResponse::class
                    , example = Examples.Identity
                ) )]
        )
        , ApiResponse(responseCode = "500", description = "Server Error")
    ])

    fun WholeDoc(): ResponseEntity<String> {
        val f = File(appSettings.configFileFolderPath, "identity.json")
        val content = f.readText()
        return ResponseEntity(content, HttpStatus.OK)
    }
}