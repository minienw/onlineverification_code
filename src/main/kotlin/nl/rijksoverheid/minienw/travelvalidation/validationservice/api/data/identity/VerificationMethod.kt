package nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.identity

import com.fasterxml.jackson.annotation.JsonProperty
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.PublicKeyJwk

/**
 3.8.2.2
*/
class VerificationMethod
(
    /**
     All ID values must be prefixed by {serviceproviderurl} and a fragment value, e.g., https://servicepovider/verificationmethod#AccessTokenSigning-1

     AccessTokenSignKey-X | Validation Decorator | Public key of the key pair of the validationservice provider to sign the access token
     AccessTokenServiceKeyX | Validation Decorator | Public key of the access token validationservice URL
     ValidationServiceKey-X | Validation Decorator |Public key of the used certificate for the validation validationservice URL
     ValidationServiceEncKeyX | Validation nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.identity.Service | Public key for encrypting the content send to the validation validationservice
     ValidationServiceSignKey-X | Validation nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.identity.Service | Public key of the key pair of the validation provider to sign the result token
     ValidationServiceEncSchemeKey-{EncryptionScheme} | Validation nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.identity.Service | Verification Method definition of available encryption schemes. Contains no public key. The Encryption Scheme is later used in the Validation Request.
     ServiceProviderKey-X | Validation Decorator | Public key of the used certificate of the validationservice provider URL
     CancellationServiceKey-X | Validation Decorator | Public key of the used certificate of the cancellation URL
     StatusServiceKey-X | Validation Decorator | Public key of the used certificate of thestatus URL
    */

    @JsonProperty("id")
    val id: String,

    @JsonProperty("type")
    val type: String = "JsonWebKey2020", //TODO simply this?

    /**
    */
    @JsonProperty("controller")
    val controller: String,

    /**
     Mandatory only for asymmetric encryption/signing, otherwise optional
    */
    @JsonProperty("publicKeyJwk")
    val publicKeyJwk: PublicKeyJwk?,

//    /**
//     Optional Verification IDs Array which can be used for referencing other Keys.
//     TODO this is a reference to <see cref="Id"/> ????
//    */
//    val verificationMethod: Array<VerificationMethod>
)