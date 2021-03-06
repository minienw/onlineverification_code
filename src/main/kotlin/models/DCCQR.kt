package nl.rijksoverheid.dcbs.verifier.models

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser

import dgca.verifier.app.engine.DefaultAffectedFieldsDataRetriever
import dgca.verifier.app.engine.DefaultCertLogicEngine
import dgca.verifier.app.engine.DefaultJsonLogicValidator
import dgca.verifier.app.engine.Result
import dgca.verifier.app.engine.data.CertificateType
import dgca.verifier.app.engine.data.ExternalParameter
import dgca.verifier.app.engine.data.Rule
import dgca.verifier.app.engine.data.RuleCertificateType

import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableType
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*


/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

class DCCQR(
) {
//    private fun DCC.getName(): String {
//        return this.name?.retrieveFamilyName() + " " + (this.name?.givenName
//            ?: "").capitalize(Locale.getDefault())
//    }

    //Extension
    private fun DCC.getEngineCertificateType(): CertificateType {
        return when {
            this.recoveries?.isNotEmpty() == true -> CertificateType.RECOVERY
            this.vaccines?.isNotEmpty() == true -> CertificateType.VACCINATION
            this.tests?.isNotEmpty() == true -> CertificateType.TEST
            else -> CertificateType.TEST
        }
    }

    private fun String.getValueSetMap(): Map<String, List<String>> {
        val valueSetsMap = mutableMapOf<String, List<String>>()
        val jsonObject = JsonParser.parseString(this).asJsonObject
        val entrySet: MutableSet<MutableMap.MutableEntry<String, JsonElement>> =
            jsonObject.entrySet()
        entrySet.forEach { entrySetItem ->
            val nestedKeysList = mutableListOf<String>()
            val nestedEntrySet = entrySetItem.value.asJsonObject.entrySet()
            nestedEntrySet.forEach { nestedEntrySetItem ->
                nestedKeysList.add(nestedEntrySetItem.key)
            }
            valueSetsMap[entrySetItem.key] = nestedKeysList
        }
        return valueSetsMap
    }

    private fun List<Rule>.filterRules(
        validationClock: ZonedDateTime,
        issuanceCountryIsoCode: String,
        certificateType: CertificateType,
    ): List<Rule> {
        val acceptanceRules = this.toMutableList()
        if (issuanceCountryIsoCode.isNotBlank()) {
            forEach { rule ->
                val validDate = rule.validFrom < validationClock && rule.validTo > validationClock
                val validType =
                    rule.ruleCertificateType.name == certificateType.name || rule.ruleCertificateType == RuleCertificateType.GENERAL
                val validCountry =
                    rule.countryCode.uppercase(Locale.getDefault()) == issuanceCountryIsoCode
                if (!validDate || !validType || !validCountry) {
                    acceptanceRules.remove(rule)
                }
            }
        }
        return acceptanceRules
    }

    private var expirationTime: Long = 0
    private var issuedAt: Long = 0


    //TODO HERE single function for evaluating business rules
    fun processBusinessRules(
        issuedAt: Long, // When was this QR issued at in seconds
        expirationTime: Long, // When does this QR expire in seconds
        dcc: DCC,
        from: CountryRisk,
        to: CountryRisk,
        businessRules: List<Rule>,
        valueSets: String,
        validationClock: ZonedDateTime = ZonedDateTime.now(ZoneId.of(ZoneOffset.UTC.id)) //TODO add to DTP
    ): List<DCCFailableItem> {

        this.issuedAt = issuedAt
        this.expirationTime = expirationTime
        //TODO If we already have the DCC, why are we changing it?
        dcc.from = from
        dcc.to = to

        val result = ArrayList<DCCFailableItem>()
        if (to.ruleEngineEnabled != false)
        {
                val payload = Gson().toJson(dcc)
                result.addAll(
                    filterByRuleEngine(
                        to = to,
                        businessRules = businessRules,
                        payload = payload,
                        dcc = dcc,
                        valueSets = valueSets,
                        validationClock = validationClock
                    ))
        }

        //TODO send me a different QR code
        if (from.isIndecisive() && to.getPassType() == CountryRiskPass.NLRules || to.isIndecisive()) {
            return listOf(DCCFailableItem(DCCFailableType.UndecidableFrom))
        }

        return result
    }

    private fun filterByRuleEngine(
        to: CountryRisk,
        businessRules: List<Rule>,
        payload: String,
        dcc: DCC,
        valueSets: String,
        validationClock: ZonedDateTime
    ): List<DCCFailableItem> {
        val objectMapper = ObjectMapper()
        val certLogicEngine = DefaultCertLogicEngine(
            jsonLogicValidator = DefaultJsonLogicValidator(),
            affectedFieldsDataRetriever = DefaultAffectedFieldsDataRetriever(
                objectMapper.readTree(
                    JSON_SCHEMA_V1
                ), objectMapper
            )
        )
        val valueSetsMap = valueSets.getValueSetMap()
        val instantExpirationTime: Instant = Instant.ofEpochMilli(this.expirationTime)
        val instantIssuedAt: Instant = Instant.ofEpochMilli(this.issuedAt)
        val externalParameter = ExternalParameter(
            validationClock = validationClock,
            valueSets = valueSetsMap,
            countryCode = to.code ?: "",
            exp = ZonedDateTime.ofInstant(instantExpirationTime, ZoneId.systemDefault()),
            iat = ZonedDateTime.ofInstant(instantIssuedAt, ZoneId.systemDefault()),
            issuerCountryCode = to.code ?: "",
            kid = "",
        )

        val validationResults =
            certLogicEngine.validate(
                dcc.getEngineCertificateType(),
                dcc.version,
                businessRules.filterRules(
                    ZonedDateTime.now(ZoneId.of(ZoneOffset.UTC.id)),
                    to.code ?: "",
                    dcc.getEngineCertificateType(),
                ),
                externalParameter,
                payload
            )
        val failingItems = ArrayList<DCCFailableItem>()
        validationResults.map { validationResult ->
            if (validationResult.result == Result.FAIL) {
                val description = getRuleDescription(validationResult.rule.descriptions)
                failingItems.add(
                    DCCFailableItem(
                        DCCFailableType.CustomFailure,
                        customMessage = description,
                        ruleIdentifier = validationResult.rule.identifier
                    )
                )
            }
        }
        return failingItems
    }

    private fun getRuleDescription(descriptions: Map<String, String>): String {

        val language = Locale.getDefault().language.lowercase(Locale.getDefault())
        descriptions[language]?.let {
            return it
        } ?: run {
            return descriptions["en"] ?: ""
        }
    }

    fun shouldShowGreenOverride(from: CountryRisk, to: CountryRisk): Boolean {
        return to.getPassType() == CountryRiskPass.NLRules && from.getColourCode() == CountryColorCode.GREEN
                && from.isEU == true
    }

}