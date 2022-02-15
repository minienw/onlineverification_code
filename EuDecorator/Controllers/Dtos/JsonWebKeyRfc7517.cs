using System.Text.Json.Serialization;

namespace EuDecorator.Controllers.Dtos;

/// <summary>
/// The JWK must contain a x509 certificate with the public key for signing/encryption in PEM encoding
/// without markers, using ECDSA (P-256 parameters, ES256) or RSA-PSS (PS256) or RSA (RS256)
/// algorithm following the cryptographic requirements of the DCC certificate governance. The used
/// algorithm and the intention of usages must be set in the JWK
/// </summary>
public class JsonWebKeyRfc7517
{
    /// <summary>
    /// PEM encoding without markers
    /// </summary>
    [JsonPropertyName("x5c")]
    public string[] X5C { get; set; }

    /// <summary>
    /// First 8 bytes of the SHA256 Fingerprint of the X5C
    /// TODO HEX?
    /// </summary>
    [JsonPropertyName("kid")]
    public string KeyId { get; set; }

    /// <summary>
    /// E.g. ES256/PS256/RS256
    /// </summary>
    [JsonPropertyName("alg")]
    public string Algorithm { get; set; }

    /// <summary>
    /// TODO Examples
    /// </summary>
    [JsonPropertyName("use")]
    public string Use { get; set; }
}