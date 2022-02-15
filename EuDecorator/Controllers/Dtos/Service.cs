namespace EuDecorator.Controllers.Dtos;

/// <summary>
/// 3.8.2.3 Service
/// </summary>
public class Service
{

    /*
    * ValidationService  | Validation Service | URL of the Validation Service protected by “ValidationServiceKey-x”
    * AccessTokenService | Validation Decorator | URL of the Access Token Service Endpoint protected by “AccessTokenServiceKey-x”
    * ServiceProvider  | Validation Decorator  | URL of the Service Provider protected by “ServiceProviderKey-x”
    * CancellationService  | Validation Decorator | URL of the Cancellation Endpoint protected by “CancellationServiceKey-x”
    * StatusService  | Validation Decorator | URL of the Status Endpoint protected by“StatusServiceKey-x”
    */

    /// <summary>
    /// ID of the Endpoint(must resolve to a Identity Document)
    /// All id values have to be prefixed by {serviceproviderurl} and a fragment value, e.g. https://servicepovider/service#AccessCredentialService
    /// </summary>
    public string Id { get; set; }

    /// <summary>
    /// One of first column from above table
    /// </summary>
    public string Type { get; set; }

    /// <summary>
    /// Actual Uri
    /// </summary>
    public string ServiceEndPoint { get; set; }

    /// <summary>
    /// "Human readable"
    /// </summary>
    public string Name { get; set; }
}