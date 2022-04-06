package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import org.springframework.stereotype.Component

@Component
class ActualApplicationSettings(
    private val file: ApplicationPropertiesFile
): IApplicationSettings
{
    override val rootUrl: String
        get() = file.rootUrl;
    override val validationResultJwsLifetimeSeconds: Long
        get() = file.resultTokenLifetimeSeconds.toLong()
    override val dccVerificationServiceUri: String
        get() = file.dccVerificationServiceUri
    override val acceptedValidationTokenAlgorithms: Array<String>
        get() = arrayOf("RS256", "PS256", "ES256") //TODO move to setting! https://stackoverflow.com/questions/50395985/spring-boot-read-array-from-yaml-properties-file

    override val configUri: String
        get() = file.configUri
    override val customBusinessRulesUri: String
        get() = file.customBusinessRulesUri
    override val businessRulesUri: String
        get() = file.businessRulesUri
    override val valueSetsUri: String
        get() = file.valueSetsUri
    override val publicKeysUri: String
        get() = file.publicKeysUri
    override val publicKeysFileName: String
        get() = file.publicKeysFileName

    override val validationResultJwsSigningKey: String
        get() = file.validationResultJwsSigningKey
    override val dccEncryptionRsaPrivateKey: String
        get() = file.dccEncryptionRsaPrivateKey
    override val sessionMaxDurationSeconds: Long
        get() = file.sessionMaxDurationSecondsString.toLong()
    override val configFileFolderPath: String
        get() = file.configFileFolderPath
    override val redisHost: String
        get() = file.redisHost
}
