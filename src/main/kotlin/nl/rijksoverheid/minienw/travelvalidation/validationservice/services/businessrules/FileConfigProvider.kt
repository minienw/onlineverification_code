package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Component
import java.io.File

@Deprecated("Use http get instead.")
class FileConfigProvider : IStringReader
{
    private val values = HashMap<String,String>()
    private val appConfig: IApplicationSettings

    override fun read(name: String) = values[name]!!

    constructor(appConfig: IApplicationSettings)
    {
        this.appConfig = appConfig
        val logger = LogManager.getLogger(FileConfigProvider::class.java)
        logger.info("CURRENT PATH ----> " + File(".").absoluteFile)
        refresh()
    }

    private fun readFile(name:String) = File(appConfig.configFileFolderPath, name).readText(Charsets.UTF_8)

    fun refresh() {
        values["config"] = readFile("config.json")
        values["business_rules"] = readFile("business_rules.json")
        values["value_sets"] = readFile("value_sets.json")
        values["custom_business_rules"] = readFile("custom_business_rules.json")
    }
}