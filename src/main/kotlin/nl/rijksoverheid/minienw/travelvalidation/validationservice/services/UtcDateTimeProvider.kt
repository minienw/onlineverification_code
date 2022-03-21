package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import org.springframework.stereotype.Component
import java.time.Instant


@Component
//TODO should be a 'per request bean' and a snapshot of the time
class UtcDateTimeProvider : IDateTimeProvider
{
    override fun snapshot(): Instant = Instant.now()
}