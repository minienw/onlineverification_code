package nl.rijksoverheid.minienw.travelvalidation.validationservice

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito


class BusinessRulesEngineTests {

    @Test
    fun remoteSanityCheck() {
        val appSettings =  Mockito.mock(IApplicationSettings::class.java)
        Mockito.`when`(appSettings.businessRulesUri).thenReturn("https://verifier-api.coronacheck.nl/v4/dcbs/business_rules")
        Mockito.`when`(appSettings.customBusinessRulesUri).thenReturn("https://verifier-api.coronacheck.nl/v4/dcbs/custom_business_rules")
        Mockito.`when`(appSettings.configUri).thenReturn("https://verifier-api.coronacheck.nl/v4/dcbs/config")
        Mockito.`when`(appSettings.valueSetsUri).thenReturn("https://verifier-api.coronacheck.nl/v4/dcbs/value_sets")
        //Mockito.`when`(appSettings.publicKeysUri).thenReturn("https://verifier-api.coronacheck.nl/v4/dcbs/public_keys")

        val provider = DefaultBusinessRulesProvider(RedisConfigStorage(appSettings), HttpRemoteBusinessRulesSource(appSettings))
        val config = provider.allOrNothing()

        assert(!config.rules.isEmpty())
        assert(!config.countryRisks.isEmpty())
        assert(!config.valueSetsJson.isEmpty())
    }

//    //val public_keysContents = File(fileFolder, "public_keys.json") //For DCC check? //Trust list? Which one? or is that the DCC check?
//    @Test
//    fun sanityCheck()
//    {
//        val folder = "build\\resources\\main\\test"
//
//        val appSettings =  Mockito.mock(IApplicationSettings::class.java)
//        Mockito.`when`(appSettings.configFileFolderPath).thenReturn(folder)
//
//        println(File(folder,".").absoluteFile)
//
//        //For args
//        val dccString = File(folder, "JeanneSpecimen.dcc.json").readText(Charsets.UTF_8)
//
//        val config = DefaultBusinessRulesProvider(FileConfigProvider(appSettings))
//
////        //For command
////        //TODO put these directly in Redis
////        val configContents = File(fileFolder, "config.json").readText(Charsets.UTF_8) //https://unshit.nl/?shit=https://verifier-api.coronacheck.nl/v4/dcbs/config //contains a top level element countryColours
////        val business_rulesContents = File(fileFolder, "business_rules.json").readText(Charsets.UTF_8) //https://unshit.nl/?shit=https://verifier-api.coronacheck.nl/v4/dcbs/business_rules
////        val value_setsContents = File(fileFolder, "value_sets.json").readText(Charsets.UTF_8) //https://unshit.nl/?shit=https://verifier-api.coronacheck.nl/v4/dcbs/value_sets
////        val custom_business_rulesContents = File(fileFolder, "custom_business_rules.json").readText(Charsets.UTF_8) //https://unshit.nl/?shit=https://verifier-api.coronacheck.nl/v4/dcbs/custom_business_rules
////
////        //Keep these in memory
//        val mapper = ObjectMapper()
//        mapper.findAndRegisterModules()
//        val dcc = Gson().fromJson(dccString, DCC::class.java)
////        val countries : List<CountryRisk> = parseCountries(configContents)
////        val remoteRules = mapper.readValue(business_rulesContents, object : TypeReference<List<RuleRemote>>() {}).toRules()
////        val customBusinessRules = mapper.readValue(custom_business_rulesContents, object : TypeReference<List<RuleRemote>>() {}).toRules()
////        val allBusinessRules = listOf(remoteRules, customBusinessRules).flatten()
//
//        val args = BusinessRulesCommandArgs(
//            TripInfo(countryFrom = "NL",
//            countryTo = "DE"),
//            //issuer = "NL", //TODO Not from the DCC?  //QR code?
//            issuedAt =       1646139858, //QR code?
//            expirationTime = 1800000000, //QR code?
//            dcc = dcc,
//            //encodeDcc = dccString //NOPE!!!!!!
//        )
//
//        val businessRulesCommand = BusinessRulesService(config)
//        assert(businessRulesCommand.canExecute(args))
//        val result = businessRulesCommand.execute()
//        assert(result.isNotEmpty())
//    }

//    @Test
//    fun demo_BE_to_NL_any_DCC_from_JeanneSpecimen_Json()
//    {
//        val folder = "src\\main\\resources\\acc_20220307"
//
//        val appSettings =  Mockito.mock(IApplicationSettings::class.java)
//        Mockito.`when`(appSettings.configFileFolderPath).thenReturn(folder)
//
//        val dccJson = File(folder, "dcc.bob_bouwer.v.json").readText(Charsets.UTF_8)
//
//        val config = arserBusinessRulesProvider(FileConfigProvider(appSettings))
//
////        val mapper = ObjectMapper()
////        mapper.findAndRegisterModules()
//        val dcc = Gson().fromJson(dccJson, DCC::class.java)
//
//        val args = BusinessRulesCommandArgs(
//            TripInfo(countryFrom = "BE", countryTo = "NL"),
//            //issuer = "NL", //TODO Not from the DCC?  //QR code?
//            issuedAt =       1646139858, //QR code?
//            expirationTime = 1800000000, //QR code?
//            dcc = dcc,
//        )
//
//        val businessRulesCommand = BusinessRulesService(config)
//        assert(businessRulesCommand.canExecute(args))
//        val result = businessRulesCommand.execute()
//        assert(result.isEmpty())
//    }
}