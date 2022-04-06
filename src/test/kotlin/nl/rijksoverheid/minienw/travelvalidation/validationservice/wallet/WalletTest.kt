package nl.rijksoverheid.minienw.travelvalidation.validationservice.wallet

import com.google.gson.Gson
import io.jsonwebtoken.Jwts
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.ValidationAccessTokenPayload
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.controllers.Headers
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.PublicKeyJwk
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.identity.IdentityResponse
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.validate.ResultTokenPayload
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.validate.ValidateRequestBody
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.CryptoKeyConverter
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.crypto.AesCbcDecryptCommand
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.crypto.RsaEcbOaepWithSha256DecryptCommand
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.crypto.RsaOaepWithSha256AesCbcSchemeDecryptCommand
import nl.rijksoverheid.minienw.travelvalidation.validationservice.wallet.imported.InitiatingQrPayload
import nl.rijksoverheid.minienw.travelvalidation.validationservice.wallet.imported.PostTokenResponse
import org.bouncycastle.util.encoders.Base64
import org.junit.jupiter.api.Test
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClientResponseException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.random.Random


class WalletTest
{
//    1.	In the Airline’s Booking Application, the traveller (aka the user) is presented with the Initialization QR Code containing a token that contains just enough information to initiate the validation sequence. (Note the size of the data in the Initiating QR code is small so that devices with limited functionality are supported.)
//    2.	Using the URL in the QR code the user navigates to the wallet application.
//    3.	The wallet parses the QR Code for the Airline  s Identity Document URL.
//    4.	The wallet gets the Airline  s Identity Document and obtains the URL for the /token endpoint.
//    5.	The wallet GETs the Validation Service’s Identity document and parses it for the DCC encryption public key.
//    6.	The wallet POSTs the Initiating Token from the QR Code to the /token endpoint to obtain the Validation Access Token and a nonce. The wallet now has the full trip information.
//    7.	The wallet displays the trip information from the Validation Access Token and the UX info in the QR code to the user and obtains explicit consent to proceed.
//    8.	The wallet enables the user the upload the QR code of a DCC
//    9.	Using the nonce and the encryption key of the Validation Service, encrypt the DCC and POST to the /validate URL along with the Validation Access Token.
//    10.	The wallet displays the results to user. If not successful, the user may go back to step 8 and try again.
//    11.	If successful, the user may send the result to the airline. The wallet POSTs the Confirmation Token to the Airline  s /callback endpoint.

    private val airlineBaseUrl = "http://localhost:8081"

