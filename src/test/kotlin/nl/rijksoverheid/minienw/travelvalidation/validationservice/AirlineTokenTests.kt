package nl.rijksoverheid.minienw.travelvalidation.validationservice

import com.google.gson.Gson
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.ValidationAccessTokenPayload
import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.data.PublicKeyJwk
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import java.time.Instant


public class AirlineTokenTests() {

    @Test
    fun create() {

        var ttt = BusinessTransactionTests().sanityCheckValidationAccessTokenPayload()

        val payloadJson = Gson().toJson(ttt)
        //val airlineKeys = Keys.keyPairFor(SignatureAlgorithm.PS256)
        //val privateKeyEncoded : String = Base64.toBase64String(airlineKeys.private.encoded)
        //val publicKeyEncoded : String = Base64.toBase64String(airlineKeys.public.encoded)

        val jws : String = Jwts.builder()
            .setPayload(payloadJson)
            //.signWith(airlineKeys.private)
            .setHeaderParam("kid", "SsXyRIVSy4Y=")
            .signWith(CryptoKeyConverter.decodeAsn1DerPkcs8Base64ToPrivateKey("RSA", privateKeyEncoded), SignatureAlgorithm.RS256)
            .compact()

        val appSettings = mock(IApplicationSettings::class.java)
        `when`(appSettings.demoModeOn).thenReturn(true)
        `when`(appSettings.demoModePassAllDccs).thenReturn(true)

        val sigKeys = mock(IAirlineSigningKeyProvider::class.java)
        `when`(sigKeys.get("SsXyRIVSy4Y=","RS256"))
            .thenReturn(PublicKeyJwk(x5c = arrayOf(publicKeyEncoded), kid="SsXyRIVSy4Y=", alg="RS256",use="sig" ))

        val dtp = mock(IDateTimeProvider::class.java)
        `when`(dtp.snapshot()).thenReturn(Instant.now()) //TODO test with specific time

        val resolver = AirlineSigningKeyResolverAdapter(sigKeys)

        val result = ValidationAccessTokenParser(appSettings, dtp, resolver).parse(jws)

        assert(result.statusCode == HttpStatus.OK)
        val body = result.body as ValidationAccessTokenPayload
        assert(body.subject == ttt.subject)
    }

    @Test
    fun parseJws()
    {
        val appSettings = mock(IApplicationSettings::class.java)

        val sigKeys = mock(IAirlineSigningKeyProvider::class.java)
        `when`(sigKeys.get("SsXyRIVSy4Y=","RS256"))
            .thenReturn(PublicKeyJwk(x5c = arrayOf(publicKeyEncoded), kid="SsXyRIVSy4Y=", alg="RS256",use="sig" ))

        val resolver = AirlineSigningKeyResolverAdapter(sigKeys)

        val snapshot = Instant.now()
        val dtp = mock(IDateTimeProvider::class.java)
        `when`(dtp.snapshot()).thenReturn(snapshot) //TODO test with specific time

        var result = ValidationAccessTokenParser(appSettings, dtp, resolver).parse(testVat)
        assert(result.statusCode == HttpStatus.OK)
        assert(result.body!!.subject == "ABCD0123ABCD0123ABCD0123ABCD0123")
    }

