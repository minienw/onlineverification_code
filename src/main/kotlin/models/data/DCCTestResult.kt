package dcbs.verifier.models.data

enum class DCCTestResult(val value: String) {
    NotDetected("260415000"),
    Detected("260373001");

    fun getDisplayName(): String {
        return "TODO DCCTestResult.getDisplayName"
//        return context.getString(when (this) {
//            NotDetected -> R.string.item_test_header_negative
//            Detected ->R.string.item_test_header_positive
//        })
    }

    companion object {
        fun fromValue(value: String?): DCCTestResult? {
            return values().firstOrNull { it.value == value }
        }
    }
}