    @Test
    fun main()
    {
        var initiatingTokenPayloadJson = "{\"protocol\":\"DCCVALIDATION\",\"protocolVersion\":\"2.00\",\"serviceIdentity\":\"http://localhost:8081/identity\",\"privacyUrl\":\"privacy policy url...\",\"token\":\"eyJraWQiOiJMNlRjam5iWmdlND0iLCJhbGciOiJSUzI1NiJ9.eyJpc3N1ZXIiOiJodHRwOi8va2VsbGFpci5jb20iLCJpYXQiOjEwMDAwMDAwLCJzdWIiOiIwMTIzNDU2Nzg5QUJDREVGMDEyMzQ1Njc4OUFCQ0RFRiIsImV4cCI6MjAwMDAwMDB9.Iw033pODh2JKrDZW10Tq2_wAzlThnTmnYBlPtya5wrV44-08dpR7hHnssD0RzEEgwOrNCxKt5i2cjwHViT8VmzVzQUO-Y9tzhDShVzifVd8SEU6W6t9g8gmRdMJXXOwkFsF5cGB1GCsB_r1hDeKp_5CxDmw9D4J_cP41u1Sjsu-eFK7iAJnAiJlmFi8sgxOICHFzQGZk0jppFiaAxBJmjuU0GHdQbK6OQOOKGoLReMasgy-nbNr9AMpqhFteDUayiP81UzPEtW-pgKhNHhzzDu85nC6C3dmCEGvh915BxYfA4IagRWwmXxkD_v6vUEZuW3AmoytHr_qDQJsXntK5PypTg2cQmXAdPYYHr60DIkGLRwyf1kxXjoSN3AyV2P1H8J-fdfjtBfqnzZtVqUNMmj5ton28KoH9lTWXx0hOMbLFZ49LSsLLoT79Ma4NzIVLnYBG8iWjzmYLmZbRixi9eOURb7fSQb-vexGM1t--Dj35YeMQzz6LuPEc_5bb_1aZ-dABHr6TLJM0gI2a5DEUnNmmuDSe-fBwjDEpkL8v3Pspat9XFzHjAcXOV78bojowS7KcezpRQMMYvtx51EUk26yyrGGSrc0GC4RSi52QAnbG9BYG1ht4InVxO9V7edGJgLYuyVvQtbxGU6pBS7Nn1KKo18VbyicaBCfGHtTurrc\",\"consent\":\"informed consent text...\",\"subject\":\"0123456789ABCDEF0123456789ABCDEF\",\"serviceProvider\":\"Kellair\"}"
        var initiatingTokenPayload = Gson().fromJson(initiatingTokenPayloadJson, InitiatingQrPayload::class.java)

        //Gather identities
        val airlineIdentityUrl = initiatingTokenPayload.serviceIdentity
        val airlineIdentity = getIdentity(airlineIdentityUrl)
        val tokenUrl = findTokenUrl(airlineIdentity)

        //get vat and iv
        val postTokenResult = postToken(tokenUrl, initiatingTokenPayload)
        val vatJwt = postTokenResult.validationAccessToken
        val vat = getVatPayload(vatJwt)
        val iv = Base64.decode(postTokenResult.nonce)
        val validationEncryptionJwk = Gson().fromJson(Base64.decode(postTokenResult.encKeyBase64).toString(Charsets.UTF_8),PublicKeyJwk::class.java)

        println("/token done")

        val validationEncryptionPublicKey = CryptoKeyConverter.decodeAsn1DerPkcs1X509Base64ToPublicKey("RSA", validationEncryptionJwk.x5c[0])

        //get dcc
        val dcc = "HC1:NCF7X3W08$\$Q030:+HGHBFO026M6M4NHSU5S8CUHC9:R7YL8YW7.6JUVM0\$U7UQ%-B/$5X$59498\$BLQH*CT4TJ603TRTIHKK5H6%EJTEPTSIWEJFAYMPRMQ%BOPUGIL3P2CHZMZ35D:2G7JU5E0B180OZ0W7:O/C5TAVVO5X\$D+BTE\$C:EWP 1Z$29%IDM5-6Q+F56U0ZBI0*8UEADP2LBLM9QH7HWPIYMGCXA5773R7 HFOV0-VG::AP9POXD6-J/WM4*R*O7HZSJG3NYTLQFT5D51V 4C5D8M-JOYLNTC*%MG5LWI1 H7%DNGUQ%3RQ\$H4DQULL905%*1ZV6S9GIUHL103BHVUVZU9 0I%:DVF1H21LCCLCB\$W4HVN%2BN7CLK07BG/PS:W3\$M91WI5N02 QASQNY81+6BJ9OFERUK-AHWU5I7L-%2ZY9J/G:-A/UFGIIETL6 G41W00JIRP-NMJLP.W7 7BHJ0J V%%HR/HMWQG+CVX9NQABH8129 G6KD38L4I6G$6WT.73ZR80WH+582DUZ1O3G.9NGWNTZJ*.6NCVLUR0IH2SLH.SMPU 0PL:MDAOENSYUIJ9NH9OK8BZ6S:38IME6OE*QVUSR1FW+:QU0"
        //encrypt
        val secretKey = Random.Default.nextBytes(32)
        val encryptedSecretKey = RsaEcbOaepWithSha256DecryptCommand().testEncrypt(secretKey, validationEncryptionPublicKey)
        val encryptedDcc = AesCbcDecryptCommand().testEncrypt(dcc.toByteArray(Charsets.UTF_8), secretKey, iv)
        val scheme = RsaOaepWithSha256AesCbcSchemeDecryptCommand.Name
        //Validate
        val validateResponse = postValidate(vat.validationUrl, vatJwt, scheme, validationEncryptionJwk.kid, encryptedDcc, encryptedSecretKey)
        val confirmationToken = getResultTokenPayload(validateResponse)
        //Tell airline
        postCallback(getCallbackUrl(airlineIdentity), vatJwt, confirmationToken)
    }

