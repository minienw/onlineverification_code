using System.Text.Json.Serialization;

namespace EuDecorator.Controllers.Dtos;

/// <summary>
/// 3.8.1
/// This token is generated to access the Validation Decorator endpoints and contains the information about the booking.
/// </summary>
public class SubjectValidationDecoratorAccessToken
{
    /// <summary>
    /// TODO same as AirlineDccValidationRequest.Subject?
    /// </summary>
    public string Issuer { get; set; }

    /// <summary>
    /// Random value which references the service provider’s occurrence
    /// TODO ASSUMPTION This represent the booking and the traveller
    /// </summary>
    [JsonPropertyName("sub")]
    public string Subject { get; set; }

    /// <summary>
    /// Unix epoch time
    /// </summary>
    [JsonPropertyName("iat")]
    public int WhenIssued { get; set; }

    /// <summary>
    /// Unix epoch time
    /// </summary>
    [JsonPropertyName("exp")]
    public int WhenExpires { get; set; }
}