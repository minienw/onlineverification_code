using System.Text.Json.Serialization;

namespace EuDecorator.Controllers.Dtos;

/// <summary>
/// 3.8.4.3 Validation Access Token Payload
/// The Token Replay section regarding replay mitigation is in section 3.8.4.3 ->
/// this must refer to <see cref="ValidationAccessTokenPayload.TokenIdentifier"/> and <see cref="ValidationAccessTokenPayload.WhenExpires"/>
/// </summary>
public class ValidationAccessTokenPayload
{
    /// <summary>
    /// TODO From????
    /// </summary>
    [JsonPropertyName("jti")]
    public string TokenIdentifier { get; set; }

    /// <summary>
    /// https://serviceprovider
    /// id (uri?) of identity document
    /// </summary>
    [JsonPropertyName("iss")]
    public string ServiceProvider { get; set; }

    /// <summary>
    /// TODO of what?
    /// Unix epoch time
    /// </summary>
    [JsonPropertyName("iat")]
    public int Iat { get; set; }

    /// <summary>
    /// TODO "Value of the Initialisation"
    /// e.g. ADEDDDDDDDDDDDDDDD
    /// </summary>
    [JsonPropertyName("sub")]
    public string Subject { get; set; }

    /// <summary>
    /// Value of the Initialisation (must match to service endpoint “ValidationService”)
    /// e.g. https://validationprovider/validate/{subject}
    /// </summary>
    [JsonPropertyName("aud")]
    public string SubjectUri { get; set; }

    /// <summary>
    /// Epoch time
    /// </summary>
    [JsonPropertyName("exp")]
    public int WhenExpires { get; set; }

    /// <summary>
    /// </summary>
    [JsonPropertyName("t")]
    public ValidationType ValidationType { get; set; }

    /// <summary>
    /// e.g. 1.0
    /// </summary>
    [JsonPropertyName("v")]
    public string ValidationVersion { get; set; }

    /// <summary>
    /// Validation Conditions
    /// (optional, depending on Type = Full)
    /// </summary>
    [JsonPropertyName("vc")]
    public ValidationAccessTokenPayloadCondition ValidationCondition { get; set; }
}