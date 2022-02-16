using System.Text.Json.Serialization;

namespace EuDecorator.Controllers.Dtos;

/// <summary>
/// 3.8.5.3
/// <see cref="ResultTokenPayload.Confirmation" />
/// </summary>
public class ResultTokenPayloadConfirmation
{
    /// <summary>
    /// TODO may have confused <see cref="AccessTokenValue"/> and <see cref="Id"/> 
    /// Unique Identifier of the confirmation token
    /// GUID ?????
    /// TODO <see href="https://docs.microsoft.com/en-us/dotnet/api/system.guid.tostring?view=net-6.0">What format? No example given</see>
    /// Not <see cref="ValidationAccessTokenPayload.TokenIdentifier">!!!!!
    /// </summary>
    [JsonPropertyName("jti")]
    public string Id { get; set; }

    /// <summary>
    /// TODO may have confused <see cref="AccessTokenValue"/> and <see cref="Id"/> 
    /// TODO NB subject is a GUID
    /// Value of the access token
    /// Subject from airline
    /// e.g. ADEDDDDDDDDDDDDDDD
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

    /// <summary>
    /// The world according to EF ;)
    /// </summary>
    public DisclosedPersonalData DisclosedPersonalData { get; set; }

}