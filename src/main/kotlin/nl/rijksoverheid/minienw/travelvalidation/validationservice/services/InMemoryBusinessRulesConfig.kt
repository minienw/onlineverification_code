package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import dgca.verifier.app.engine.data.Rule
import nl.rijksoverheid.dcbs.verifier.models.CountryRisk
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.IBusinessRulesConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
@Qualifier("Top")
class InMemoryBusinessRulesConfig : IBusinessRulesConfig
{
    constructor(@Qualifier("Bottom")businessRulesConfigSource: IBusinessRulesConfig)
    {
        rules = businessRulesConfigSource.rules
        valueSetsJson = businessRulesConfigSource.valueSetsJson
        countryRisks = businessRulesConfigSource.countryRisks
    }

    override val rules: List<Rule>
    override val valueSetsJson: String
    override val countryRisks: List<CountryRisk>
}