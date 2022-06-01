package nl.rijksoverheid.minienw.travelvalidation.validationservice.commands

import com.google.gson.Gson
import nl.rijksoverheid.minienw.travelvalidation.api.data.*
import nl.rijksoverheid.minienw.travelvalidation.api.data.identity.*
import nl.rijksoverheid.minienw.travelvalidation.api.data.initialize.*
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.io.File

@Service
class HttpPostValidationInitialiseV2Command(
    private val dateTimeProvider: IDateTimeProvider,
    private val appSettings: IApplicationSettings,
    private val sessionRepository: ISessionRepository,
    private val bodyValidator: ValidationInitializeRequestBodyValidatorV2
) {
    fun execute(validationAccessTokenPayload: ValidationAccessTokenPayload, body: ValidationInitializeRequestBody, subjectId: String): ResponseEntity<Any>
    {
//        var subjectIdValidationResult = subjectIdGenerator.validate(subjectId)
//        if (subjectIdValidationResult.isNotEmpty())
//        {
//            //TODO log subjectIdValidationResult
//            return ResponseEntity(HttpStatus.BAD_REQUEST) //TODO .body("Incorrect subject id format - " + subjectIdValidationResult.joinToString("\n")
//        }

        if (validationAccessTokenPayload.sub != subjectId)
            return ResponseEntity(HttpStatus.UNAUTHORIZED)

        var validationResult = bodyValidator.validate(body)
        if (validationResult.isNotEmpty()) {
            //TODO Log validationResult.joinToString("\n")
            return ResponseEntity.badRequest().body(validationResult.joinToString("\n"))
        }

        var f = File(appSettings.configFileFolderPath, "identity.json")
        var content = f.readText()
        var identityDoc = Gson().fromJson(content, IdentityResponse::class.java)
        var encryptionKey = findEncryptionKey(identityDoc)
        var verificationKey = findVerificationKey(identityDoc) //TODO what is this one actually used for? Is this the correct key?
        var validationServiceValidateUri = findValidationServiceUri(identityDoc)

        val result = ValidationInitializeResponse(
            subjectId = subjectId,
            whenExpires = dateTimeProvider.snapshot().epochSecond + appSettings.sessionMaxDurationSeconds,
            validationUrl = "${validationServiceValidateUri}/${subjectId}",
            validationServiceEncryptionKey = encryptionKey,
            signKey = verificationKey
        )

        //TODO re-instate this code for V1 or if V2 starts checking signatures of encrypted DCCs
        val cacheItem = SessionInfo(body, result) //Cut this down a bit.
        sessionRepository.save(cacheItem)

        return ResponseEntity.ok(result)
    }

    private fun findEncryptionKey(validationIdentity: IdentityResponse): PublicKeyJwk {
        return validationIdentity.verificationMethod.find{it.publicKeyJwk?.use.equals("enc")}?.publicKeyJwk ?: throw Exception()
    }

    private fun findVerificationKey(validationIdentity: IdentityResponse): PublicKeyJwk {
        return validationIdentity.verificationMethod.find{it.publicKeyJwk?.use.equals("sig")}?.publicKeyJwk ?: throw Exception()
    }

    private fun findValidationServiceUri(validationIdentity: IdentityResponse): String {
        return validationIdentity.service.find{it.type.equals("ValidationService", ignoreCase = true)}?.serviceEndpoint ?: throw Exception()
    }
}


