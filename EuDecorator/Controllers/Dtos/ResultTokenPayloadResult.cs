namespace EuDecorator.Controllers.Dtos;

/// <summary>
/// 3.8.5.3
/// <see cref="ResultTokenPayload.Results" />
/// </summary>
public class ResultTokenPayloadResult
{
    /// <summary>
    /// Identifier of the check (rule?)
    /// e.g.VR-0001, CBOR, SIGNATURE etc.
    /// </summary>
    public string Identifier { get; set; }

    /// <summary>
    /// Result of the check
    /// OPEN (CHK)
    /// FAILED (NOK)
    /// PASSED (OK)
    /// </summary>
    public string Result { get; set; }

    /// <summary>
    /// Type of the check
    /// Values:
    /// Technical Check
    /// Issuer Invalidation
    /// Destination Acceptance
    /// Traveller Acceptance
    /// </summary>
    public string Type { get; set; }

    /// <summary>
    /// Description of the checkup
    /// </summary>
    public string Details { get; set; }
}