using System.Text.Json.Serialization;

namespace EuDecorator.Controllers.Dtos;


/// <summary>
/// 3.8.2.2
/// </summary>
public class VerificationMethod
{
    /// <summary>
    /// All ID values must be prefixed by {serviceproviderurl} and a fragment value, e.g., https://servicepovider/verificationmethod#AccessTokenSigning-1
    ///
    /// AccessTokenSignKey-X | Validation Decorator | Public key of the key pair of the service provider to sign the access token
    /// AccessTokenServiceKeyX | Validation Decorator | Public key of the access token service URL
    /// ValidationServiceKey-X | Validation Decorator |Public key of the used certificate for the validation service URL
    /// ValidationServiceEncKeyX | Validation Service | Public key for encrypting the content send to the validation service
    /// ValidationServiceSignKey-X | Validation Service | Public key of the key pair of the validation provider to sign the result token
    /// ValidationServiceEncSchemeKey-{EncryptionScheme} | Validation Service | Verification Method definition of available encryption schemes. Contains no public key. The Encryption Scheme is later used in the Validation Request.
    /// ServiceProviderKey-X | Validation Decorator | Public key of the used certificate of the service provider URL
    /// CancellationServiceKey-X | Validation Decorator | Public key of the used certificate of the cancellation URL
    /// StatusServiceKey-X | Validation Decorator | Public key of the used certificate of thestatus URL
    ///
    /// </summary>
    public string Id { get; set; }
    public string Type { get; set; } = "JsonWebKey2020"; //TODO simply this?

    /// <summary>
    /// 
    /// </summary>
    [JsonPropertyName("controller")]
    public string ServiceUri { get; set; }

    /// <summary>
    /// Mandatory only for asymmetric encryption/signing, otherwise optional
    /// </summary>
    public JsonWebKeyRfc7517 publicKeyJwk { get; set; }

    /// <summary>
    /// Optional Verification IDs Array which can be used for referencing other Keys.
    /// TODO this is a reference to <see cref="Id"/> ????
    /// </summary>
    [JsonPropertyName("verificationMethods")]
    public string[] VerificationMethodIds { get; set; }
}