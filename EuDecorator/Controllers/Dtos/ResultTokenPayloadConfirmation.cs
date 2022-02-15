using System.Text.Json.Serialization;

namespace EuDecorator.Controllers.Dtos;

/// <summary>
/// 3.8.5.3
/// <see cref="ResultTokenPayload.Confirmation" />
/// </summary>
public class ResultTokenPayloadConfirmation
{
    /// <summary>
    /// Unique Identifier of the confirmation token
    /// GUID
    /// TODO <see href="https://docs.microsoft.com/en-us/dotnet/api/system.guid.tostring?view=net-6.0">What format? No example given</see>
    /// Not <see cref="ValidationAccessTokenPayload.TokenIdentifier">!!!!!
    /// </summary>
    [JsonPropertyName("jti")]
    public string Id { get; set; }

    /// <summary>
    /// Value of the access token
    /// e.g. ADEDDDDDDDDDDDDDDD
    /// TODO this looks like hex rather than base64??
    /// <see cref="ValidationAccessTokenPayload.TokenIdentifier">
    /// </summary>
    [JsonPropertyName("sub")]
    public string AccessTokenValue{ get; set; }

    /// <summary>
    /// TODO of what? This confirmation or is this copied from the validation request payload?
    /// Unix epoch time
    /// </summary>
    [JsonPropertyName("iat")]
    public int Iat { get; set; }

    /// <summary>
    /// Epoch time
    /// TODO of what? This confirmation or is this copied from the validation request payload?
    /// </summary>
    [JsonPropertyName("exp")]
    public int WhenExpires { get; set; }

    /// <summary>
    /// e.g. Inter-Flight, Concert, Domestic, MassEvent > 1000, etc.
    /// Category which  shall be reflected in the Validation by additional rules/logic. If null, Standard Business Rule Check will apply.
    /// Default: “Standard”
    /// For 2
    /// </summary>
    [JsonPropertyName("category")]
    public string[] Category { get; set; }

    /// <summary>
    /// Final result of the evaluation.
    /// OK = Passed
    /// NOK = Fail
    /// CHK = Cross Check(OPEN)
    /// </summary>
    public string Result { get; set; }
}