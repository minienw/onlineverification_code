package nl.rijksoverheid.minienw.travelvalidation.validationservice

import com.google.gson.Gson
import nl.rijksoverheid.minienw.travelvalidation.api.data.PublicKeyJwk
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.AirlineKeys
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IAirlineSigningKeyProvider
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.slf4j.LoggerFactory
import java.io.File

class keyResolvingTests {
    @Test
    fun keyResolving() {
        val appSettings: IApplicationSettings = Mockito.mock(IApplicationSettings::class.java)
        Mockito.`when`(appSettings.configFileFolderPath).thenReturn("src/main/resources/dev")
        val stash = LocalFileAirlineSigningKeyProvider(appSettings)
        assert(stash.get("argle", "ROT13") == null)
        assert(stash.get("SsXyRIVSy4Y=", "RS256") != null)
    }
}

//@Qualifier("local")
//@Component
@Deprecated("Moved out of main project due to bean collision.")
class LocalFileAirlineSigningKeyProvider : IAirlineSigningKeyProvider {
    private val items: AirlineKeys

    constructor(appSettings: IApplicationSettings) {
        LoggerFactory.getLogger("argle").info("-----------> " + File(".").absolutePath)
        val configContents = File(appSettings.configFileFolderPath, "airlinePublicKeys.json").readText(Charsets.UTF_8)
        items = Gson().fromJson(configContents, AirlineKeys::class.java)
    }

    override fun get(keyId: String?, algorithm: String?): PublicKeyJwk? {
        return items.keys.find { it.key.kid == keyId && it.key.alg == algorithm }?.key
    }

    override fun refresh() {
        //Nothing
    }
}
