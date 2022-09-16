package nl.rijksoverheid.minienw.travelvalidation.validationservice

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.CryptoKeyConverter
import nl.rijksoverheid.minienw.travelvalidation.validationservice.servicetests.CryptoKeyParserTests
import org.bouncycastle.util.encoders.Base64
import org.bouncycastle.util.encoders.Hex
import org.junit.jupiter.api.Test

class JwtTests {
    @Test
    fun roundTrip()
    {
        val kwt = io.jsonwebtoken.Jwts.builder()
            .signWith(CryptoKeyConverter.decodeAsn1DerPkcs8Base64ToPrivateKey("RSA", CryptoKeyParserTests.privateKeyPem))
            .claim("poland", "springtime")
            .compact()

        println(kwt)

        val verified = io.jsonwebtoken.Jwts.parserBuilder().setSigningKey(
            CryptoKeyConverter.decodeAsn1DerPkcs1X509Base64ToPublicKey("RSA", CryptoKeyParserTests.publicKeyPem)
        ).build().parseClaimsJws(kwt)

        assert(verified.body.get("poland") == "springtime")
    }

    /*
    * Getting io.jsonwebtoken.security.SignatureException: Unable to verify RSA signature using configured PublicKey. Signature length not correct: got 256 but was expecting 512 when attempting to verify sig in airline stub.
    * */
    @Test
    fun BadKeyPair()
    {
        val kwt = io.jsonwebtoken.Jwts.builder()
            .signWith(CryptoKeyConverter.decodeAsn1DerPkcs8Base64ToPrivateKey("RSA", "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDPUbaopyZqDK5TuscJsodGFuYzwKiQ5e/58mQxHfyVVhqOe/9bak7hDeHUwiu/ctuQRcNYQBDPv1Qv00sfCTUZb4ZiQG0AcMkUcqrhkzLfqvKNyTDVPrTmVDwo4l+QAshN40KbQbornxIC9DOy9/gBjHNLbWb2SG6KlRBHCW0YCJ5ErkMIcwxRqUML/74c2oasvJPh8TXGmGhp/KX2/0Jgb5XuBihMmLfvgBkUsp1yYSu6GkMiEeZv4L0Nxi8a6xJacc7i2km2F20i7nhUC6dCLnbEZS2nlMhS5AkFNHNkhCXFLXjE3jp6I0nxchoblZg78IFoZb4eo8WC/ETdht+vAgMBAAECggEAAWCuIY3GzBKcf/LwSqi0jGsviW2A0UGt6A/LyaXE3Wsujv7U8hMRRh6U/xhxOPr26Y0FIMuAMRk44n55c9HYV/xtPK7n6oZVV7zxVMFpJ6JXrUiGegxKIHOz2KO0dd0G3rmhYi28InkSohFkXI6YR85an8gw1HcjwOj/rDEsV+oHr0v5KRDXSiHv2CIRmyqSm5Y2vGTnuOziyAj6wemlkmRxoxgI5cSvOZwicRYTCLkiUM/Pyvhl/fjrOlUFsaXu3pt87AaQcNtzd/MxkP8mx0h2qf1G0PqWapeWRNeW8MN4ILlmPuTshQ+EarJSLgGOnZs1yHFmbMf5KVBC0sJxMQKBgQDi4F3ourmBrqPsMjLzGcJ4wZkKEXsvfV2ocot/zWPY4BDp+ISBxynhm55dTY3iC7/cv5b7Pf3Cwt5nFRSdQTuA1BgOB/vccPx4gOR48SPnu1HM2FbLBUtK/bnyVmqdyOy41AlNPHZEd70pYh7Zkw8u+3eg9OG8cbl7N/z3um3vXwKBgQDp7qersfLwpBA6TMWZM8dcTNLQ4NZcNXk7dRWRzH4HmtAbCWRDlm6MFok8Fsj1txBis+X5izQyvdCL53/h/SzEDe3eiElufeT6RolA0UoMRvCuW8dfQC0KmaUG2l76I7or332JtPtDUXC4rqJjUSE7mVZCh2Tt4GkAWghVD4sBsQKBgQDKRDX1EuPoFgbWwTpjOYgDmLYUH9mvURl64AQ2gKqP49OwhHu+KA3SsabSkB36dXcsGzND8tSWGp2lUUn7mPivtmdruvp0Ydb0qBUJLhNQudQHyKg4gBPMW8C3qQuMeGFztrZEQa8/A3GFv5C5cT6MKG1S9n96PfmbPSMuyOrSCQKBgQCFa2oR0MIgYVIoDhU81X1EtUQfgbyAhrcIJDUTHLBcof1DDk4zBh8yor/Ol6gyDCC6SqRFeRFiR0QkJvqW55CamN8r5ksLS55G5+fsIfG7otIuf4XsW2vU+eBXUkCNJcdOsn6lBaWwtUg27NMNFf6AQWojM5lq2mzImJDcAqtjYQKBgQDghCvIDfvlF0AJba8BHcmo7EMiB67OfBKQ6aPcjY7wSUqYHv2WVeCKgxOKC5O1RtdmnX7QDXdNuG8DaICCQkcWSvWM0C0W6gqzARK77CvIB5ch68zH+mX77UDHiNFeg2T8P2ExttTEtAAGGhZa+4W1rzNF+f10FvT15zL8W+iKzA=="))
            .claim("poland", "springtime")
            .compact()

        println(kwt)

        val verified = io.jsonwebtoken.Jwts.parserBuilder().setSigningKey(
            CryptoKeyConverter.decodeAsn1DerPkcs1X509Base64ToPublicKey("RSA", "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEArTl66RAAnSYtDm6rb2dCclaozgPSr6Oxu/whHlknTke5E28YGiIWKSiuTR530GO/Wie22FHIIUoIyaZT6mmCC3XTZiQ8V+fqFGaqr7uQooNzJT6sNXRj+iqZxueDKEClry/6Rsq8mfZw+K7UD7hdn9EWfFR5VWY+PgbWPZkSaRVldCpjZrNwECAsyBNTFSDZcMJ7hoofrp/g5+qms8OjwPuc1Jw3yg0qNVig3sSDNqbXSkGimrmWWCpGZ255zCgVJbQTwOgRqrpZAoIq2sJNdKaVQ8aCwKQeZo85jcXS1iB8meG0GFiWI8/A8+mNodiAZNLxxrbiRFkh6posVbmxo/gyvlVmyaYXg09CZrNNCmicTyQ4tC7Oz0PNrr+/ZQA7UvyPnPQs1j9YGCeG1HhHwT58d9d6/01a29YHuxa+bwr/Qey4QEOX+n1+DDTGrRN9TySr/+uP+CJk2yeXBwHbywKPfC/3mOur47jCyy3aaozWkDsSZsNePfHpPjULyawt817IQ6/b3Le0oklmlpB8I+5BeicO8oEmPoFr9QCq6IxhJ1RDNJquESX5s71HS3Y8nZ98TQrZUpigI+w06IsaQgR4VCVhbn5LvE93A+RWOldaM+WvpZwHh4UoUHOBPmxof8cb5xoCUBbgel/ASMz66H9zSiFWBr2c3lXafbfMV20CAwEAAQ==")
        ).build().parseClaimsJws(kwt)

        assert(verified.body.get("poland") == "springtime")
    }
}