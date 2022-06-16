package nl.rijksoverheid.minienw.travelvalidation.validationservice.servicetests

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.HexStringEncoding
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class HexEncoderTests {

    @ParameterizedTest
    @ValueSource(strings = ["01", "23333333333333"])
    fun valid(value: String) {
        assert(HexStringEncoding.decode(value).isNotEmpty())
    }

    @Test
    fun roundTrip()
    {
        for(i in 0..1000) {
            val buffer = ByteArray(10)
            Random().nextBytes(buffer)
            val encoded = HexStringEncoding.encode(buffer)
            val actual = HexStringEncoding.decode(encoded)
            assertArrayEquals(buffer, actual)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "a", "bbb", "qwerty"])
    fun invalid(value: String) {
        Assertions.assertThrowsExactly(NumberFormatException::class.java, { HexStringEncoding.decode(value) }, "Barf!")
    }
}