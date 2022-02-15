using System.Text.Json.Serialization;

namespace EuDecorator.Controllers.Dtos;

/// <summary>
/// 3.10.2.4.4 Request Body
/// </summary>
public class ValidateRequestBody
{
    /// <summary>
    /// Used kid for encryption. 
    /// e.g. 239348fdfff -> hex?
    /// e.g. the pub;lic key from the service?
    /// </summary>
    [JsonPropertyName("kid")]
    public string EncryptionKeyId { get; set; }

    /// <summary>
    /// Encrypted with public key from <see cref="EncryptionKeyId"/>
    /// Encrypted DCC according to encScheme.Input is the HCert base45 string.
    /// Base64 encoded - BEWARE DOUBLE ENCODING/DECODING
    /// </summary>
    [JsonPropertyName("dcc")]
    public string EncryptedDcc { get; set; }

    /// <summary>
    /// User Signature of the unencrypted dcc content.The Validation Service checks it against the public key transmitted during the initialisation.Proofs that the sender which initializes the booking knows the transmitted content.
    /// Contains ECDSA/RSA signature -> is that an OR?
    /// Assuming base64
    /// </summary>
    [JsonPropertyName("sig")]
    public string EncryptedDccSignature { get; set; }

    /// <summary>
    /// Used Signature Algorithm
    /// e.g. SHA256withECDSA
    /// </summary>
    [JsonPropertyName("sigAlg")]
    public string EncryptedDccSignatureAlgorithm { get; set; }

    [JsonPropertyName("encScheme")] public string EncryptedScheme { get; set; }

    /// <summary>
    /// Optional, if no CMS or no Encryption Scheme with built in AES is used(e.g. X963SHA256AESGCM). The key must be randomly created and encrypted by ECDSA/RSA OEAP.Keylength must be minimum 32 bytes.
    /// Base64 of byte[]
    /// </summary>
    [JsonPropertyName("encKey")]
    public string EncryptionKey { get; set; }
}