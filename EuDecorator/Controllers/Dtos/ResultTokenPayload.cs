using System.Text.Json.Serialization;

namespace EuDecorator.Controllers.Dtos;

/// <summary>
/// 3.8.5.3
/// </summary>
public class ResultTokenPayload
{
    /// <summary>
    /// https://serviceprovider
    /// id (uri?) of identity document
    /// </summary>
    [JsonPropertyName("iss")]
    public string ServiceProvider { get; set; }

    /// <summary>
    /// TODO time of what?
    /// Unix epoch time
    /// </summary>
    [JsonPropertyName("iat")]
    public int Iat { get; set; }

    /// <summary>
    /// TODO What expires?
    /// Epoch time
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
    /// TODO "Value of the Initialisation"
    /// e.g. ADEDDDDDDDDDDDDDDD
    /// </summary>
    [JsonPropertyName("sub")]
    public int Subject { get; set; }

    /// <summary>
    /// Final result of the evaluation.
    /// OK = Passed
    /// NOK = Fail
    /// CHK = Cross Check(OPEN)
    /// </summary>
    public string Result { get; set; }

    /// <summary>
    /// TODO The results ARE in the signed packet!
    /// </summary>
    public ResultTokenPayloadResult[] Results { get; set; }

    public ResultTokenPayloadConfirmation Confirmation { get; set; }
}