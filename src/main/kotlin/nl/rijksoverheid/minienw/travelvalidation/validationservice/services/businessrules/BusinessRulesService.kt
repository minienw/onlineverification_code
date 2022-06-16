package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules

import nl.rijksoverheid.dcbs.verifier.models.CountryRisk
import nl.rijksoverheid.dcbs.verifier.models.DCCQR
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.BusinessRulesCommandArgs
import org.springframework.stereotype.Component
import java.lang.IllegalStateException


//class FileReaderCheckBusinessRulesCommandConfig: ICheckBusinessRulesCommandConfig
//{
//    val businessRules: List<Rule>
//    val valueSets: String
//    val countries : List<CountryRisk>
//
//    constructor(appConfig: IApplicationSettings)
//    {
//        appConfig.configFilesPath
//    }
//
//
//}

@Component
class BusinessRulesService(
    private val businessRulesProvider: IBusinessRulesProvider
) {
    private val config: AllOrNothing = businessRulesProvider.allOrNothing()
    private var countryFrom: CountryRisk? = null
    private var countryTo: CountryRisk? = null
    private var args: BusinessRulesCommandArgs? = null

    fun canExecute(args: BusinessRulesCommandArgs): Boolean
    {
        val config = businessRulesProvider.allOrNothing()
        this.args = args
        countryFrom = config.countryRisks.find { it.code == args.trip.countryFrom }
        countryTo = config.countryRisks.find { it.code == args.trip.countryTo }
        return countryFrom != null && this.countryTo != null
    }

    fun execute() : List<DCCFailableItem> //TODO crossed the streams a bit here...
    {
        if (this.args == null || this.countryFrom == null || this.countryTo == null)
            throw IllegalStateException("Must call canExecute before.")

        return DCCQR().processBusinessRules(
            this.args!!.issuedAt,
            this.args!!.expirationTime,
            this.args!!.dcc,
            this.countryFrom!!,
            this.countryTo!!,
            config.rules,
            config.valueSetsJson)
    }
}
