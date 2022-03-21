package nl.rijksoverheid.dcbs.verifier.models

import com.google.gson.annotations.SerializedName
import java.util.*

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

class CountryRisk(
    @SerializedName("name_us")
    val nameUs: String?,
    @SerializedName("name_nl")
    val nameNl: String?,
    @SerializedName("country_EN")
    val countryEn: String?,
    @SerializedName("country_NL")
    val countryNl: String?,
    val code: String?,
    val color: String?,
    @SerializedName("result_on_valid_code")
    val resultOnValidCode: String?,
    @SerializedName("is_colour_code")
    val isColourCode: Boolean?,
    @SerializedName("rule_engine_enabled")
    val ruleEngineEnabled: Boolean?,
    @SerializedName("is_EU")
    val isEU: Boolean?
) {

    fun getUnselected(): CountryRisk {
        val unselected = "R.string.country_unselected" //TODO context.getString(R.string.country_unselected)
        return CountryRisk(
            unselected,
            unselected,
            unselected,
            unselected,
            unselected,
            CountryColorCode.GREEN.value,
            CountryRiskPass.Inconclusive.value,
            isColourCode = false,
            ruleEngineEnabled = false,
            isEU = false,
        )
    }

    fun isIndecisive(): Boolean {
        if (getColourCode() == null) {
            return true
        }
        if (getPassType() == null) {
            return true
        }
        if (getPassType() == CountryRiskPass.Inconclusive) {
            return true
        }
        return false
    }

    fun getColourCode(): CountryColorCode? {
        return CountryColorCode.fromValue(color)
    }

    fun getPassType(): CountryRiskPass? {
        return CountryRiskPass.fromValue(resultOnValidCode)
    }

    fun name(): String {
        val language = Locale.getDefault().language
        return if (language.lowercase(Locale.getDefault()) == "nl") {
            nameNl ?: countryNl ?: ""
        } else {
            nameUs ?: countryEn ?: ""
        }
    }

    fun section(): String? {
        return name().firstOrNull()?.toString()
    }
}