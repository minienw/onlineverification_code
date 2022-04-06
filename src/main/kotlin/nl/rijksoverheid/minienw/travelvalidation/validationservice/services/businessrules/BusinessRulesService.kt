package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules

import nl.rijksoverheid.dcbs.verifier.models.CountryRisk
import nl.rijksoverheid.dcbs.verifier.models.DCCQR
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.BusinessRulesCommandArgs
import org.springframework.beans.factory.annotation.Qualifier
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
    private val businessRulesConfig: IBusinessRulesProvider
) {

    private var countryFrom: CountryRisk? = null
    private var countryTo: CountryRisk? = null
    private var args: BusinessRulesCommandArgs? = null

    fun canExecute(args: BusinessRulesCommandArgs): Boolean
    {
        this.args = args
        this.countryFrom = businessRulesConfig.countryRisks.find { it.code == args.trip.countryFrom }
        this.countryTo = businessRulesConfig.countryRisks.find { it.code == args.trip.countryTo }
        return this.countryFrom != null && this.countryTo != null
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
            businessRulesConfig.rules,
            businessRulesConfig.valueSetsJson)
    }
}
