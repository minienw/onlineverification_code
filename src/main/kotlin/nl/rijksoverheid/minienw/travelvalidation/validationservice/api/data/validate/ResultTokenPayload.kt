package nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.validate

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem

/**
* 3.8.5.3
*/
class ResultTokenPayload(
    /**
     * https://serviceprovider
     * id (uri?) of identity document
     */
    @SerializedName("iss")
    @JsonProperty("iss")
    var serviceProviderUri: String,

    //Claim added by token builder

    /**
     * TODO time of what?
     * Unix epoch time
     */
    @SerializedName("iat")
    @JsonProperty("iat")
    var whenIssued: Long,

    /**
     * TODO What expires?
     * Epoch time
     */
    @SerializedName("exp")
    @JsonProperty("exp")
    var whenExpires: Long,

    /**
     * e.g. Inter-Flight, Concert, Domestic, MassEvent > 1000, etc.
     * Category which  shall be reflected in the Validation by additional rules/logic. If null, Standard Business Rule Check will apply.
     * Default: “Standard”
     * For 2
     * @return list of categories
     */
    var category: Array<String>,

    /**
     * TODO "Value of the Initialisation"
     * e.g. ADEDDDDDDDDDDDDDDD
     */
    @SerializedName("sub")
    @JsonProperty("sub")
    var subject: String,

    /**
     * Final result of the evaluation.
     * OK = Passed
     * NOK = Fail
     * CHK = Cross Check(OPEN)
     */
    var result: String, //TODO ResultTokenPayloadResult?

    var results: Array<DCCFailableItem>,

    /**
     * The one to send to the airline
     */
    var confirmation: String?, //JWT of ConfirmationTokenPayload

    // V2 Extra
    @SerializedName("personalinfodccextract")
    @JsonProperty("personalinfodccextract")
    var dccExtract: DccExtract?
)