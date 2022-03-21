package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import java.time.Instant

interface IDateTimeProvider
{
    //Careful have to make this per request!
    fun snapshot() : Instant
}