package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import dgca.verifier.app.engine.data.Rule
import dgca.verifier.app.engine.data.source.remote.rules.RuleRemote
import dgca.verifier.app.engine.data.source.remote.rules.toRules
import nl.rijksoverheid.dcbs.verifier.models.CountryColorCode
import nl.rijksoverheid.dcbs.verifier.models.CountryRisk
import nl.rijksoverheid.dcbs.verifier.models.CountryRiskPass
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.io.File


@Component
@Qualifier("Bottom")
class FileBusinessRulesConfig : IBusinessRulesConfig
{
    constructor(appConfig: IApplicationSettings)
    {
        val logger = LogManager.getLogger(FileBusinessRulesConfig::class.java)
        logger.info("CURRENT PATH ----> " + File(".").absoluteFile)

        val fileFolder = appConfig.configFileFolderPath

        val configContents = File(fileFolder,"config.json").readText(Charsets.UTF_8) //https://unshit.nl/?shit=https://verifier-api.coronacheck.nl/v4/dcbs/config //contains a top level element countryColours
        val business_rulesContents = File(fileFolder, "business_rules.json").readText(Charsets.UTF_8) //https://unshit.nl/?shit=https://verifier-api.coronacheck.nl/v4/dcbs/business_rules
        valueSetsJson = File(fileFolder, "value_sets.json").readText(Charsets.UTF_8) //https://unshit.nl/?shit=https://verifier-api.coronacheck.nl/v4/dcbs/value_sets
        val custom_business_rulesContents = File(fileFolder, "custom_business_rules.json").readText(Charsets.UTF_8) //https://unshit.nl/?shit=https://verifier-api.coronacheck.nl/v4/dcbs/custom_business_rules

        //Keep these in memory

        countryRisks = parseCountries(configContents)

        val mapper = ObjectMapper()
        mapper.findAndRegisterModules() //TODO Replace with Gson?
        val remoteRules = mapper.readValue(business_rulesContents, object : TypeReference<List<RuleRemote>>() {}).toRules()
        val customBusinessRules = mapper.readValue(custom_business_rulesContents, object : TypeReference<List<RuleRemote>>() {}).toRules()
        rules = listOf(remoteRules, customBusinessRules).flatten()
    }

    private var isOtherIncluded: Boolean = true //TODO where is this set?

    override val rules: List<Rule>
    override val valueSetsJson: String
    override val countryRisks: List<CountryRisk>

    private fun parseCountries(appConfig: String): List<CountryRisk> {
        val obj: JsonObject = Gson().fromJson(appConfig, JsonObject::class.java)
        val jsonString: JsonElement = obj.get("countryColors")
        val type = object : TypeToken<List<CountryRisk>>() {}.type
        val countries = Gson().fromJson<ArrayList<CountryRisk>>(jsonString, type)
        if (isOtherIncluded) {
            countries.add(getOther())
        }
        return countries
    }

    private fun getOther(): CountryRisk {
        val other = "Others" //or anders...
        return CountryRisk(
            other,
            other,
            other,
            other,
            other,
            CountryColorCode.GREEN.value,
            CountryRiskPass.Inconclusive.value,
            isColourCode = false,
            ruleEngineEnabled = false,
            isEU = false
        )
    }
}