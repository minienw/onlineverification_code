package nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.initialize

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.PublicKeyJwk

/**
    * 3.8.3
    * The validation initialisation response delivers a JSON with a unique subject ID, which identifies the
    * occurrence in the validation validationservice.Next to the unique subject, a var key and an expiration date
    * for the occurrence are attached.The var key must be unique for each subject for maximum data
    * privacy.
    */
class ValidationInitializeResponse(
    /**
     * hexadecimal-encoded value
     * Traveller or whole trip?
     */
    @SerializedName("sub")
    @JsonProperty("sub")
    var subjectId: String,

    /**
     * Unix epoch time
     * Expiry
     */
    @SerializedName("exp")
    @JsonProperty("exp")
    var whenExpires: Long,

    /**
     * E.g. https://validationprovider/validate/{subject}
     */
    @SerializedName("aud")
    @JsonProperty("aud")
    var validationUrl: String,

    /**
     * "Optional var Key for Encryption of Validation nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.identity.Service."
     */
    @SerializedName("encKey")
    @JsonProperty("encKey")
    var validationServiceEncryptionKey: PublicKeyJwk?,

    /**
     * "Optional var Key for Signing of Validation nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.identity.Service."
     * See encKey...
     */
    @SerializedName("signKey")
    @JsonProperty("signKey")
    var signKey: PublicKeyJwk?,
)
