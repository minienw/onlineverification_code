using System.Net.Mime;
using EuDecorator.Controllers.Dtos;
using Microsoft.AspNetCore.Mvc;

namespace EuDecorator.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class AirlineDecoratorController : ControllerBase
    {
        /// <summary>
        /// Creates the QR Code - not seen in the outside world!!!!
        /// 3.10.1.1 QR Code delivery Endpoint
        /// UNCHANGED - <see cref="/token response for..."/>
        /// </summary>
        /// <param name="subject">'Case Id' Is this a JWT or JSON cos it also has </param>
        /// <returns></returns>
        /// <exception cref="NotImplementedException"></exception>
        [HttpGet(nameof(Initialize) + "/{subject}")]
        [Produces(MediaTypeNames.Application.Json)]
        [ProducesResponseType(StatusCodes.Status200OK)]
        //[ProducesResponseType(StatusCodes.Status204NoContent, Type = typeof(ResultToken))] //TODO cut/paste error? 
        [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(InitializationQrCodeContent))] //TODO cut/paste error? //Mangled
        [ProducesResponseType(StatusCodes.Status401Unauthorized, Type = typeof(InitializationQrCodeContent))] //TODO cut/paste error? //Signing bad
        [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(InitializationQrCodeContent))]
        [ProducesResponseType(StatusCodes.Status500InternalServerError, Type = typeof(InitializationQrCodeContent))]
        public ActionResult<InitializationQrCodeContent> Initialize(
            [FromHeader(Name = "X-Version")] string xVersion,
            [FromRoute] string subject)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// 3.10.1.3 Access Token Endpoint
        /// The access token endpoint is triggered from the user after the consent to the transfer process with a public key of the user and the access token which was received by the qr code session content.
        /// </summary>
        /// <param name="authorizationHeader">Token from the QR Session Content <see cref="InitializationQrCodeContent.ValidationDecoratorAccessToken"/> from call to POST <see cref="Initialize"/></param>
        /// <param name="body"></param>
        /// <returns></returns>
        /// <exception cref="NotImplementedException"></exception>
        [HttpPost("Token")]
        [Consumes("application/jwt")]
        [ProducesResponseType(typeof(ValidationAccessToken), StatusCodes.Status200OK, MediaTypeNames.Application.Json)] //Access token - not sure how this is JSON
        [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(string))] //Mangled
        [ProducesResponseType(StatusCodes.Status401Unauthorized, Type = typeof(string))] //Signing bad
        [ProducesResponseType(StatusCodes.Status410Gone, Type = typeof(string))]
        [ProducesResponseType(StatusCodes.Status500InternalServerError, Type = typeof(string))]
        public ActionResult<ValidationAccessToken> GetValidationAccessTokenAndTripInfo(
            [FromHeader(Name = "Authorization")] string authorizationHeaderJwt, //
            [FromHeader(Name = "Content-Type")] string contentTypeHeader, //Must be json
            [FromHeader(Name = "Accept")] string acceptHeader, //Must be json
            [FromHeader(Name = "X-Version")] string xVersion, 
            [FromBody] TokenRequestBody body)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="authorizationHeader">Token from the QR Session Content <see cref="InitializationQrCodeContent.ValidationDecoratorAccessToken"/> </param>
        /// <returns></returns>
        /// <exception cref="NotImplementedException"></exception>
        [Obsolete("Out of scope for now.")]
        [HttpGet(nameof(Reject))]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)] //TODO cut/paste error? //Signing bad
        public ActionResult Reject(
            [FromHeader(Name = "Authorization")] string authorizationHeader, //Token from the QR Session Content
            [FromHeader(Name = "Accept")] string acceptHeader, //Must be json
            [FromHeader(Name = "X-Version")] string xVersion)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// 3.10.1.5 Validation Status Endpoint
        /// The request body contains the structure described in data structures section 3.8.5
        /// </summary>
        /// <param name="authorizationToken"></param>
        /// <returns></returns>
        /// <exception cref="NotImplementedException"></exception>
        [Obsolete("This is result polling which cannot be performed by the airlines and should not be necessary for a wallet implementation.")]
        [HttpGet(nameof(Status))]
        [Produces(MediaTypeNames.Application.Json)] 
        [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(ResultToken))]
        [ProducesResponseType(StatusCodes.Status204NoContent, Type = typeof(ResultToken))] //TODO cut/paste error? 
        //[ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(ResultToken))] //TODO cut/paste error? //Mangled
        [ProducesResponseType(StatusCodes.Status401Unauthorized, Type = typeof(ResultToken))] //TODO cut/paste error? //Signing bad
        [ProducesResponseType(StatusCodes.Status410Gone, Type = typeof(ResultToken))]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public ActionResult<ResultToken> Status(
                [FromHeader(Name = "Authorization")] string authorizationHeader, //Token from the QR Session Content
                [FromHeader(Name = "Accept")] string acceptHeader, //Must be json
                [FromHeader(Name = "X-Version")] string xVersion)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// 3.10.1.6 Callback Status Endpoint
        /// Use this instead of GET Status
        /// </summary>
        /// <param name="subject"></param>
        /// <param name="xVersion"></param>
        /// <param name="body">Jwt</param>
        /// <returns></returns>
        /// <exception cref="NotImplementedException"></exception>
        [HttpPut(nameof(Callback) + "/{subject}")] //Debatable is this is strictly an Update operation
        [Consumes(typeof(ResultToken), "application/jwt")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        //[ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(string))] //Mangled
        [ProducesResponseType(StatusCodes.Status401Unauthorized, Type = typeof(string))] //Signing bad
        [ProducesResponseType(StatusCodes.Status410Gone, Type = typeof(string))] //Cannot identify trip/traveller - difference between 404 and 410?
        //[ProducesResponseType(StatusCodes.Status500InternalServerError, Type = typeof(string))]
        public ActionResult Callback(
            //TODO No authorization token???? or just parse the confirmation token?
            [FromHeader(Name = "X-Version")] string xVersion,
            [FromHeader(Name = "Content-Type")] string contentTypeHeader, //Must be json
            [FromRoute] string subject, 
            [FromBody] AirlineResultToken body)
        {
            throw new NotImplementedException();
        }
    }
}