package nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.validate

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

/**
* 3.10.2.4.4 Request Body
*/
class ValidateRequestBody
    (
    /**
    * Used kid for encryption. 
    * e.g. 239348fdfff -> hex?
    * e.g. the public key from the validationservice?
    */
    @SerializedName("kid")
    @JsonProperty("kid")
    var encryptionKeyId : String,

    /**
    * Encrypted with var key from <see cref="EncryptionKeyId"/>
    * Encrypted DCC according to encScheme.Input is the HCert base45 string.
    * Base64 encoded - BEWARE DOUBLE ENCODING/DECODING
    */
    @SerializedName("dcc")
    @JsonProperty("dcc")
    var encryptedDcc : String,

    /**
    * User Signature of the unencrypted dcc content.The Validation nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.identity.Service checks it against the var key transmitted during the initialisation.Proofs that the sender which initializes the booking knows the transmitted content.
    * Contains ECDSA/RSA signature -> is that an OR?
    * Assuming base64
     * Integrity check
    */
    @SerializedName("sig")
    @JsonProperty("sig")
    var encryptedDccSignature : String?,

    /**
    * Used Signature Algorithm
    * e.g. SHA256withECDSA
     * TODO repetative
    */
    @SerializedName("sigAlg")
    @JsonProperty("sigAlg")
    var encryptedDccSignatureAlgorithm : String?,

    @SerializedName("encScheme")
    @JsonProperty("encScheme")
    var encryptedScheme : String,

    /**
    * Optional, if no CMS or no Encryption Scheme with built in AES is used(e.g. X963SHA256AESGCM). The key must be randomly created and encrypted by ECDSA/RSA OEAP.Keylength must be minimum 32 bytes.
    * Base64 of byte[]
    */
    @SerializedName("encKey")
    @JsonProperty("encKey")
    var encryptionKey : String?,
)