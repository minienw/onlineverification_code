package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules

import dgca.verifier.app.engine.data.Rule
import nl.rijksoverheid.dcbs.verifier.models.CountryRisk
import org.springframework.stereotype.Component

data class AllOrNothing(
val rules : List<Rule>,
val valueSetsJson : String,
val countryRisks : List<CountryRisk>
)


@Component
interface IBusinessRulesProvider
{
    fun allOrNothing(): AllOrNothing
}
