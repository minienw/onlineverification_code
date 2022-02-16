namespace EuDecorator.Controllers.Dtos;

/// <summary>
/// Bits would have made more sense...
/// </summary>
public enum ValidationType
{
    /// <summary>
    /// Validates just the Content of the DCC(Schema, Values, CBOR Structure)
    /// </summary>
    Structure = 0,

    /// <summary>
    /// Structure Validation + Signature Validation
    /// </summary>
    Cryptographic = 1,

    /// <summary>
    /// Structure Validation + Cryptographic + Business Rule Check(condition structure necessary)
    /// </summary>
    Full = 2,
        
    [Obsolete("Steffen's not in scope yet but additionally Perform ID check")]
    FullWithIdCheck = 4
}