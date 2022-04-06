package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

interface IApplicationSettings
{
    val airlineIdentityUris: Array<String>
    val rootUrl: String
    val validationResultJwsLifetimeSeconds: Long
    val validationResultJwsSigningKey: String
    val dccEncryptionRsaPrivateKey: String
    val sessionMaxDurationSeconds: Long
    val redisHost: String
    val dccVerificationServiceUri: String //"http://localhost:4002/verify_signature"

    val configUri: String
    val customBusinessRulesUri: String
    val businessRulesUri: String
    val valueSetsUri: String
    val publicKeysUri: String
    val publicKeysFileName: String

    @Deprecated("Tests Only")
    val configFileFolderPath: String
}