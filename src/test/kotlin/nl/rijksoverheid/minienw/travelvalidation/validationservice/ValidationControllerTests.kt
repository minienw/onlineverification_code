package nl.rijksoverheid.minienw.travelvalidation.validationservice

import ValidationAccessTokenConditionPayload
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.ValidationAccessTokenPayload
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.initialize.ValidationInitializeRequestBody
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.initialize.ValidationInitializeResponse
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.initialize.ValidationType
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.HttpPostValidationInitialiseV2Command
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.ValidationInitializeRequestBodyValidatorV2
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Security
import java.security.spec.ECGenParameterSpec
import java.time.Instant
import java.util.*


class ValidationControllerTests {
    @Test
    fun initialize_withRedis(){

        val configFile =  Mockito.mock(IApplicationSettings::class.java)
//        Mockito.`when`(configFile.airlineTravellerTripTokenPublicKey).thenReturn(AirlineTokenTests.publicKeyEncoded)
//        Mockito.`when`(configFile.dccRsaPublicKeyBase64).thenReturn("an encryption key")
        Mockito.`when`(configFile.redisHost).thenReturn("localhost")

        Mockito.`when`(configFile.configFileFolderPath).thenReturn("build\\resources\\main\\dev")

        val dtp =  Mockito.mock(IDateTimeProvider::class.java)
        Mockito.`when`(dtp.snapshot()).thenReturn(Instant.ofEpochSecond(1646052982))

        var subject = HttpPostValidationInitialiseV2Command(
            dtp,
            configFile,
            SessionRepositoryRedis(configFile),
            ValidationInitializeRequestBodyValidatorV2())

        val keypair = getEcKeyPair()
        var publicKeyString = CryptoKeyConverter.encodeAsn1DerPkcs1X509Base64(keypair.public);

        val validationAccessTokenPayload = ValidationAccessTokenPayload(
            whenExpires = 1645966339L,
            whenIssued = 1645966339L,
            serviceProvider = ValidationServicesSubjectIdGenerator().next(),
            subject = UUID.randomUUID().toString().replace("-", "").uppercase(),
            subjectUri = "https://subject.com",
            jsonTokenIdentifier = "0123456789abcdef0123456789abcdef",
            ValidationCondition = ValidationAccessTokenConditionPayload(
                //DccHash = "sdaasdad",
                language = "en",
                familyNameTransliterated = "who",
                givenNameTransliterated = "knows",
                dateOfBirth = "1979-04-14",
                countryOfArrival = "NL",
                countryOfDeparture = "DE",
                portOfArrival = "AMS",
                portOfDeparture = "FRA",
                regionOfArrival = "",
                regionOfDeparture = "",
                dccTypes = arrayOf("v"),
                categories = arrayOf("standard"),
                validationClock = "2021-01-29T12:00:00+01:00",
                whenValidStart = "2021-01-29T12:00:00+01:00",
                whenValidEnd = "2021-01-29T12:00:00+01:00",
            ),
            ValidationVersion =  "1.0", //TODO probably 2.0?
            ValidationType = ValidationType.Full
        )

        var result = subject.execute(
            validationAccessTokenPayload,
            body = ValidationInitializeRequestBody(nonce = "MDEyMzQ1Njc4OWFiY2RlZg==", walletPublicKeyAlgorithm = "SHA256withECDSA", walletPublicKey = publicKeyString),
            subjectId = validationAccessTokenPayload.subject
        )

        assert(result.statusCode.value() == 200)
        var body = result.body as ValidationInitializeResponse
        assert(body.subjectId.length == 32)
        assert(body.whenExpires > 0L)
        //TODO assert(result.body!!.DccEncryptionKey.alg == "ROT13")
    }

    private fun getEcKeyPair(): KeyPair {
        Security.addProvider(BouncyCastleProvider())
        val ecSpec = ECGenParameterSpec("secp256k1")
        val g = KeyPairGenerator.getInstance("ECDSA")
        g.initialize(ecSpec, SecureRandom())
        return g.generateKeyPair()
    }

    class FakeSessionRepository : ISessionRepository {
        override fun save(sessionInfo: SessionInfo) {
            sessionInfo.response.subjectId = "0123456789ABCDEF0123456789ABCDEF"
        }

        override fun find(subject: String): SessionInfo? {
            TODO("Not yet implemented")
        }

        override fun remove(subject: String) {
            TODO("Not yet implemented")
        }
    }

    @Test
    fun initialize(){

        val configFile =  Mockito.mock(IApplicationSettings::class.java)
        Mockito.`when`(configFile.configFileFolderPath).thenReturn("build\\resources\\main\\dev")
//        Mockito.`when`(configFile.airlineTravellerTripTokenPublicKey).thenReturn(AirlineTokenTests.publicKeyEncoded)
//        Mockito.`when`(configFile.dccRsaPublicKeyBase64).thenReturn("an encryption key")

        val dtp =  Mockito.mock(IDateTimeProvider::class.java)
        Mockito.`when`(dtp.snapshot()).thenReturn(Instant.ofEpochSecond(1646052982))

        var testSubject = HttpPostValidationInitialiseV2Command(dtp, configFile, FakeSessionRepository(), ValidationInitializeRequestBodyValidatorV2())

        //val sessionRepo = Mockito.mock(ISessionRepository::class.java)
        val keypair = getEcKeyPair()
        var publicKeyString = CryptoKeyConverter.encodeAsn1DerPkcs1X509Base64(keypair.public);

        var result = testSubject.execute(
            ValidationAccessTokenPayload("id", "KLM", "1324AAAA", "uri",
                12345,
                ValidationType.Full,
                "2",
                ValidationAccessTokenConditionPayload("" +
                        "hash",
                    "en",
                    "",
                    "",
                    "",
                    "NL",
                    "DE",
                    "",
                    "","",
                    arrayOf(),
                    arrayOf(),
                    "","",""                ),
                342234234)
            ,
            ValidationInitializeRequestBody(
                nonce = "MDEyMzQ1Njc4OWFiY2RlZg==",
                walletPublicKeyAlgorithm = "SHA256withECDSA",
                walletPublicKey = publicKeyString
            ),
            subjectId = "1324AAAA"
        )

        assert(result.statusCode.value() == 200)
        //assert(result.body!!.subjectId.length == 32)
        var body = result.body as ValidationInitializeResponse
        assert(body.subjectId.length > 0)
        assert(body.whenExpires > 0L)
        assert(body.validationServiceEncryptionKey != null)
        assert(body.signKey != null)
    }
}