using System.Net.Mime;
using EuDecorator.Controllers;
using EuDecorator.Controllers.Dtos;

namespace Wallet
{

    /// <summary>
    /// V2
    /// </summary>
    public class DccFormatterV2
    {
        public string Encode(string[] dccs) => throw new NotImplementedException();
    }

    public class Wallet
    {
        private const string VersionHeaderValueV1 = "1.0";
        private const string VersionHeaderValueV2 = "2.0";

        private void GenerateRandomNonce()
        {
            var nonce = new byte[16];
            new Random().NextBytes(nonce);
            _Nonce = Convert.ToBase64String(nonce);
        }
        private void GenerateWalletKeys()
        {
            //e.g. ecdsa.SigningKey.generate(curve=NIST256p,hashfunc=SHA256.new)
        }

        //Wallet session info
        private string _HardCodedValidationServiceUrl;
        private string _WalletPublicKey;
        private string _WalletPublicKeyAlgorithm; //TODO choices?
        private string _WalletPrivateKey;
        private string _Nonce;

        //TODO Check sigs after each stage
        public void Main(InitializationQrCodeContent qrCode)
        {
            var validationAccessToken = TravllerObtainsQrCodeThenTripInfo(qrCode);
            var validationInitializeResponse = StartValidation(validationAccessToken);
            var dccs = new string[2]; //Obtain Dccs...
            var validationResult = Validate(dccs, validationInitializeResponse, validationAccessToken);
            NotifyAirline(validationAccessToken.Header.ResultCallBackUri, dccs, validationResult);
        }

        public ValidationAccessToken TravllerObtainsQrCodeThenTripInfo(InitializationQrCodeContent qrCode)
        {
            //TODO nothing needed from the Identity endpoints?

            GenerateWalletKeys(); //Surely key pair...

            return new AirlineDecoratorController().GetValidationAccessTokenAndTripInfo(qrCode.SubjectToken,
                MediaTypeNames.Application.Json,
                MediaTypeNames.Application.Json,
                VersionHeaderValueV1,
                new TokenRequestBody
                {
                    Service = _HardCodedValidationServiceUrl,
                    WalletPublicKey = _WalletPublicKey
                }
            ).Value;
        }

        /// <summary>
        /// Originally intended to 
        /// </summary>
        /// <param name="validationAccessToken"></param>
        /// <returns></returns>
        public ValidationInitializeResponse StartValidation(ValidationAccessToken validationAccessToken)
        {
            GenerateRandomNonce();

            var validationInitializeRequestBody = new ValidationInitializeRequestBody
            {
                WalletPublicKey = _WalletPublicKey,
                Algorithm = _WalletPublicKeyAlgorithm,
                Nonce = _Nonce
            };

            return new SecureValidationServiceController().Initialize(VersionHeaderValueV1,
                MediaTypeNames.Application.Json, MediaTypeNames.Application.Json, false, false,
                validationInitializeRequestBody).Value!;
        }

        public ResultToken Validate(string[] dccs, ValidationInitializeResponse validationServiceSession, ValidationAccessToken validationAccessToken)
        {
            var encodedDccs = new DccFormatterV2().Encode(dccs);

            var validateRequestBody = new ValidateRequestBody
            {
                EncryptedDcc = EncryptDcc(encodedDccs, validationServiceSession.ValidationServiceEncryptionKey),
                EncryptionKeyId = validationServiceSession.ValidationServiceEncryptionKey.KeyId,

                EncryptedDccSignature = EncryptDcc(encodedDccs, validationServiceSession.ValidationServiceSigningKey),
                EncryptedDccSignatureAlgorithm = validationServiceSession.ValidationServiceSigningKey.Algorithm, //Where?
                
                //EncryptionKey = "Ignored for asymmetric encryption"
            };

            var validationUrl = validationServiceSession.ValidationUrl; //TODO set this in httpRequest
            //NB V2
            return new SecureValidationServiceController()
                .Status(
                    VersionHeaderValueV2, 
                    MediaTypeNames.Application.Json,
                    validationAccessToken.Payload.ToString(), 
                    validationAccessToken.Payload.Subject, 
                    validateRequestBody
                    ).Value;
        }

        private void NotifyAirline(string callbackUrl, string[] dccs, ResultToken validationResult)
        {
            //extract DisclosedPersonalData from each DCC and check they all match.
            var disclosedPersonalData = GetThingFromDccs(dccs);
            
            //TODO how much of ResultTokenPayload is required by airlines?
            var body = new AirlineResultToken
            {
                //TODO subject or it that in the URL?
                //TODO anything else?
                ConfirmationToken = validationResult.Payload.Confirmation,
                DisclosedPersonalData = disclosedPersonalData
            };

            //URL from callbackUrl
            //TODO authentication? Same as 
            //Probably should be V2?
            new AirlineDecoratorController()
                .Callback(
                    VersionHeaderValueV1, 
                    MediaTypeNames.Application.Json, 
                    validationResult.Payload.Subject, 
                    body
                    );
        }

        private DisclosedPersonalData GetThingFromDccs(string[] dccs)
        {
            throw new NotImplementedException();
        }

        //var airlineController = new AirlineDecoratorController();
        //    var airlineControllerCallbackUrl = qrCode.CallbackUri;
        //    airlineController.Callback(MediaTypeNames.Application.Json, MediaTypeNames.Application.Json, qrCode.SubjectToken, resultToken);
        //}

        /// <summary>
        /// Return base64 encoding of resulting byte array
        /// </summary>
        private string SignDcc(string dcc, JsonWebKeyRfc7517 signingKey)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Return base64 encoding of resulting byte array
        /// </summary>
        private string EncryptDcc(string dcc, JsonWebKeyRfc7517 encryptionKey)
        {
            throw new NotImplementedException();
        }
    }
}