using System.Text.Json.Serialization;

namespace EuDecorator.Controllers.Dtos
{

    /// <summary>
    /// 3.8.3
    /// The validation initialisation response delivers a JSON with a unique subject ID, which identifies the
    /// occurrence in the validation service.Next to the unique subject, a public key and an expiration date
    /// for the occurrence are attached. The public key must be unique for each subject for maximum data
    /// privacy.
    /// </summary>
    public class ValidationInitializeResponse
    {
        /// <summary>
        /// hexadecimal-encoded value
        /// Traveller or whole trip?
        /// </summary>
        [JsonPropertyName("sub")]
        public string SubjectId { get; set; }

        /// <summary>
        /// Unix epoch time
        /// </summary>
        [JsonPropertyName("exp")]
        public int WhenExpires { get; set; }

        /// <summary>
        /// E.g. https://validationprovider/validate/{subject}
        /// TODO odd name
        /// </summary>
        [JsonPropertyName("aud")]
        public int ValidationUrl { get; set; }
        
        /// <summary>
        /// "Optional Public Key for Encryption of Validation Service."
        /// NOTE: This key should only be used in a Backend-Backend Validation.
        /// A wallet should not use the given key provided by the service provider backend, because this one can be intercepted.
        /// 
        /// Hence this is NOT the key to use to encrypt DDCs...
        /// </summary>
        [JsonPropertyName("encKey")]
        public JsonWebKeyRfc7517 ValidationServiceEncryptionKey { get; set; }

        /// <summary>
        /// "Optional Public Key for Signing of Validation Service."
        /// See encKey...
        /// </summary>
        [JsonPropertyName("signKey")]
        public JsonWebKeyRfc7517 ValidationServiceSigningKey { get; set; }
    }
}
