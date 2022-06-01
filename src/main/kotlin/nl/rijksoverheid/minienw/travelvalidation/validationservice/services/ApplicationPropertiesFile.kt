package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("validation-service")
class ApplicationPropertiesFile {
    lateinit var validationAccessTokenSignatureAlgorithms: String
    lateinit var dccVerificationServiceUri: String
    lateinit var dccEncryptionRsaPrivateKey: String //TODO needs to be decrypted by resolving kid
    lateinit var resultTokenLifetimeSeconds: String
    lateinit var configFileFolderPath: String
    lateinit var sessionMaxDurationSecondsString: String
    lateinit var validationResultJwsSigningKey: String
    lateinit var redisHost: String //e.g. "redis" whatever the container was called in the compose.
    lateinit var airlineIdentityUris: String
    lateinit var configUri: String
    lateinit var customBusinessRulesUri: String
    lateinit var businessRulesUri: String
    lateinit var valueSetsUri: String
    lateinit var publicKeysUri: String
    //And where to drop it so the verifier can see it.
    lateinit var publicKeysFileName: String
    lateinit var  dccArtifactParsingServiceUri: String
}