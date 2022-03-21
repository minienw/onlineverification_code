package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.dccverification

//Did not work for any reason
class DccVerificationException : Exception
{
    constructor(message: String) : super(message)
    constructor(message: String, e: Exception): super(message, e)
}