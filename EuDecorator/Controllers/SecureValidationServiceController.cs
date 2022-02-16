using System.Net.Mime;
using EuDecorator.Controllers.Dtos;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace EuDecorator.Controllers
{
    /// <summary>
    /// 3.10.2
    ///The identity document endpoint delivers a json description of public keys and service endpoints.This endpoint should be publicly available.
    /// TODO need a reason for this
    /// GET https://serviceprovider/identity/service/AccessCredentialService#AccessTokenService-1
    /// GET https://serviceprovider/identity/verificationMethod/ValidationServiceSignKey#ValidationServiceSignKey-1
    /// </summary>
    [Route("api/[controller]")]
    [ApiController]
    public class SecureValidationServiceController : ControllerBase
    {
        private const string IdentityPrefix = "identity/";

        [HttpGet(IdentityPrefix)]
        [Produces(MediaTypeNames.Application.Json)]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public ActionResult<IdentityResponse> Identity([FromHeader(Name = "X-Version")] string xVersion,
            [FromHeader(Name = "Accept")] string accept)
        {
            throw new NotImplementedException();
        }

        [HttpGet(IdentityPrefix+"{element}")]
        [Produces(MediaTypeNames.Application.Json)]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public ActionResult<IdentityResponse> Identity([FromHeader(Name = "X-Version")] string xVersion,
            [FromHeader(Name = "Accept")] string accept, [FromRoute] string element)
        {
            throw new NotImplementedException();
        }

        [HttpGet(IdentityPrefix + "{element}/{type}")]
        [Produces(MediaTypeNames.Application.Json)]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public ActionResult<IdentityResponse> Identity([FromHeader(Name = "X-Version")] string xVersion,
            [FromHeader(Name = "Accept")] string accept, [FromRoute] string element, [FromRoute] string type)
        {
            throw new NotImplementedException();
        }

        [HttpGet(IdentityPrefix + "{element}/{type}#{id}")]
        [Produces(MediaTypeNames.Application.Json)]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public ActionResult<IdentityResponse> Identity([FromHeader(Name = "X-Version")] string xVersion,
            [FromHeader(Name = "Accept")] string accept, [FromRoute] string element, [FromRoute] string type,
            [FromRoute] string id)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// 3.10.2.2 DCC Validation Initialisation Endpoint
        /// </summary>
        /// <param name="accept"></param>
        /// <param name="contentType"></param>
        /// <param name="xVersion"></param>
        /// <param name="encrypt"></param>
        /// <param name="sign"></param>
        /// <param name="body"></param>
        /// <returns></returns>
        /// <exception cref="NotImplementedException"></exception>
        //PUT /initialize/{subject}
        [HttpPost("initialize/{subject}")] //Cannot be an update as this is the start -. Changed to POST
        [Produces(MediaTypeNames.Application.Json)]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        public ActionResult<ValidationInitializeResponse> Initialize(
            [FromHeader(Name = "X-Version")] string xVersion,
            [FromHeader(Name = "Accept")] string accept,
            [FromHeader(Name = "Content-Type")] string contentType,
            [FromHeader(Name = "X-Crypto-Enc")] bool encrypt, //Optional, boolean, Send Encryption JWK in Response
            [FromHeader(Name = "X-Crypto-Sign")] bool sign, //Optional, boolean, Send Signed JWK in Response
            [FromBody] ValidationInitializeRequestBody body)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// 3.10.2.4 DCC Provision Endpoint
        /// The provision endpoint is the public endpoint where DCCs can be provided for a subject. The endpoint receives the encrypted DCC, validates it and creates the result for the subject.
        /// </summary>
        /// <param name="accept"></param>
        /// <param name="xVersion"></param>
        /// <returns></returns>
        /// <exception cref="NotImplementedException"></exception>
        [HttpPost("validate/{subject}")]
        [Produces(MediaTypeNames.Application.Json)]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status410Gone)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)] //Thought this was 401...
        public ActionResult<ResultToken> Status(
            [FromHeader(Name = "X-Version")] string xVersion,
            [FromHeader(Name = "Accept")] string accept,
            [FromHeader(Name = "Authorization")] string authorization,
            [FromRoute] string subject,
            [FromBody] ValidateRequestBody body
        )
        {
            throw new NotImplementedException();
        }
    }
}


