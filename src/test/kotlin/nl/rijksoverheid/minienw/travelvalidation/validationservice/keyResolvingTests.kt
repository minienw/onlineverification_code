package nl.rijksoverheid.minienw.travelvalidation.validationservice

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.FileAirlineSigningKeyProvider
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class keyResolvingTests {
    @Test
    fun keyResolving()
    {
        val appSettings: IApplicationSettings = Mockito.mock(IApplicationSettings::class.java)
        Mockito.`when`(appSettings.configFileFolderPath).thenReturn("src/main/resources/dev")
        var stash = FileAirlineSigningKeyProvider(appSettings)
        assert(stash.get("argle", "ROT13") == null)
        assert(stash.get("SsXyRIVSy4Y=","RS256") != null)
    }
}