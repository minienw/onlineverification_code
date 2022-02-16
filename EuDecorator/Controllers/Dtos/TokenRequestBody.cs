using System.Text.Json.Serialization;

namespace EuDecorator.Controllers.Dtos;

/// <summary>
/// 3.10.1.3.4 Request Body
/// </summary>
public class TokenRequestBody
{
    /// <summary>
    /// Chosen Validator Service from Identity Document...
    /// Supposed to be chosen by Traveller in the wallet.
    /// Possibly fixed by the airline???
    /// </summary>
    public string Service { get; set; }
    /// <summary>
    /// What is the purpose of this?
    /// Public key of wallet?
    /// Instance of wallet 'session' for a given definition of session...
    /// Business transaction might be appropriate
    /// </summary>
    [JsonPropertyName("pubKey")]
    //TODO name? bytes? base64???
    public string WalletPublicKey { get; set; }
}