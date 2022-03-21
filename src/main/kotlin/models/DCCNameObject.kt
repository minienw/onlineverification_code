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

class DCCNameObject(
    @SerializedName("gn")
    val givenName: String?,
    @SerializedName("gnt")
    val givenNameTransliterated: String?,

    @SerializedName("fn")
    val familyName: String?,
    @SerializedName("fnt")
    val familyNameTransliterated: String
) {
    fun retrieveFamilyName(): String {
        return familyName ?: familyNameTransliterated.lowercase(Locale.getDefault()).capitalize()
            //.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}