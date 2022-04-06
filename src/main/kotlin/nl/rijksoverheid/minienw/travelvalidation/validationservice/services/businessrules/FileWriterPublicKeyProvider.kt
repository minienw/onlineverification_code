package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import org.springframework.stereotype.Component
import java.io.File

@Component
class FileWriterPublicKeyProvider(
    val appSettings: IApplicationSettings,
    val getter: IStringReader
) : IPublicKeysProvider
{
	override fun refresh() {
        File(appSettings.publicKeysFileName).writeText(getter.read("public_keys"))
	}
}