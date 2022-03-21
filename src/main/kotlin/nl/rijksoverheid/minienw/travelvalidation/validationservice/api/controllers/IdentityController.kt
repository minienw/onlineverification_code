package nl.rijksoverheid.minienw.travelvalidation.validationservice.api.controllers

//import io.swagger.v3.oas.annotations.Operation
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import org.springframework.http.HttpStatus
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
@RestController()
class IdentityController(
    val appSettings : IApplicationSettings
) {
    //ValidationServiceSignKey-X
    //Can be ES256/PS256/RS256
    //@Operation(summary = "Sets a price for a chosen car", description = "Returns 202 if successful")
//    @GetMapping("/identity/SignKey-X", headers = [Headers.AcceptJson, Headers.MustBeVersion2], produces = [Headers.Json])
//    @ResponseStatus(HttpStatus.OK)
//    fun SignKey() : ResponseEntity<PublicKeyJwk>
//    {
//        return JustTheSigningPublicKey()
//    }
//
//    @GetMapping("/identity/SignKey-X")
//    @ResponseStatus(HttpStatus.OK)
//    fun VanillaSignKey() : ResponseEntity<PublicKeyJwk>
//    {
//        return JustTheSigningPublicKey()
//    }

    //TODO parse from settings file identity.json
//    private fun JustTheSigningPublicKey(): ResponseEntity<PublicKeyJwk> {
//        var keyBase64 = appSettings.validationResultJwsVerificationKey
//        var response = PublicKeyJwk(
//            x5c = arrayOf(keyBase64),
//            alg = "RS256",
//            kid = CryptoKeyConverter.getKid(Base64.decode(keyBase64)),
//            use = "sig"
//        )
//        //TODO cache
//        return ResponseEntity(response, HttpStatus.OK)
//    }

//    @GetMapping("/identity/verificationMethod/ValidationServiceSignKey#ValidationServiceSignKey-1", produces = [Headers.Json])
//    @ResponseStatus(HttpStatus.OK)
//    fun SignKeyAlt(): ResponseEntity<PublicKeyJwk>
//    {
//        return JustTheSigningPublicKey()
//    }

    @GetMapping("/identity")
    @ResponseStatus(HttpStatus.OK)
    fun WholeDoc(): ResponseEntity<String>
    {
        var f = File(appSettings.configFileFolderPath, "identity.json")
        var content = f.readText()
        return ResponseEntity(content, HttpStatus.OK)
    }

//    //@Operation(summary = "Sets a price for a chosen car", description = "Returns 202 if successful")
//    @GetMapping("/identity/{element}/{type}", headers = [Headers.AcceptJson, "${Headers.Authorization}=${Headers.V2}"], produces = [Headers.Json])
//    @ResponseStatus(HttpStatus.OK)
////    [ProducesResponseType(StatusCodes.Status404NotFound)]
//    fun Identity(element: String, type: String) : IdentityResponse {
//        throw Exception();
//    }

//@Operation(summary = "Sets a price for a chosen car", description = "Returns 202 if successful")
//    @GetMapping("/identity{element}/{type}#{id}", headers = [Headers.AcceptJson, "${Headers.Auth}=${Headers.V2}"], produces = [Headers.Json])
//    @ResponseStatus(HttpStatus.OK)
//    //    [ProducesResponseType(StatusCodes.Status404NotFound)]
//    fun Identity(element: String, type: String, id: String) : nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.identity.IdentityResponse {
//        throw Exception();
//    }
}