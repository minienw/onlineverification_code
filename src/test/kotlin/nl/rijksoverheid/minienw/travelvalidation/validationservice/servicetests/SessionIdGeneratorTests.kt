package nl.rijksoverheid.minienw.travelvalidation.validationservice.servicetests

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.ValidationServicesSubjectIdGenerator
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SubjectIdGeneratorTests {

    @ParameterizedTest
    @ValueSource(strings = ["0123456789ABCDEF0123456789ABCDEF"
        //, "23333333333333"
    ])
    fun valid(value: String) {
        var actual = ValidationServicesSubjectIdGenerator().validate(value)
        assert(actual.isEmpty())
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "1", "0123456789ABCDEF0123456789ABCDE", "0123456789ABCDEF0123456789ABCDEX" ])
    fun invalid(value: String) {
        var actual = ValidationServicesSubjectIdGenerator().validate(value)
        assert(!actual.isEmpty())
    }
}



