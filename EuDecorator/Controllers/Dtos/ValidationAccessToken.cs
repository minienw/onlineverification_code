namespace EuDecorator.Controllers.Dtos;

public class ValidationAccessToken
{
    public ValidationAccessTokenHeader Header { get; set; }
    public ValidationAccessTokenPayload Payload { get; set; }
}