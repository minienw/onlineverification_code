using System.Text.Json.Serialization;

namespace EuDecorator.Controllers.Dtos
{
    public class DisclosedPersonalData
    {
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
        public string DateOfBirth { get; set; }
    }
}
