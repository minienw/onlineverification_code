using System.Text.Json.Serialization;

namespace EuDecorator.Controllers.Dtos;

/// <summary>
/// 3.8.4.3 Validation Access Token Payload Condition Structure
/// The validation condition structure is embedded in the validation access token to fulfil two things:
/// a) The validation service knows the selected conditions by the service provider/validation service user
/// b) The wallet app can select an appropriate certificate for the user with reference to the conditions
/// </summary>
public class ValidationAccessTokenPayloadCondition
{
    /// <summary>
    /// TODO Hex or base64?
    /// Not applicable for Type 1,2
    /// </summary>
    [JsonPropertyName("hash")]
    public string DccHash { get; set; }

    /// <summary>
    /// ISO 639-1 standard language codes???
    /// For all
    /// </summary>
    [JsonPropertyName("lang")]
    public string Language { get; set; }

    /// <summary>
    /// ICAO 9303 transliterated
    /// For 1 and 2
    /// </summary>
    [JsonPropertyName("fnt")]
    public string SurnameTransliterated { get; set; }

    /// <summary>
    /// ICAO 9303 transliterated
    /// For 1 and 2
    /// </summary>
    [JsonPropertyName("gnt")]
    public string GivenNameTransliterated { get; set; }

    /// <summary>
    /// Various formats:
    /// 1979-04-14 or 1901-08 or 1950
    /// For 1 and 2
    /// </summary>
    [JsonPropertyName("dob")]
    public string DateOfBirth{ get; set; }

    /// <summary>
    /// ISO 3166-1 alpha-2
    /// For 2
    /// e.g. NL
    /// </summary>
    [JsonPropertyName("coa")]
    public string CountryOfArrival { get; set; }

    /// <summary>
    /// ISO 3166-1 alpha-2
    /// For 2
    /// e.g. NL
    /// </summary>
    [JsonPropertyName("cod")]
    public string CountryOfDeparture { get; set; }

    /// <summary>
    /// ISO 3166-2 without Country
    /// For 2
    /// e.g. NL
    /// </summary>
    [JsonPropertyName("roa")]
    public string RegionOfArrival { get; set; }

    /// <summary>
    /// ISO 3166-2 without Country
    /// For 2
    /// e.g. NL
    /// </summary>
    [JsonPropertyName("rod")]
    public string RegionOfDeparture { get; set; }

    /// <summary>
    /// Type of DCC
    /// For 0,1,2
    /// Values v, t, r, tp, tr
    /// </summary>
    [JsonPropertyName("type")]
    public string[] Type { get; set; }

    /// <summary>
    /// e.g. Inter-Flight, Concert, Domestic, MassEvent > 1000, etc.
    /// Category which  shall be reflected in the Validation by additional rules/logic. If null, Standard Business Rule Check will apply.
    /// Default: “Standard”
    /// For 2
    /// </summary>
    [JsonPropertyName("category")]
    public string[] Category { get; set; }

    /// <summary>
    /// Date where the DCC must be validatable.
    /// ISO8601 with time and offset e.g. 2021-01-29T12:00:00+01:00
    /// For 1,2
    /// </summary>
    [JsonPropertyName("validationClock")] //Who comes up with these names?
    public string[] WhenMustBeValid { get; set; }

    /// <summary>
    ///  DCC must be valid from this date.
    /// ISO8601 with time and offset e.g. 2021-01-29T12:00:00+01:00
    /// For 0,1,2
    /// </summary>
    [JsonPropertyName("validfrom")]
    public string[] WhenValidStart { get; set; }


    /// <summary>
    /// DCC must be valid minimum to this date.
    /// ISO8601 with time and offset e.g. 2021-01-29T12:00:00+01:00
    /// For 0,1,2
    /// </summary>
    [JsonPropertyName("validTo")]
    public string[] WhenValidEnd { get; set; }
}