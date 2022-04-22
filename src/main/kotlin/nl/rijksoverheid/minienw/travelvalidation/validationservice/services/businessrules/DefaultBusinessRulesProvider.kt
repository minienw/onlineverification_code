package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import dgca.verifier.app.engine.data.source.remote.rules.RuleRemote
import dgca.verifier.app.engine.data.source.remote.rules.toRules
import nl.rijksoverheid.dcbs.verifier.models.CountryColorCode
import nl.rijksoverheid.dcbs.verifier.models.CountryRisk
import nl.rijksoverheid.dcbs.verifier.models.CountryRiskPass
import org.bouncycastle.util.encoders.Base64
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DefaultBusinessRulesProvider(
    private val storage: RedisConfigStorage,
    private val httpRemoteBusinessRulesSource: HttpRemoteBusinessRulesSource
    ): IBusinessRulesProvider
{
    private val logger: Logger = LoggerFactory.getLogger(DefaultBusinessRulesProvider::class.java)

    fun getOrRefresh(): EncodedBusinessConfig
    {
        //Get
        var encodedConfigJson = storage.find("allOrNothing")
        if (encodedConfigJson != null) {
            return Gson().fromJson(encodedConfigJson, EncodedBusinessConfig::class.java)
        }

        logger.debug("Refreshing business rules.")
        //Refresh
        val encodedConfig = refresh()
        encodedConfigJson = Gson().toJson(encodedConfig)
        storage.save("allOrNothing", encodedConfigJson)
        logger.info("Refreshed business rules.")
        return encodedConfig
    }

    override fun allOrNothing(): AllOrNothing = ParseFromEncoded(getOrRefresh())

    private fun refresh() : EncodedBusinessConfig {
        logger.debug("Refreshing business rules.")
        return EncodedBusinessConfig(
            config = httpRemoteBusinessRulesSource.read("config"),
            business_rules = httpRemoteBusinessRulesSource.read("business_rules"),
            custom_business_rules = httpRemoteBusinessRulesSource.read("custom_business_rules"),
            value_sets = httpRemoteBusinessRulesSource.read("value_sets")
        )

    }

    //Unsh**
    private fun decode(encoded: String): String = String(Base64.decode(encoded), Charsets.UTF_8)

     private fun ParseFromEncoded(encodedConfig: EncodedBusinessConfig) : AllOrNothing
     {
        val config = decode(encodedConfig.config)
        val cbr = decode(encodedConfig.custom_business_rules)
        val br = decode(encodedConfig.business_rules)
        val vs = decode(encodedConfig.value_sets)
        val countryRisks = parseCountries(config)

        //TODO switch to Gson...?
        val mapper = ObjectMapper()
        mapper.findAndRegisterModules()
        val remoteRules = mapper.readValue(br, object : TypeReference<List<RuleRemote>>() {}).toRules()
        val customBusinessRules = mapper.readValue(cbr, object : TypeReference<List<RuleRemote>>() {}).toRules()
        val rules = listOf(remoteRules, customBusinessRules).flatten()

        return AllOrNothing(
            countryRisks = countryRisks,
            rules = rules,
            valueSetsJson = vs
        )
    }

    private var isOtherIncluded: Boolean = true //TODO where is this set?

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