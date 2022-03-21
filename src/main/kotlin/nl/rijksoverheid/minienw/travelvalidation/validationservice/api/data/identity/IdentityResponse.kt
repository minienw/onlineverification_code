package nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.identity

import com.fasterxml.jackson.annotation.JsonProperty

class IdentityResponse
(
    @JsonProperty("id")
    val id: String,

    /**
    TODO name should be plural
     */
    @JsonProperty("verificationMethod")
    val verificationMethod :  Array<VerificationMethod>,

    /** TODO name should be plural*/
    @JsonProperty("validationservice")
    val service : Array<Service>
)