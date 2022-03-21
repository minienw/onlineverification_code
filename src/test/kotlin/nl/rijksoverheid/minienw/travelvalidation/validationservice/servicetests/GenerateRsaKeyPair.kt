package nl.rijksoverheid.minienw.travelvalidation.validationservice.servicetests

import com.fasterxml.jackson.databind.ObjectMapper
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.CryptoKeyConverter
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.HexStringEncoding
import org.junit.jupiter.api.Test
import java.security.KeyPair
import java.security.KeyPairGenerator


class GenerateRsaKeyPair {

    @Test
    fun newRsa4096KeyPair()
    {
        val keyPair = GenKeyPair()
        val privateKeyObject = keyPair.private //as RSAPrivateKey
        println("Private Key implementation: ${privateKeyObject.javaClass}, encodes in ${privateKeyObject.format}")
        println("Private Key as ASN1 PKCS#8 hex:")
        println(HexStringEncoding.encode(privateKeyObject.encoded))
        println("Private Key as ASN1->DER->PKCS#8 base64:")
        println(CryptoKeyConverter.encodeAsn1DerPkcs8Base64(keyPair.private))
        println("Private Key as ASN1->DER->PKCS#8->PEM:")
        var privateKeyPem = CryptoKeyConverter.encodeAsn1DerPkcs8Pem(keyPair.private)
        println(privateKeyPem)
        CryptoKeyConverter.decodeAsn1DerPkcs8PemPrivateKey(privateKeyPem)

        val publicKeyObject = keyPair.public //as RSAPrivateKey
        println("Public Key implementation: ${publicKeyObject.javaClass}, encodes in ${publicKeyObject.format}")
        println("Public Key as ASN.1->DER->PKCS#1 hex:")
        println(HexStringEncoding.encode(publicKeyObject.encoded))
        println("Public Key as ASN.1->DER->PKCS#1 base64:")
        println(CryptoKeyConverter.encodeAsn1DerPkcs1X509Base64(publicKeyObject))
        println("Public Key as ASN.1->DER->PKCS#1->PEM:")
        var publicKeyPem = CryptoKeyConverter.encodeAsn1DerPkcs1X509Pem(keyPair.public)
        println(publicKeyPem)
        CryptoKeyConverter.decodeAsn1DerPkcs1X509PemPublicKey(publicKeyPem)
        println("Public Key as RS256 JWK")
        val jwkJson = ObjectMapper().writeValueAsString(CryptoKeyConverter.encodeRs256VerificationJwk(publicKeyObject))
        println(jwkJson)

        println("Public Key as RSA encryption JWK")
        println(ObjectMapper().writeValueAsString(CryptoKeyConverter.encodeRsaEncryptionJwk(publicKeyObject)))
    }

    private fun GenKeyPair(): KeyPair {
        val kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(4096);
        val keyPair = kpg.generateKeyPair()
        return keyPair
    }

//    @Test
//    fun decodePublicKeyWithoutExplicitAlgorithm()
//    {
//        Security.addProvider(BouncyCastleProvider())
//        val buffer = GenKeyPair().public.encoded
//        val pem = CryptoKeyConverter.encodePem("CERTIFICATE", buffer)
//
//        //CryptoKeyConverter.decodeAsn1DerPkcs1X509ToPublicKey(pem.toByteArray(Charsets.UTF_8))
//    }

}