    private fun postCallback(callback: String, vatJwt: String, resultTokenPayload: ResultTokenPayload) {

        var body = CallbackRequestBody(resultTokenPayload.confirmation)

        val bodyJson = Gson().toJson(body)

        val request = HttpRequest.newBuilder()
            .uri(URI.create(callback))
            .setHeader("authorization", "bearer $vatJwt")
            //.setHeader(Headers.Version, Headers.V2)
            //.setHeader("accept", Headers.Json)
            .setHeader("content-type", Headers.Json)
            .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
            .build()

        val response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString())
        println(response)
        if (response.statusCode() != HttpStatus.OK.value())
            throw Exception()
    }

    private fun getResultTokenPayload(validateResponseJwt: String): ResultTokenPayload {
        val splitToken = validateResponseJwt.split(".");
        val unsignedToken = splitToken[0] + "." + splitToken[1] + ".";
        var body = Jwts.parserBuilder().build()
            .parse(unsignedToken).body

        val gson = Gson()
        return gson.fromJson(gson.toJson(body), ResultTokenPayload::class.java)
    }

    private fun getVatPayload(jwt: String): ValidationAccessTokenPayload {
        val splitToken = jwt.split(".");
        val unsignedToken = splitToken[0] + "." + splitToken[1] + ".";
        var body = Jwts.parserBuilder().build()
            .parse(unsignedToken).body

        val gson = Gson()
        return gson.fromJson(gson.toJson(body), ValidationAccessTokenPayload::class.java)
    }

    private fun getCallbackUrl(airlineIdentity: IdentityResponse): String {
        return airlineIdentity.service.find {it.type.equals("ResultTokenService", ignoreCase = true)}?.serviceEndpoint ?: throw Exception()
    }

    private fun findEncryptionKey(validationIdentity: IdentityResponse): PublicKeyJwk {
        return validationIdentity.verificationMethod.find{it.publicKeyJwk?.use.equals("enc")}?.publicKeyJwk ?: throw Exception()
    }

    private fun findValidateUrl(validationIdentity: IdentityResponse): String {
        return validationIdentity.service.find {it.type.equals("ValidationService", ignoreCase = true)}?.serviceEndpoint ?: throw Exception()
    }

    private fun postValidate(
        url: String,
        vatJwt:String,
        scheme: String,
        encryptionPublicKeyKid: String,
        encryptedDcc: ByteArray,
        encryptedSecretKey: ByteArray,
    ) : String {

        var body = ValidateRequestBody(
            encryptionKeyId = encryptionPublicKeyKid,
            encryptedDcc = Base64.toBase64String(encryptedDcc),
            encryptedDccSignature = "",
            encryptedDccSignatureAlgorithm = "",
            encryptedScheme = scheme,
            encryptionKey = Base64.toBase64String(encryptedSecretKey)
        )

        var bodyJson  = Gson().toJson(body)

        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .setHeader(Headers.Version, Headers.V2)
            .setHeader("accept", Headers.Json)
            .setHeader("content-type", Headers.Json)
            .setHeader("authorization", "bearer $vatJwt")
            .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
            .build()

        val response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString())
        println(response)
        if (response.statusCode() != HttpStatus.OK.value())
            throw Exception()

        return response.body()
    }

    //TODO this is out of step - intiatingToken belongs in the header and the keys in the body.
    private fun postToken(tokenUrl: String, initiatingToken: InitiatingQrPayload): PostTokenResponse {
        try
        {
            var bodyJson  = Gson().toJson(initiatingToken)

            val request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .headers("content-type", "application/json")
                .build()

            val response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() != HttpStatus.OK.value())
            {
                throw IllegalStateException()
            }

            return PostTokenResponse(
                response.body(),
                response.headers().firstValue("x-nonce").get(),
                response.headers().firstValue("x-enc").get(),
                response.headers().firstValue("x-sig").get()
            )
        }
        catch(e: RestClientResponseException)
        {
            println(e)
            throw e
        }
    }

    private fun findTokenUrl(airlineIdentity: IdentityResponse): String {
        return airlineIdentity.service.find {it.type.equals("AccessTokenService", ignoreCase = true)}?.serviceEndpoint ?: throw Exception()
    }

    fun getIdentity(url: String): IdentityResponse
    {
        try
        {
            val response = RestTemplateBuilder().build().getForEntity(URI(url), IdentityResponse::class.java)
            println(response.statusCode)
            println(response.body)
            //var result = ObjectMapper().readValue(response.body!!, IdentityResponse::class.java)
            //var result = Gson().fromJson(response.body!!, IdentityResponse::class.java)
            return response.body!!
        }
        catch(e: RestClientResponseException)
        {
            println(e)
            throw e
        }
    }
}


data class CallbackRequestBody (

    var confirmationToken: String
)

