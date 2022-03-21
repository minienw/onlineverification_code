package nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.validate

/**
 * TODO Map to this is a DCCFailableItem
* 3.8.5.3
* <see cref="nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.validate.ResultTokenPayload.Results" />
*/
class ResultTokenPayloadResult
    (
    /**
    * Identifier of the check (rule?)
    * e.g.VR-0001, CBOR, SIGNATURE etc.
    */
    var id : String,

    /**
    * Result of the check
    * OPEN (CHK)
    * FAILED (NOK)
    * PASSED (OK)
    */
    var result : String,

    /**
    * Type of the check
    * Values:
    * Technical Check
    * Issuer Invalidation
    * Destination Acceptance
    * Traveller Acceptance
    */
    var type : String,

    /**
    * Description of the checkup
    */
    var details : String,
)
