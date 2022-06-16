package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

@Component
class ValidationServicesSubjectIdGenerator {
    fun next():String {
        return HexStringEncoding.encode(Random.nextBytes(16))
    }

    fun validate(value:String) : List<String>
    {
        val tryHex = validateHexEncoding(value)
        if (tryHex.isEmpty())
            return listOf()

        val tryGuid = validateGuidEncoding(value)
        if (tryGuid.isEmpty())
            return listOf()

        return tryHex + tryGuid
    }

    private fun validateHexEncoding(value:String): List<String> {
        try {
            UUID.fromString(value)
            return listOf()
        } catch (exception: IllegalArgumentException) {
            return listOf("Not a valid standard GUID format.")
        }
    }

    private fun validateGuidEncoding(value:String): List<String> {
        val result = ArrayList<String>()
        try {
            if (HexStringEncoding.decode(value).size != 16)
                result.add("Incorrect length of hex format.")
        }
        catch (ex: NumberFormatException)
        {
            result.add("Incorrect hex format.")
            return result
        }
        return result
    }
}