namespace EuDecorator.Controllers.Dtos;

public class Trip
{
    /// <summary>
    /// ISO8601 with offset
    /// </summary>
    public string WhenArriving { get; set; }
    
    /// <summary>
    /// Country codes
    /// This is currently simplified down to the ultimate destination
    /// TODO what is the intention of the business rules engine in the medium term?
    /// </summary>
    public string[] Destinations { get; set; }

    /// <summary>
    /// Country codes
    /// Equivalent to Issuing Country
    /// </summary>
    public string Origin { get; set; }
}