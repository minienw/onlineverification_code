package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("validation-service")
class ApplicationPropertiesFile {
    lateinit var rootUrl: String
    lateinit var dccVerificationServiceUri: String
    lateinit var demoPassAllDccs: String
    lateinit var dccEncryptionRsaPrivateKey: String //TODO needs to be decrypted by resolving kid
    lateinit var dccEncryptionRsaPrivateKeyKid: String
    lateinit var demoModeOn: String
    lateinit var resultTokenLifetimeSeconds: String
    lateinit var configFileFolderPath: String
    lateinit var sessionMaxDurationSecondsString: String
    lateinit var validationResultJwsSigningKey: String
    lateinit var redisHost: String
    lateinit var verifierHost: String
}