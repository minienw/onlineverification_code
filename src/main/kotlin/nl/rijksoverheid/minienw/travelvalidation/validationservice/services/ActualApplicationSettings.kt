package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import org.springframework.stereotype.Component

@Component
class ActualApplicationSettings(
    private val file: ApplicationPropertiesFile
): IApplicationSettings
{
    override val validationAccessTokenSignatureAlgorithms: Array<String>
        get() {
            val result = ArrayList<String>()
            for(i in file.validationAccessTokenSignatureAlgorithms.split(","))
                result.add(i.trim())

            return result.toTypedArray()
        }
    override val airlineIdentityUris: Array<String>
        get() {
            val result = ArrayList<String>()
            for(i in file.airlineIdentityUris.split(","))
                result.add(i.trim())

            return result.toTypedArray()
        }

    override val validationResultJwsLifetimeSeconds: Long
        get() = file.resultTokenLifetimeSeconds.toLong()
    override val dccVerificationServiceUri: String
        get() = file.dccVerificationServiceUri
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
    override val validationResultJwsVerificationKid: String
        get() = file.validationResultJwsVerificationKid
    override val dccEncryptionRsaPrivateKey: String
        get() = file.dccEncryptionRsaPrivateKey
    override val sessionMaxDurationSeconds: Long
        get() = file.sessionMaxDurationSecondsString.toLong()
    override val configFileFolderPath: String
        get() = file.configFileFolderPath
    override val dccArtifactParsingServiceUri: String
        get() = file.dccArtifactParsingServiceUri
    override val redisHost: String
        get() = file.redisHost
}
