package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import COSE.Message
import COSE.MessageTag
import com.upokecenter.cbor.CBORObject
import nl.minvws.encoding.Base45
import nl.rijksoverheid.dcbs.verifier.models.*
import java.io.ByteArrayOutputStream
import java.util.zip.Inflater


class DccDecoder {
    fun parse(prefix: String): DccQrCode {
        var delimiterIndex = prefix.indexOf(":")
        if (delimiterIndex == -1) //TODO || delimiterIndex > 4
            throw Exception("Delimiter missing")

        val base45 = prefix.substring(delimiterIndex + 1).trimStart()
        val base45Bytes = base45.toByteArray()
        val compressed = Base45.getDecoder().decode(base45Bytes)
        val cose = decompress(compressed!!);
        val cbor = Message.DecodeFromBytes(cose, MessageTag.Sign1)
        val cborObject: CBORObject = CBORObject.DecodeFromBytes(cbor.GetContent())

        var vaccinations = ArrayList<DCCVaccine>(0)
        var recoveries = ArrayList<DCCRecovery>(0)
        var tests = ArrayList<DCCTest>(0)

        val dccRoot = cborObject[-260][1]

        if (dccRoot.ContainsKey("t")) {
            for (i in 0 until dccRoot["t"].size()) {
                val obj = dccRoot["t"][i]
                tests.add(
                    DCCTest(
                        targetedDisease = getString(obj, "tg"),
                        dateOfSampleCollection = getStringOptional(obj, "sc"),
                        testResult = getString(obj, "tr"),
                        testingCentre = getStringOptional(obj, "tc"),
                        countryOfTest = getString(obj, "co"),
                        typeOfTest = getString(obj, "tt"),
                        certificateIssuer = getString(obj, "is"),
                        RATTestNameAndManufac = getStringOptional(obj, "noIdea"),
                        certificateIdentifier = getString(obj, "ci"),
                        NAATestName = getStringOptional(obj, "???"),
                    )
                )
            }
        }

        if (dccRoot.ContainsKey("v")) {
            for (i in 0 until dccRoot["v"].size()) {
                val obj = dccRoot["v"][i]
                vaccinations.add(
                    DCCVaccine(
                        targetedDisease = getString(obj, "tg"),
                        vaccine = getString(obj, "vp"),
                        vaccineMedicalProduct = getString(obj, "mp"),
                        marketingAuthorizationHolder = getString(obj, "ma"),
                        doseNumber = getInt32(obj, "dn"),
                        totalSeriesOfDoses = getInt32(obj, "sd"),
                        certificateIssuer = getString(obj, "is"),
                        certificateIdentifier = getString(obj, "ci"),
                        countryOfVaccination = getString(obj, "co"),
                        dateOfVaccination = getStringOptional(obj, "dt"),
                    )
                )
            }
        }

        if (dccRoot.ContainsKey("r")) {
            for (i in 0 until dccRoot["r"].size()) {
                val obj = dccRoot["r"][i]
                recoveries.add(
                    DCCRecovery(
                        targetedDisease = getString(obj, "tg"),
                        countryOfTest = getString(obj, "co"),
                        certificateIssuer = getString(obj, "is"),
                        certificateIdentifier = getString(obj, "ci"),
                        dateOfFirstPositiveTest = getStringOptional(obj, "df"),
                        certificateValidFrom = getStringOptional(obj, "fr"),
                        certificateValidTo = getStringOptional(obj, "du"),
                    )
                )
            }
        }

        var nameRoot = dccRoot["nam"]
        var nameObject = DCCNameObject(
            givenName = getString(nameRoot, "gn"),
            familyName = getString(nameRoot, "fn"),
            familyNameTransliterated = getString(nameRoot, "fnt"),
            givenNameTransliterated = getString(nameRoot, "gnt"),
        )

        val dcc = DCC(
            version = getString(dccRoot, "ver"),
            dateOfBirth = getString(dccRoot, "dob"),
            name = nameObject,
            vaccines = if (vaccinations.size > 0) vaccinations else null,
            tests = if (tests.size > 0) tests else null,
            recoveries = if (recoveries.size > 0) recoveries else null,

            from = null, //Trip parameter
            to = null, //Trip parameter
        )

        //var country = cborObject[1].toString()
        var item4 = cborObject[4].AsInt64Value()
        var item6 = cborObject[6].AsInt64Value()

        val result = DccQrCode(dcc = dcc, expirationTime = item4, issuedAt = item6)

        return result
    }

    fun getString(obj: CBORObject, key: String): String = obj[key].AsString()

    fun getStringOptional(obj: CBORObject, key: String): String? = if (obj.ContainsKey(key)) obj[key].AsString() else null

    fun getInt32(obj: CBORObject, key: String): Int = obj[key].AsInt32()

    fun decompress(data: ByteArray): ByteArray {
        val inflater = Inflater()
        inflater.setInput(data)
        ByteArrayOutputStream(data.size).use { outputStream ->
            val buffer = ByteArray(4096)
            while (!inflater.finished()) {
                val count = inflater.inflate(buffer)
                outputStream.write(buffer, 0, count)
            }
            return outputStream.toByteArray()
        }
    }
}