    companion object {
        const val KellAir = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEArCRwFFoxKmxHbmd6o65sGawUI+ck6qTQvVhn+iTn6HG6CcQ7g02bOLi/sx+0x7UGLpBI7w94ma8r172joqlTjWme6LCyhxwTD2OdOn39btWI86BDN+rUkekit1gDQAcesOKC4EhUJX4xUOfnBukKPZ7nIgljAcSOCYbTHdgxrMt+uifVXDUjkeJOwiYetpNabAjfcAXpMlhcgn6eGJ5Cnm6GdXqup19HH+9zlA0RozY82EkabqvnM6+tQ4RUvaUdChwtCJ+VUqTo9XfhoIyIFjYn6wJrC1rZdyP9lrMiFeNxuhwoZLPkxCe3TVQkykjOkF8ktcUBJz40fjI006XrkOA/VGT9GBN3e6t2rqFPq460fBkpWEcJCgVi7+yMT8Fna2H/kUU3GvWCsZs75HdY4EfZhxkwULyocgmdg/2uWYYE9sF0Z6mEKcZyg5h+n1Lp8Sz6veOFlLX/mAd/K7N5W/Id4CiBkg7bF0jiUh0ZgjZdlWHk3POoQ/Glt1UkWI67Cg47BliJikuFILURUhxu/MT9Gth+RYMO3TQc0VlhBxx3Z19xn1Xoof1v2vA2uQPPczckupPmzNLEsty3vtmHz0QUzB09J/JtS2XdlQQfhrFw49EroYKHj0D/zXkd+Bhqqp4sDggJFdidFmJgnUTEqW3HnWLyXkX3sNhwgUYWpY0CAwEAAQ=="
        const val privateKeyEncoded = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDPUbaopyZqDK5TuscJsodGFuYzwKiQ5e/58mQxHfyVVhqOe/9bak7hDeHUwiu/ctuQRcNYQBDPv1Qv00sfCTUZb4ZiQG0AcMkUcqrhkzLfqvKNyTDVPrTmVDwo4l+QAshN40KbQbornxIC9DOy9/gBjHNLbWb2SG6KlRBHCW0YCJ5ErkMIcwxRqUML/74c2oasvJPh8TXGmGhp/KX2/0Jgb5XuBihMmLfvgBkUsp1yYSu6GkMiEeZv4L0Nxi8a6xJacc7i2km2F20i7nhUC6dCLnbEZS2nlMhS5AkFNHNkhCXFLXjE3jp6I0nxchoblZg78IFoZb4eo8WC/ETdht+vAgMBAAECggEAAWCuIY3GzBKcf/LwSqi0jGsviW2A0UGt6A/LyaXE3Wsujv7U8hMRRh6U/xhxOPr26Y0FIMuAMRk44n55c9HYV/xtPK7n6oZVV7zxVMFpJ6JXrUiGegxKIHOz2KO0dd0G3rmhYi28InkSohFkXI6YR85an8gw1HcjwOj/rDEsV+oHr0v5KRDXSiHv2CIRmyqSm5Y2vGTnuOziyAj6wemlkmRxoxgI5cSvOZwicRYTCLkiUM/Pyvhl/fjrOlUFsaXu3pt87AaQcNtzd/MxkP8mx0h2qf1G0PqWapeWRNeW8MN4ILlmPuTshQ+EarJSLgGOnZs1yHFmbMf5KVBC0sJxMQKBgQDi4F3ourmBrqPsMjLzGcJ4wZkKEXsvfV2ocot/zWPY4BDp+ISBxynhm55dTY3iC7/cv5b7Pf3Cwt5nFRSdQTuA1BgOB/vccPx4gOR48SPnu1HM2FbLBUtK/bnyVmqdyOy41AlNPHZEd70pYh7Zkw8u+3eg9OG8cbl7N/z3um3vXwKBgQDp7qersfLwpBA6TMWZM8dcTNLQ4NZcNXk7dRWRzH4HmtAbCWRDlm6MFok8Fsj1txBis+X5izQyvdCL53/h/SzEDe3eiElufeT6RolA0UoMRvCuW8dfQC0KmaUG2l76I7or332JtPtDUXC4rqJjUSE7mVZCh2Tt4GkAWghVD4sBsQKBgQDKRDX1EuPoFgbWwTpjOYgDmLYUH9mvURl64AQ2gKqP49OwhHu+KA3SsabSkB36dXcsGzND8tSWGp2lUUn7mPivtmdruvp0Ydb0qBUJLhNQudQHyKg4gBPMW8C3qQuMeGFztrZEQa8/A3GFv5C5cT6MKG1S9n96PfmbPSMuyOrSCQKBgQCFa2oR0MIgYVIoDhU81X1EtUQfgbyAhrcIJDUTHLBcof1DDk4zBh8yor/Ol6gyDCC6SqRFeRFiR0QkJvqW55CamN8r5ksLS55G5+fsIfG7otIuf4XsW2vU+eBXUkCNJcdOsn6lBaWwtUg27NMNFf6AQWojM5lq2mzImJDcAqtjYQKBgQDghCvIDfvlF0AJba8BHcmo7EMiB67OfBKQ6aPcjY7wSUqYHv2WVeCKgxOKC5O1RtdmnX7QDXdNuG8DaICCQkcWSvWM0C0W6gqzARK77CvIB5ch68zH+mX77UDHiNFeg2T8P2ExttTEtAAGGhZa+4W1rzNF+f10FvT15zL8W+iKzA=="
        const val publicKeyEncoded ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz1G2qKcmagyuU7rHCbKHRhbmM8CokOXv+fJkMR38lVYajnv/W2pO4Q3h1MIrv3LbkEXDWEAQz79UL9NLHwk1GW+GYkBtAHDJFHKq4ZMy36ryjckw1T605lQ8KOJfkALITeNCm0G6K58SAvQzsvf4AYxzS21m9khuipUQRwltGAieRK5DCHMMUalDC/++HNqGrLyT4fE1xphoafyl9v9CYG+V7gYoTJi374AZFLKdcmEruhpDIhHmb+C9DcYvGusSWnHO4tpJthdtIu54VAunQi52xGUtp5TIUuQJBTRzZIQlxS14xN46eiNJ8XIaG5WYO/CBaGW+HqPFgvxE3YbfrwIDAQAB"

        const val testVat = "eyJraWQiOiJTc1h5UklWU3k0WT0iLCJhbGciOiJSUzI1NiJ9.eyJpZCI6IjEyMzQ1Njc4OTBBQkNERUYxMjM0NTY3ODkwQUJDREVGIiwic2VydmljZVByb3ZpZGVyIjoiaHR0cDovL2dvZmx5eW91cnNlbGYuY29tIiwid2hlbklzc3VlZCI6MTY0NjU2MDQ5Nywic3ViamVjdCI6IkFCQ0QwMTIzQUJDRDAxMjNBQkNEMDEyM0FCQ0QwMTIzIiwic3ViamVjdFVyaSI6Imh0dHBzOi8vc3ViamVjdC5jb20iLCJ3aGVuRXhwaXJlcyI6MTc0NjU2MDQ5NywidmFsaWRhdGlvblR5cGUiOiJGdWxsIiwidmFsaWRhdGlvblZlcnNpb24iOiIyLjAwIiwidmFsaWRhdGlvbkNvbmRpdGlvbiI6eyJsYW5ndWFnZSI6ImVuIiwiZmFtaWx5TmFtZVRyYW5zbGl0ZXJhdGVkIjoiayIsImdpdmVuTmFtZVRyYW5zbGl0ZXJhdGVkIjoicyIsImRhdGVPZkJpcnRoIjoiMTk0NCIsInBvcnRPZkFycml2YWwiOiJBTVMiLCJwb3J0T2ZEZXBhcnR1cmUiOiJGUkEiLCJjb3VudHJ5T2ZBcnJpdmFsIjoiTkwiLCJjb3VudHJ5T2ZEZXBhcnR1cmUiOiJERSIsInJlZ2lvbk9mQXJyaXZhbCI6InNkYXNkIiwicmVnaW9uT2ZEZXBhcnR1cmUiOiJzZGZzZGYiLCJkY2NUeXBlcyI6WyJ2Il0sImNhdGVnb3JpZXMiOlsiY2F0Li4uIl0sInZhbGlkYXRpb25DbG9jayI6IjIwMjEtMDEtMjlUMTI6MDA6MDArMDE6MDAiLCJ3aGVuVmFsaWRTdGFydCI6IjIwMjEtMDEtMjlUMTI6MDA6MDArMDE6MDAiLCJ3aGVuVmFsaWRFbmQiOiIyMDIxLTAxLTI5VDEyOjAwOjAwKzAxOjAwIn19.JHqbRPHm3_sYWrPmGqn2oMniRcLj00ENgiYsiyS57bo75A0YU04PVkqLW4nrq8ibFQ45ZFzVqPLcHOflf_weDYsxLGcjyEyzrNaE1f1_w4ssGahhz1DANYt3Osum-cJIz5HCRo12P9NcIkDLcaHukp8s3DBHb60TZyxJ5fX9wj0BPUaej_yGy-PVEaEpEIGr-rVagS7FPxXf823U71hVrw39KUngsdaqXb3tCF7rf-REs0aS3ckH5w-ZNNB6CysqdUS3RBPGgFM-7nlzWuEzF8Nv2BD566lvxLhDczxvb9RYgiAkUddcszC47N1cH-c-zLvjR2EOfAg8qwM-BelUyw"
        //const val testAirlineTttJws = "eyJhbGciOiJSUzI1NiJ9.eyJqc29uVG9rZW5JZGVudGlmaWVyIjoiaWZpZmlmaWZpIiwic2VydmljZVByb3ZpZGVyIjoiYXJnbGUiLCJ3aGVuSXNzdWVkIjozNDIxLCJzdWJqZWN0IjoiQUJDRURFIiwic3ViamVjdFVyaSI6Imh0dHBzOi8vc3ViamVjdC5jb20iLCJ3aGVuRXhwaXJlcyI6LTEyMzQsInZhbGlkYXRpb25UeXBlIjoiRnVsbCIsInZhbGlkYXRpb25WZXJzaW9uIjoiVjEyMzMiLCJ2YWxpZGF0aW9uQ29uZGl0aW9uIjp7IkRjY0hhc2giOiJzZGFhc2RhZCIsIkxhbmd1YWdlIjoiZW4iLCJTdXJuYW1lVHJhbnNsaXRlcmF0ZWQiOiIiLCJHaXZlbk5hbWVUcmFuc2xpdGVyYXRlZCI6IiIsIkRhdGVPZkJpcnRoIjoiMTk3OS0wNC0xNCIsIkNvdW50cnlPZkFycml2YWwiOiJOTCIsIkNvdW50cnlPZkRlcGFydHVyZSI6IkRFIiwiUmVnaW9uT2ZBcnJpdmFsIjoic2Rhc2QiLCJSZWdpb25PZkRlcGFydHVyZSI6InNkZnNkZiIsIlR5cGUiOlsidiJdLCJDYXRlZ29yeSI6WyJjYXQuLi4iXSwiV2hlbk11c3RCZVZhbGlkIjpbIjIwMjEtMDEtMjlUMTI6MDA6MDArMDE6MDAiXSwiV2hlblZhbGlkU3RhcnQiOlsiMjAyMS0wMS0yOVQxMjowMDowMCswMTowMCJdLCJXaGVuVmFsaWRFbmQiOlsiMjAyMS0wMS0yOVQxMjowMDowMCswMTowMCJdfX0.VZgGqVyk8JV5GO127RaiLyd3nd34ZoXtVGuUK96bU7o7t_JOA-wIcjL0TYrUjN8OQpKVeQwKdkeuKXHXINza-4f3_ftUftVrE1XChxHI4OPKMhpHTwjXJgd9NWkWkxlOedr4BtvrfeiYo3I0XU8-7dldO_C6AiirDWO_3BLxpSxdNiEjbXBBrdLS5cvTVmDI98Jh7o0Ge1F5Axlsg4thsnsqw337kYBPmOVUp15bxs37U61BodZ2sXOLomzII2_6C2eWcfI_riRItq4urVSaBEf16MYnWcrJApwwTG03s_VFOdWgp8rKcjhRYj6g9mvCj21qPGcNQTbxChu4gLd9AA"
    }
}
