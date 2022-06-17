package nl.rijksoverheid.minienw.travelvalidation.validationservice

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*


class SubjectIdTests() {

    @Test
    fun rawUuid() {
        UUID.fromString("93b9bf08-3123-42e6-8369-b895fee24d6c")
    }

    @Test
    fun uuid() {
        val result = ValidationServicesSubjectIdGenerator().validate("93b9bf08-3123-42e6-8369-b895fee24d6c")
        assert(result.isEmpty())
    }

    @Test
    fun uuidBadFormat() {
        val result = ValidationServicesSubjectIdGenerator().validate("93b9bf08-3123-42e6-8369b895fee24d6c")
        assert(result.isNotEmpty())
    }

    @Test
    fun hex() {
        val result = ValidationServicesSubjectIdGenerator().validate("93b9bf08312342e68369b895fee24d6c")
        assert(result.isEmpty())
    }

    @Test
    fun hexLength() {
        val result = ValidationServicesSubjectIdGenerator().validate("93b9bf08312342e68369b895fee24d")
        Assertions.assertTrue(result[0].contains("length") || result[1].contains("length"))
        Assertions.assertTrue(result[0].contains("format") || result[1].contains("format"))
        Assertions.assertEquals(2, result.size)
    }

    @Test
    fun hexBadChar() {
        val result = ValidationServicesSubjectIdGenerator().validate("93b9bf08312342e68369b895fee24dxx")
        Assertions.assertEquals(2, result.size)
        Assertions.assertTrue(result[0].contains("character") || result[1].contains("format"))
        Assertions.assertTrue(result[0].contains("format") || result[1].contains("format"))
    }
}
