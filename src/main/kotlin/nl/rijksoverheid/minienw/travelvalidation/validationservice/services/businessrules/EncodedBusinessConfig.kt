package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules

/*Save this in Redis - using the base64 encoding from the original endpoints avoids json encoding/decoding issues*/
data class EncodedBusinessConfig(
    val config:String,
    val business_rules:String,
    val custom_business_rules:String,
    val value_sets:String
)