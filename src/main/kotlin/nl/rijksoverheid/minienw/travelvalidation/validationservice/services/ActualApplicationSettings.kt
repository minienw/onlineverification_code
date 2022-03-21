package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import org.springframework.stereotype.Component

@Component
class ActualApplicationSettings(
    private val file: ApplicationPropertiesFile
): IApplicationSettings
{
    override val validationResultJwsLifetimeSeconds: Long
        get() = file.resultTokenLifetimeSeconds.toLong()
    override val demoModeOn: Boolean
        get() = file.demoModeOn.toBoolean()
    override val demoModePassAllDccs: Boolean
        get() = file.demoPassAllDccs.toBoolean()
    override val dccVerificationServiceUri: String
        get() = file.dccVerificationServiceUri
    override val acceptedValidationTokenAlgorithms: Array<String>
        get() = arrayOf("RS256") //TODO add the lot and wire the key resolver back in
    override val validationResultJwsSigningKey: String
        get() = file.validationResultJwsSigningKey
    override val dccEncryptionRsaPrivateKey: String
        get() = file.dccEncryptionRsaPrivateKey
    override val dccEncryptionRsaPrivateKeyKid: String
        get() = TODO("Not yet implemented")
    override val sessionMaxDurationSeconds: Long
        get() = file.sessionMaxDurationSecondsString.toLong()
    override val configFileFolderPath: String
        get() = file.configFileFolderPath
    override val redisHost: String
        get() = file.redisHost
}
