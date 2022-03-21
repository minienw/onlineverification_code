package nl.rijksoverheid.minienw.travelvalidation.validationservice.servicetests

import com.google.gson.Gson
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.CryptoKeyConverter
import org.bouncycastle.util.encoders.Base64
import org.junit.jupiter.api.Test

class CryptoKeyParserTests
{
    companion object {
        //TODO der? :(
        val privateKeyPem =
            "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"
        val publicKeyPem =
            "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEArCRwFFoxKmxHbmd6o65sGawUI+ck6qTQvVhn+iTn6HG6CcQ7g02bOLi/sx+0x7UGLpBI7w94ma8r172joqlTjWme6LCyhxwTD2OdOn39btWI86BDN+rUkekit1gDQAcesOKC4EhUJX4xUOfnBukKPZ7nIgljAcSOCYbTHdgxrMt+uifVXDUjkeJOwiYetpNabAjfcAXpMlhcgn6eGJ5Cnm6GdXqup19HH+9zlA0RozY82EkabqvnM6+tQ4RUvaUdChwtCJ+VUqTo9XfhoIyIFjYn6wJrC1rZdyP9lrMiFeNxuhwoZLPkxCe3TVQkykjOkF8ktcUBJz40fjI006XrkOA/VGT9GBN3e6t2rqFPq460fBkpWEcJCgVi7+yMT8Fna2H/kUU3GvWCsZs75HdY4EfZhxkwULyocgmdg/2uWYYE9sF0Z6mEKcZyg5h+n1Lp8Sz6veOFlLX/mAd/K7N5W/Id4CiBkg7bF0jiUh0ZgjZdlWHk3POoQ/Glt1UkWI67Cg47BliJikuFILURUhxu/MT9Gth+RYMO3TQc0VlhBxx3Z19xn1Xoof1v2vA2uQPPczckupPmzNLEsty3vtmHz0QUzB09J/JtS2XdlQQfhrFw49EroYKHj0D/zXkd+Bhqqp4sDggJFdidFmJgnUTEqW3HnWLyXkX3sNhwgUYWpY0CAwEAAQ=="
        val publicKeyJwkJson =
            "{\"kty\":\"RSA\",\"e\":\"AQAB\",\"kid\":\"60efcb8a-68f8-402c-a61c-4cda228ea3da\",\"n\":\"rCRwFFoxKmxHbmd6o65sGawUI-ck6qTQvVhn-iTn6HG6CcQ7g02bOLi_sx-0x7UGLpBI7w94ma8r172joqlTjWme6LCyhxwTD2OdOn39btWI86BDN-rUkekit1gDQAcesOKC4EhUJX4xUOfnBukKPZ7nIgljAcSOCYbTHdgxrMt-uifVXDUjkeJOwiYetpNabAjfcAXpMlhcgn6eGJ5Cnm6GdXqup19HH-9zlA0RozY82EkabqvnM6-tQ4RUvaUdChwtCJ-VUqTo9XfhoIyIFjYn6wJrC1rZdyP9lrMiFeNxuhwoZLPkxCe3TVQkykjOkF8ktcUBJz40fjI006XrkOA_VGT9GBN3e6t2rqFPq460fBkpWEcJCgVi7-yMT8Fna2H_kUU3GvWCsZs75HdY4EfZhxkwULyocgmdg_2uWYYE9sF0Z6mEKcZyg5h-n1Lp8Sz6veOFlLX_mAd_K7N5W_Id4CiBkg7bF0jiUh0ZgjZdlWHk3POoQ_Glt1UkWI67Cg47BliJikuFILURUhxu_MT9Gth-RYMO3TQc0VlhBxx3Z19xn1Xoof1v2vA2uQPPczckupPmzNLEsty3vtmHz0QUzB09J_JtS2XdlQQfhrFw49EroYKHj0D_zXkd-Bhqqp4sDggJFdidFmJgnUTEqW3HnWLyXkX3sNhwgUYWpY0\"}"
    }

    @Test
    fun roundTripPem()
    {
        val plainTextPem = Base64.decode(privateKeyPem).toString(Charsets.UTF_8)
        println(plainTextPem)
    }

    @Test
    fun verificationJwk()
    {
        var argle = CryptoKeyConverter.encodeRs256VerificationJwk(CryptoKeyConverter.decodeAsn1DerPkcs1X509Base64ToPublicKey("RSA", publicKeyPem))
        var final = Gson().toJson(argle)
        println(final)
    }

    //dWikdAbpdoI=
    //{"kid":"dWikdAbpdoI\u003d","alg":"RS512","use":"sig", "x5c":["MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEArCRwFFoxKmxHbmd6o65sGawUI+ck6qTQvVhn+iTn6HG6CcQ7g02bOLi/sx+0x7UGLpBI7w94ma8r172joqlTjWme6LCyhxwTD2OdOn39btWI86BDN+rUkekit1gDQAcesOKC4EhUJX4xUOfnBukKPZ7nIgljAcSOCYbTHdgxrMt+uifVXDUjkeJOwiYetpNabAjfcAXpMlhcgn6eGJ5Cnm6GdXqup19HH+9zlA0RozY82EkabqvnM6+tQ4RUvaUdChwtCJ+VUqTo9XfhoIyIFjYn6wJrC1rZdyP9lrMiFeNxuhwoZLPkxCe3TVQkykjOkF8ktcUBJz40fjI006XrkOA/VGT9GBN3e6t2rqFPq460fBkpWEcJCgVi7+yMT8Fna2H/kUU3GvWCsZs75HdY4EfZhxkwULyocgmdg/2uWYYE9sF0Z6mEKcZyg5h+n1Lp8Sz6veOFlLX/mAd/K7N5W/Id4CiBkg7bF0jiUh0ZgjZdlWHk3POoQ/Glt1UkWI67Cg47BliJikuFILURUhxu/MT9Gth+RYMO3TQc0VlhBxx3Z19xn1Xoof1v2vA2uQPPczckupPmzNLEsty3vtmHz0QUzB09J/JtS2XdlQQfhrFw49EroYKHj0D/zXkd+Bhqqp4sDggJFdidFmJgnUTEqW3HnWLyXkX3sNhwgUYWpY0CAwEAAQ=="]}

//    @Test
//    fun pemString()
//    {
//        var pemString = CryptoKeyConverter.encodeAsn1Pkcs8Pem("RSA", Base64.decode(publicKeyPem))
//        assert(pemString.startsWith("-----BEGIN RSA-----\r\n"))
//    }
}