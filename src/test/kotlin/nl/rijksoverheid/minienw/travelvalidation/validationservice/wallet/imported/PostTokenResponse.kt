package nl.rijksoverheid.minienw.travelvalidation.validationservice.wallet.imported

class PostTokenResponse(
    val validationAccessToken: String,
    val nonce: String,
    val encKeyBase64: String,
    val sigKeyBase64: String)
