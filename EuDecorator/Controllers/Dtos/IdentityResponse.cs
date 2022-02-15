using System.Text.Json.Serialization;

namespace EuDecorator.Controllers.Dtos
{

    public class IdentityResponse
    {
        [JsonPropertyName("id")] public string Id { get; set; }

        /// <summary>
        /// TODO name should be plural
        /// </summary>
        [JsonPropertyName("verificationMethod")]
        public VerificationMethod[] VerificationMethods { get; set; }

        /// <summary>
        /// TODO name should be plural
        /// </summary>
        [JsonPropertyName("service")]
        public Service[] Services { get; set; }
    }
}