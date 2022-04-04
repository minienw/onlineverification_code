package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

interface IApplicationSettings
{
    val rootUrl: String
    val validationResultJwsLifetimeSeconds: Long
    val validationResultJwsSigningKey: String
    val dccEncryptionRsaPrivateKey: String
    val sessionMaxDurationSeconds: Long
    val configFileFolderPath: String
    val redisHost: String
    val dccVerificationServiceUri: String //"http://localhost:4002/verify_signature"
    val acceptedValidationTokenAlgorithms: Array<String>

    //TODO remove
    val demoModeOn: Boolean
    val demoModePassAllDccs: Boolean
}