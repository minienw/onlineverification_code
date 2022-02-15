using System.Text.Json.Serialization;

namespace EuDecorator.Controllers.Dtos;





/// <summary>
/// 3.8.1 Initialization QR Code Content
/// Called InitializationQrCodeContent in another doc/code
/// </summary>
public class InitializationQrCodeContent
{
    /// <summary>
    /// Type of the requested protocol
    /// </summary>
    public string Protocol { get; set; } = "DCCVALIDATION";

    /// <summary>
    /// SemVer version number
    /// </summary>
    public string ProtocolVersion { get; set; } = "1.0.0";

    /// <summary>
    /// TODO URL to the service provider identity document - if the Airline knows the 'Give me a QR Code' end point, they know the Identity end point too.
    /// TODO When are the contents of the Identity Document used?
    /// </summary>
    public string ServiceIdentity { get; set; }

    /// <summary>
    /// A separate privacy statement url with additional data processing information.
    /// TODO who writes and hosts this?
    /// </summary>
    public string PrivacyUrl { get; set; }

    /// <summary>
    /// This token is generated to access the Validation Decorator endpoints and contains the information about the booking.
    /// TODO JWT or JWK RFC7517 ?
    /// <see cref="SubjectValidationDecoratorAccessToken" />
    /// </summary>
    [JsonPropertyName("token")]
    public string SubjectToken { get; set; }

    /// <summary>
    /// Consent text which is shown to the user by the wallet app
    /// </summary>
    public string Consent { get; set; }

    ///<summary>
    // e.g. Booking Nr.1234
    // Plain text
    ///</summary>
    public string Subject { get; set; }
    
    /// <summary>
    /// e..g. Airline name
    /// </summary>
    public string ServiceProvider { get; set; }
}