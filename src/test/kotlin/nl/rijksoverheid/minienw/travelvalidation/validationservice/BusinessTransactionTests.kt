package nl.rijksoverheid.minienw.travelvalidation.validationservice

import nl.rijksoverheid.minienw.travelvalidation.api.data.*
import nl.rijksoverheid.minienw.travelvalidation.api.data.ValidationAccessTokenPayload
import nl.rijksoverheid.minienw.travelvalidation.api.data.initialize.ValidationInitializeRequestBody
import nl.rijksoverheid.minienw.travelvalidation.api.data.initialize.ValidationInitializeResponse
import nl.rijksoverheid.minienw.travelvalidation.api.data.token.ValidationType
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.HttpPostValidationInitialiseV2Command
import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.ValidationInitializeRequestBodyValidatorV2
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.slf4j.*
import org.springframework.http.HttpStatus
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Security
import java.security.spec.ECGenParameterSpec
import java.time.Instant
import java.util.*
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory
import javax.validation.constraints.NotNull

class BusinessTransactionTests {

    class Argle {
        @NotNull
        val thing: Any? = null
    }

    @Test
    fun sanityCheckValidation() {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        val validator: Validator = factory.validator
        val errors = validator.validate(Argle())
        assert(errors.size == 1)
    }

    @Test
    fun sanityCheckValidationAccessTokenPayload(): ValidationAccessTokenPayload {
        val ttt = ValidationAccessTokenPayload(
            whenExpires = 1746560497,
            whenIssued = 1646560497,
            serviceProvider = "http://goflyyourself.com",
            subject = "fake Subject",
            subjectUri = "https://subject.com",
            jsonTokenIdentifier = "fake jti",
            ValidationCondition = ValidationAccessTokenPayloadCondition(
                //DccHash = "sdaasdad",
                lang = "en",
                gnt = "s",
                fnt = "k",
                dob = "1944",
                poa = "AMS",
                pod = "FRA",
                coa = "NL",
                cod = "DE",
                roa = "sdasd",
                rod = "sdfsdf",
                type = arrayOf("v"),
                category = arrayOf("cat..."),
                validationClock = "2021-01-29T12:00:00+01:00",
                validfrom = "2021-01-29T12:00:00+01:00",
                validTo = "2021-01-29T12:00:00+01:00",
            ),
            ValidationType = ValidationType.Full,
            ValidationVersion = "2.00",
        )

//        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
//        val validator: Validator = factory.validator
//        val errors = validator.validate(ttt)

//        assert(errors.isEmpty())

        return ttt
    }


    @Test
    fun start() {

        val dateTimeProvider = Mockito.mock(IDateTimeProvider::class.java)
        Mockito.`when`(dateTimeProvider.snapshot()).thenReturn(Instant.ofEpochSecond(1645951914))


        val appSettings = Mockito.mock(IApplicationSettings::class.java)
        Mockito.`when`(appSettings.sessionMaxDurationSeconds).thenReturn(42)
        Mockito.`when`(appSettings.configFileFolderPath).thenReturn("build\\resources\\main\\dev")

        val sessionRepository  = Mockito.mock(ISessionRepository::class.java)

        val l = LoggerFactory.getLogger(HttpPostValidationInitialiseV2Command::class.java) as Logger

        val cmd = HttpPostValidationInitialiseV2Command(
            dateTimeProvider=dateTimeProvider,
            sessionRepository = sessionRepository,
            appSettings = appSettings,
            bodyValidator = ValidationInitializeRequestBodyValidatorV2(),
            subjectIdGenerator = ValidationServicesSubjectIdGenerator(),
            logger = l
        )

        val ttt = ValidationAccessTokenPayload(
            whenExpires = 1645966339L,
            whenIssued = 1645966339L,
            serviceProvider = "argle",
            subject = UUID.randomUUID().toString().replace("-", ""),
            subjectUri = "https://subject.com",
            jsonTokenIdentifier = "0123456789abcdef0123456789abcdef",
            ValidationCondition = ValidationAccessTokenPayloadCondition(
                //DccHash = "sdaasdad",
                lang = "en",
                fnt = "",
                gnt = "",
                dob = "1979-04-14",
                coa = "NL",
                cod = "DE",
                roa = "sdasd",
                rod = "sdfsdf",
                poa = "AMS",
                pod = "FRA",
                type = arrayOf("v"),
                category = arrayOf("cat..."),
                validationClock = "2021-01-29T12:00:00+01:00",
                validfrom = "2021-01-29T12:00:00+01:00",
                validTo = "2021-01-29T12:00:00+01:00",
            ),
            ValidationType = ValidationType.Full,
            ValidationVersion = "V1233",
        )

        //Sanity check
//        val json = Gson().toJson(ttt)
//        val andBack = Gson().fromJson(json, nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.ValidationAccessTokenPayload::class.java)

//        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
//        val validator: Validator = factory.validator
//        val errors = validator.validate(ttt)

        //assert(errors.isEmpty())

        val keypair = getEcKeyPair()
        val publicKeyString = CryptoKeyConverter.encodeAsn1DerPkcs1X509Base64(keypair.public)


        val requestBody = ValidationInitializeRequestBody(
            walletPublicKey = publicKeyString,
            walletPublicKeyAlgorithm = "SHA256withECDSA",
            nonce = "MDEyMzQ1Njc4OWFiY2RlZg=="
        )

        val result = cmd.execute(ttt, requestBody, ttt.sub)
        assert(result.statusCode == HttpStatus.OK)
        val responseBody = result.body as ValidationInitializeResponse
        assert(responseBody.whenExpires == 42L + 1645951914)
    }

    private fun getEcKeyPair(): KeyPair {
        return EcCrypto().getEcKeyPair()
    }
}

class EcCrypto
{
    fun getEcKeyPair(): KeyPair {
        Security.addProvider(BouncyCastleProvider())
        val ecSpec = ECGenParameterSpec("secp256k1")
        val g = KeyPairGenerator.getInstance("ECDSA")
        g.initialize(ecSpec, SecureRandom())
        return g.generateKeyPair()
    }
}

