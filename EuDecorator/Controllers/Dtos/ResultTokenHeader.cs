using System.Text.Json.Serialization;

namespace EuDecorator.Controllers.Dtos;

/// <summary>
/// 3.8.5.2
/// </summary>
public class ResultTokenHeader
{
    /// <summary>
    /// TODO ???? as JWT...
    /// </summary>
    [JsonPropertyName("typ")]
    public string Type { get; set; }

    /// <summary>
    /// ES256 or PS256 or RS256
    /// </summary>
    [JsonPropertyName("alg")]
    public string SignatureAlgorithm { get; set; }

    /// <summary>
    /// AccessTokenSigningKey-X KID
    /// Used key for signing. This key must be presented in the downloaded identity document.
    /// </summary>
    [JsonPropertyName("kid")]
    public string KeyId { get; set; }
}