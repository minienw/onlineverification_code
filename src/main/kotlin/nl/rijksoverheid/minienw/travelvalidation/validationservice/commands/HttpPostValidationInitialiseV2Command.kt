package nl.rijksoverheid.minienw.travelvalidation.validationservice.commands

import com.google.gson.Gson
import nl.rijksoverheid.minienw.travelvalidation.api.data.*
import nl.rijksoverheid.minienw.travelvalidation.api.data.identity.*
import nl.rijksoverheid.minienw.travelvalidation.api.data.initialize.*
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.*
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.io.File

private const val SubjectIdMismatchMessage = "SubjectId in path and token do not match."

@Service
class HttpPostValidationInitialiseV2Command(
    private val logger : Logger,
    private val dateTimeProvider: IDateTimeProvider,
    private val appSettings: IApplicationSettings,
    private val sessionRepository: ISessionRepository,
    private val bodyValidator: ValidationInitializeRequestBodyValidatorV2,
    private val subjectIdGenerator : ValidationServicesSubjectIdGenerator
) {
    fun execute(validationAccessTokenPayload: ValidationAccessTokenPayload, body: ValidationInitializeRequestBody, subjectId: String): ResponseEntity<Any>
    {
        val subjectIdValidationResult = subjectIdGenerator.validate(subjectId)
        if (subjectIdValidationResult.isNotEmpty())
        {
            val message = subjectIdValidationResult.joinToString("\n")
            logger.info("SubjectId is not a valid GUID - \n$message")
            return ResponseEntity.badRequest().body(message)
        }

        if (validationAccessTokenPayload.sub != subjectId) {
            logger.info(SubjectIdMismatchMessage)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(SubjectIdMismatchMessage)
        }

        val validationResult = bodyValidator.validate(body)
        if (validationResult.isNotEmpty()) {
            val message = validationResult.joinToString("\n")
            logger.info("Request is not valid - \n$message")
            return ResponseEntity.badRequest().body(message)
        }

        val f = File(appSettings.configFileFolderPath, "identity.json")
        val content = f.readText()
        val identityDoc = Gson().fromJson(content, IdentityResponse::class.java)
        val encryptionKey = findEncryptionKey(identityDoc)
        val verificationKey = findVerificationKey(identityDoc)
        val validationServiceValidateUri = findValidationServiceUri(identityDoc)

        val result = ValidationInitializeResponse(
            subjectId = subjectId,
            whenExpires = dateTimeProvider.snapshot().epochSecond + appSettings.sessionMaxDurationSeconds,
            validationUrl = "${validationServiceValidateUri}/${subjectId}",
            validationServiceEncryptionKey = encryptionKey,
            signKey = verificationKey
        )

        val cacheItem = SessionInfo(body, result)
        sessionRepository.save(cacheItem)

        return ResponseEntity.ok(result)
    }

    private fun findEncryptionKey(validationIdentity: IdentityResponse): PublicKeyJwk {
        return validationIdentity.verificationMethod.find{it.publicKeyJwk?.use.equals("enc")}?.publicKeyJwk ?: throw Exception("Could not find encryption key in identity.")
    }

    private fun findVerificationKey(validationIdentity: IdentityResponse): PublicKeyJwk {
        return validationIdentity.verificationMethod.find{it.publicKeyJwk?.use.equals("sig")}?.publicKeyJwk ?: throw Exception("Could not find verification key in identity.")
    }

    private fun findValidationServiceUri(validationIdentity: IdentityResponse): String {
        return validationIdentity.service.find{it.type.equals("ValidationService", ignoreCase = true)}?.serviceEndpoint ?: throw Exception("Could not find ValidationService endpoint in identity.")
    }
}


