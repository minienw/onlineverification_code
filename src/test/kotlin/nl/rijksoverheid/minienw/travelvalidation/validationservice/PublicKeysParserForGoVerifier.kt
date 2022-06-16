package nl.rijksoverheid.minienw.travelvalidation.validationservice

import com.google.gson.Gson
import com.google.gson.JsonParser
import org.junit.Assert
import org.junit.jupiter.api.Test
import java.io.File

class PublicKeysParserForGoVerifier {

    @Test
    fun create() {
            val json = File("build\\resources\\main\\test\\public_keys.json").readText()
            val euKeysOnly = JsonParser.parseString(json).asJsonObject.get("eu_keys")
            val jsonOut = Gson().toJson(euKeysOnly)
            Assert.assertTrue(jsonOut.startsWith("{\"Y"))
    }
}
