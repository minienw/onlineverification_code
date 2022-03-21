package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

interface ISessionRepository
{
    fun save(sessionInfo: SessionInfo)
    fun find(subject: String) : SessionInfo? //Yuk... where is the try/find pattern in this language :(
    fun remove(subject: String) //Set expiry and explicity remove if VAT has expired.
}

