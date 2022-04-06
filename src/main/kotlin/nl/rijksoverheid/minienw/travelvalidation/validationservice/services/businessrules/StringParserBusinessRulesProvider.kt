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
import org.springframework.stereotype.Component

@Component
class StringParserBusinessRulesProvider: IBusinessRulesProvider {

    private val stringReader: IStringReader

    constructor(stringReader: IStringReader)
    {
        this.stringReader = stringReader
        refresh()
    }

    override fun refresh() {
        countryRisks = parseCountries(stringReader.read("config"))
        val mapper = ObjectMapper()
        mapper.findAndRegisterModules()
        val remoteRules =
            mapper.readValue(stringReader.read("business_rules"), object : TypeReference<List<RuleRemote>>() {})
                .toRules()
        val customBusinessRules = mapper.readValue(
            stringReader.read("custom_business_rules"),
            object : TypeReference<List<RuleRemote>>() {}).toRules()
        rules = listOf(remoteRules, customBusinessRules).flatten()

        valueSetsJson = stringReader.read("value_sets")
    }

    private var isOtherIncluded: Boolean = true //TODO where is this set?

    override lateinit var rules: List<Rule>
    override lateinit var valueSetsJson: String
    override lateinit var countryRisks: List<CountryRisk>

    private fun parseCountries(configContent: String): List<CountryRisk> {
        val obj: JsonObject = Gson().fromJson(configContent, JsonObject::class.java)
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