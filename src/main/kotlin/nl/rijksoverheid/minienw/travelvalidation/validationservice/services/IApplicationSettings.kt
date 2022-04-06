package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

interface IApplicationSettings
{

    val rootUrl: String
    val validationResultJwsLifetimeSeconds: Long
    val validationResultJwsSigningKey: String
    val dccEncryptionRsaPrivateKey: String
    val sessionMaxDurationSeconds: Long
    @Deprecated("Tests Only")
    val configFileFolderPath: String
    val redisHost: String
    val dccVerificationServiceUri: String //"http://localhost:4002/verify_signature"
    val acceptedValidationTokenAlgorithms: Array<String>

    val configUri: String
    val customBusinessRulesUri: String
    val businessRulesUri: String
    val valueSetsUri: String
    val publicKeysUri: String
    val publicKeysFileName: String
}