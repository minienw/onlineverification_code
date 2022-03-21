package nl.rijksoverheid.minienw.travelvalidation.validationservice.commands

import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.ValidationAccessTokenPayload
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.initialize.ValidationInitializeRequestBody
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.initialize.ValidationInitializeResponse
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

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

        if (validationAccessTokenPayload.subject != subjectId)
            return ResponseEntity(HttpStatus.UNAUTHORIZED)

        var validationResult = bodyValidator.validate(body)
        if (validationResult.isNotEmpty()) {
            //TODO Log validationResult.joinToString("\n")
            return ResponseEntity.badRequest().body(validationResult.joinToString("\n"))
        }

        val result = ValidationInitializeResponse(
            subjectId = subjectId,
            whenExpires = dateTimeProvider.snapshot().epochSecond + appSettings.sessionMaxDurationSeconds,
            validationServiceEncryptionKey = null,
            signKey = null
        )

        //TODO re-instate this code for V1 or if V2 starts checking signatures of encrypted DCCs
        val cacheItem = SessionInfo(body, result) //Cut this down a bit.
        sessionRepository.save(cacheItem)

        return ResponseEntity.ok(result)
    }
}


