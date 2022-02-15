using System.Text.Json.Serialization;

namespace EuDecorator.Controllers.Dtos;


/// <summary>
/// 3.10.1.3.4 Request Body
/// </summary>
public class TokenRequestBody
{
    [JsonPropertyName("pubKey")]
    //TODO name? bytes? base64???
    public string PublicKey { get; set; }

    /// <summary>
    /// chosen Validator Service from Identity Document...
    /// </summary>
    public string Service { get; set; }
}