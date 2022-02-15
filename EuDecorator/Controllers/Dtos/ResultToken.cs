namespace EuDecorator.Controllers.Dtos;

/// <summary>
/// 3.8.5
/// The result token is generated from the validation service to acknowledge the validation associated
/// with one subject.The booking system can use it to validate/present the result to the frontend.Within    
/// the result token is a confirmation token which can be used for archiving the validation result or for
/// linking the validation result to the issued ticket by using the included unique jti identifier 3.8.4.3 <see cref="ValidationAccessTokenPayload.TokenIdentifier"/>.
/// </summary>
public class ResultToken
{
    public ResultTokenHeader Header { get; set; }
    public ResultTokenPayload Payload { get; set; }
}