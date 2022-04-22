package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import org.bouncycastle.util.encoders.Base64
import org.springframework.stereotype.Component
import java.io.File


@Component
class FileWriterPublicKeyProvider(
    val appSettings: IApplicationSettings,
    val getter: IStringReader
) : IPublicKeysProvider
{
	override fun refresh() {
        val json = decode(getter.read("public_keys"))
        val euKeysOnly = JsonParser.parseString(json).asJsonObject.get("eu_keys")
        val jsonOut = Gson().toJson(euKeysOnly)
        File(appSettings.publicKeysFileName).writeText(jsonOut)
	}

    //Unsh**
    private fun decode(encoded: String): String = String(Base64.decode(encoded), Charsets.UTF_8)

}