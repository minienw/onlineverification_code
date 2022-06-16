package nl.rijksoverheid.minienw.travelvalidation.validationservice.servicetests

import nl.rijksoverheid.minienw.travelvalidation.validationservice.commands.CheckSignatureCommand
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.CryptoKeyConverter
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.crypto.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.slf4j.*
import java.security.Security
import java.util.*

class DccEncryptionTests {

    @Test
    fun roundTripCbcVariant()
    {
        Security.addProvider(BouncyCastleProvider())

        val aesCipher = AesCbcDecryptCommand()
        val secretKeyCipher = RsaEcbOaepWithSha256DecryptCommand()

//         val buffer = ByteArray(32)
//         val key = Random().nextBytes(buffer)
//         val literal: String = Base64.encode(buffer).toString(Charsets.UTF_8);

        val publicKeyBase64 =  "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAyxEq7FtuifnkeLY5/PnNExejt3CY6FTHjIGw7tpoddMVrOSnsIwc9UBxg2L7WUuAvC8+uCWGQMhwsv49Uje5/HEG6ptj4hj3yl/3lgDvfaNA4bU33CBxP/BYDh5vqYHEqksyOJAQp3X9CbmgzDTyT5QY230n1XOTINR0KRTWQwqZqscanFTuymyxN8sb2Cupf6ZlIn+FtuOje5nkSlLR35yQRa0PeQPTAjktXLDq6Cwep6C+8dyX2ItWGCMEXnxymU/6EEBZNAQUR438qygfY+tEc1of5em+rRS6cAmU9AUmcDLeHT4AGeup3IGCgvMyIdBL95XAqFmgFScEQfdh3xNWMRlmJ5qvmPQA88dpbQBbW0LuNQAsHzJq6FZhruIY/8tFw2dkVE2xpk3Mr4KCeVFA7DIg6fntp8qp2nqoR+hVXbkOhShu7s4F6o8qAnwOGXf6wkexfpMN5WqHzeXjDp2M0ljWLJjQouR6x8iUum8ZNCNUaPSfVcLwQA3hmsf+3hz9uNFumaJA1N0/m1Z/NNKB27dD4Ip7TXPk1vkOg4id+TivEBqaz1J5IincW+Eu8hiNo4qPtWD2L1DubAEmrAK3JWIkRO3F6vRBcjVoLlyjGxQ5t11efh7TC/BvYlZxWdnxJLEOOh5aCecPL2DpWuSpwa1D5qXBvg5MaNlqE78CAwEAAQ=="
        val privateKeyBase64 =  "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"

        val secretKey = Base64.decode("+dniPBHLXJXaKMpOfHvBc7ixaNoQJFB1+hN7H7suN9o=")
//        val publicKey = CryptoKeyConverter.convertEncodedKeyToPemWithHeaderAndFooter("RSA", Base64.decode(publicKeyPem))
//        val privateKey = CryptoKeyConverter.convertEncodedKeyToPemWithHeaderAndFooter("RSA", Base64.decode(privateKeyPem))
        val iv = Base64.decode("ts5WU4w0+g//turCVWx8fQ==")
        val dcc = "HC1:NCFOXN%TSMAHN-H+XSN-7D\$AUJKAIU099C\$E 43G-VC9B5G2Q7HI9C.H94.OQWOQHIZC4TPIFRMLNKNM8JI0EUG*%NH\$RSC92GF8DTE1KO87J4F8FU:4RETF10UB/9BL5C2F\$KU8VHO0VQK96VP8BS41E3NNM78R-H9NTBY4Z7W99WD2IZD5CC9G%89-8CNNF54DABYYMAHLW 70SO:GOLIROGO3T5ZXK W5WGKZ/MPS4V77GQ0 EQPCR8U6XHQNSPO+Q*ERVH5*XKMB43Q4IOR3/RN95*28-W7AC5ADN.-O8L6IWMW KAHA508XIORW6%5L+L8NR7*.4\$HMDYA 96CX8LCNEAOWP4BD7WJ8+YMHBQ:YM/D7JSPZHQ1CQEPPXKRM.QDNQL*OXQ6CJP.NR:SQKSON64-ZOHZ5-HQM47TKRTEHGAREG3RA3/43KD3O13P-JQ20BNCPX81R69O6IR9/NN\$MPV*OH*OTHQGSOBIR*ER8JP.96SC5 76FR7 POOOGJ O--3.UBB9S31GMDPDSP+7G%/2RL3DE4:\$JE JXVU%JBJMC:/00CL82D.LE:3CQ7U7R5GH2GGP605-VOH7T:TGK.V%2LI0RIFJ6XJNJN"

        val encryptedDcc = aesCipher.testEncrypt(dcc.toByteArray(Charsets.UTF_8), secretKey, iv)
        val encryptedSecretKey = secretKeyCipher.testEncrypt(secretKey, CryptoKeyConverter.decodeAsn1DerPkcs1X509Base64ToPublicKey("RSA", publicKeyBase64))

        val appSettings = mock(IApplicationSettings::class.java)
        Mockito.`when`(appSettings.dccEncryptionRsaPrivateKey)
            .thenReturn(privateKeyBase64)

        val decryptedDcc = RsaOaepWithSha256AesCbcSchemeDecryptCommand(
            appSettings = appSettings,
            payloadDecryptionCommand = aesCipher,
            keyDecryptionCommand = secretKeyCipher
        ).execute(encryptedDcc, encryptedSecretKey, iv)
        .toString(Charsets.UTF_8)

        assert(decryptedDcc == dcc)
    }

    @Test
    fun sneakyIvGenerator() {
        val key = ByteArray(16)
        Random().nextBytes(key)
        val literal = Base64.toBase64String(key)
        println(literal)
    }

    @Test
    fun roundTripGcmVariant()
    {
        val aesCipher = AesGcmDecryptCommand()
        val secretKeyCipher = RsaEcbOaepWithSha256DecryptCommand()

        val publicKeyBase64 =  "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAyxEq7FtuifnkeLY5/PnNExejt3CY6FTHjIGw7tpoddMVrOSnsIwc9UBxg2L7WUuAvC8+uCWGQMhwsv49Uje5/HEG6ptj4hj3yl/3lgDvfaNA4bU33CBxP/BYDh5vqYHEqksyOJAQp3X9CbmgzDTyT5QY230n1XOTINR0KRTWQwqZqscanFTuymyxN8sb2Cupf6ZlIn+FtuOje5nkSlLR35yQRa0PeQPTAjktXLDq6Cwep6C+8dyX2ItWGCMEXnxymU/6EEBZNAQUR438qygfY+tEc1of5em+rRS6cAmU9AUmcDLeHT4AGeup3IGCgvMyIdBL95XAqFmgFScEQfdh3xNWMRlmJ5qvmPQA88dpbQBbW0LuNQAsHzJq6FZhruIY/8tFw2dkVE2xpk3Mr4KCeVFA7DIg6fntp8qp2nqoR+hVXbkOhShu7s4F6o8qAnwOGXf6wkexfpMN5WqHzeXjDp2M0ljWLJjQouR6x8iUum8ZNCNUaPSfVcLwQA3hmsf+3hz9uNFumaJA1N0/m1Z/NNKB27dD4Ip7TXPk1vkOg4id+TivEBqaz1J5IincW+Eu8hiNo4qPtWD2L1DubAEmrAK3JWIkRO3F6vRBcjVoLlyjGxQ5t11efh7TC/BvYlZxWdnxJLEOOh5aCecPL2DpWuSpwa1D5qXBvg5MaNlqE78CAwEAAQ=="
        val privateKeyBase64 =  "MIIJRAIBADANBgkqhkiG9w0BAQEFAASCCS4wggkqAgEAAoICAQDLESrsW26J+eR4tjn8+c0TF6O3cJjoVMeMgbDu2mh10xWs5KewjBz1QHGDYvtZS4C8Lz64JYZAyHCy/j1SN7n8cQbqm2PiGPfKX/eWAO99o0DhtTfcIHE/8FgOHm+pgcSqSzI4kBCndf0JuaDMNPJPlBjbfSfVc5Mg1HQpFNZDCpmqxxqcVO7KbLE3yxvYK6l/pmUif4W246N7meRKUtHfnJBFrQ95A9MCOS1csOroLB6noL7x3JfYi1YYIwRefHKZT/oQQFk0BBRHjfyrKB9j60RzWh/l6b6tFLpwCZT0BSZwMt4dPgAZ66ncgYKC8zIh0Ev3lcCoWaAVJwRB92HfE1YxGWYnmq+Y9ADzx2ltAFtbQu41ACwfMmroVmGu4hj/y0XDZ2RUTbGmTcyvgoJ5UUDsMiDp+e2nyqnaeqhH6FVduQ6FKG7uzgXqjyoCfA4Zd/rCR7F+kw3laofN5eMOnYzSWNYsmNCi5HrHyJS6bxk0I1Ro9J9VwvBADeGax/7eHP240W6ZokDU3T+bVn800oHbt0PgintNc+TW+Q6DiJ35OK8QGprPUnkiKdxb4S7yGI2jio+1YPYvUO5sASasArclYiRE7cXq9EFyNWguXKMbFDm3XV5+HtML8G9iVnFZ2fEksQ46HloJ5w8vYOla5KnBrUPmpcG+Dkxo2WoTvwIDAQABAoICABOgpKv0yHHop/cs8dzsTjCWu9nKtdTbAPCpRm9HyAn/bZxo+3ZajJ/96xrecdn3LMnDrUXCRsAzP4VlgorUXRMz343EmDpt3GGAth8XFw7nPSmzyOLrddbOxcwTkAprwxUMsJ17+0gcZOlQOmUShws/DsoERlng0ms9bb7cxzrypti1ZvKOWQhSyixQ4u+ZhMgUTrkvZz6+sPoM7V5rs+JvsRgy+ohhYL9ZxHMtnH71aL8eD+azwi+JhDhQBieV0sPT0aFJfvXCoS0rYa0vcx1Gy7Z0kxm3Zpk3JDAZFP8jEkyM9iAQcpUOzLvF/tXmaMWQ6w7SuCztgMYan9Q6c4iepdKTAAsOc4oxNyorNm5HutH0rAuPFqEJk64NperL6QojaNi7q7nuYEepFW6jWO1klIHcN3nezckMLhcx0tKx31qLHve9JttgsyjFVPszhEg+GNuVwy40BauMl5UFeBTDbW+iO0+gtK1Atb1VpvOJW3sZWXYx9+AgMn7aOjAP36T61sqWzIhOsKpvuCKGw4uzDX3skrj7KqnCxhdgpa9lGHULbACeQXjSnZAz5MiXt9vmQpQ72i+gaCylNYPIOvkmXsqElVga1nMPCdoY7ry8l0tWV33A95vefDSXzIwtLG9ehn4CovLB2WkwdOR+UP2a6JTtsMMsjL9h6mb5E3SJAoIBAQDvrYHvp79K+XfCZ2WYc+iy6aPbUO93jKmTiiAr2XNRQTTCaoRoklViiAULihkZ3X2YF+MmveS4XvdSkvsQSPrTk4SCOjpP9X2GWy4V8rulk/acdI7bFA/dyCEQ6ayV5wHOg6f0ckkYz41AgWDrd7+ShYehAKHkkccWOw87pb5i9Xq3Mzk1goGKtdcNML4yENc+SFMDOY/oTpV8u0YSJKyIUaE7Uz9gc/RK3LaoDEVeK/WdHdh3atigdqwWX8ljhlvNpxXlO6qxpX2Zh1RCxwP/SqdcygAke6vwTyZ/BpuGRwkExrmopNDeo1Pb3zDbgCPOaj5rTK+y54vgvScMDMx3AoIBAQDY5WWOjP/iATuIeYMYSHIhTvkyyzE9m43w+DJCQZJcabRYKzLZs7jL/sUSH65xjZL4jZY4agdd0gY/leXTFU2UDuFWnTbbMsxes4HVbfml3DWib5ow9ClUd4NlEdRnPQvB9DVrFLQx+TSfRC92BtiJum6ptTaGuYNvzLBwOq1e916mUhGeqltpe4xgWhqiva3BcJk1qTgWMkIaC5fdHFCS/EIr1RhPNrb6LZfNWK+9aUneknfoBqI/3p+5nG/TfLvlSeWasvIgiszKp/c9gKAtX5FVj8KE/YqBHhyGzXfWBVC7tfHTxlOI5V33tG5/sW/+S6S1ineT3QHq4sa5M2z5AoIBAQCMJwbLUGlQ+04nlPTY0DaroGobzlkprExhJS0o1xBS3HFwmn7w81kQs/9XPxrsgBN4Q8elmPdNROMVGzPK0xIxdJrQ2CHPLfuH/HuXhAhsi3hzBsmenD2m7+c+D5bHVX09+MjW7ZmG859isAUvzCdvE7w6zrbchM5JVqHSvFn3XhqzVDXI75a2uRyW7wDZH/XtyrcYNKnNK17IwG36IA3v7OP+X/C4gzXALQ52eC58IfKPPKufMRCZzKtWrg6Cr0exm1n3YQuws9gdVgBUyDGzMEFrxgeNTfA+JtqY/xEk1RcV/usEDdTBy/o2oW3dfTHy+zei4QYA9NtbwOA7cKSDAoIBAQCsyWqzOk5qzFKR8UNnZ+dL868AMPEkRDS6f+8B61zes/LQvnE/DzarhGWYf5anrYIk8h53CLifnD4QD6lMFE9+ILCRIKNf0kMPOm+3CGp2IEy1/7RVRV1bhe4t6RtNt0sTkymvCYpXuVCbhGVO4DtG1TdtiQhMIaO8v9LbRVNyMRy84OpafRpfmOYGqCFJ/lNapAnZvtoIjLcuKogrxG/H+J/6bUN1b4ArI4EoPHucKDGJ1yMgu5Ar031pD1/imh7RlaINMHtFQ23MEC0dWMfB/b047VKoQZdCLyvIlAzgedGMCHEDJZ7MkvTd7WaN4Vv3uGkZmpqSN+FcLpbjWT15AoIBAQCB+rOoBC16MLGBLCUw2kKr3/qpj4kFgN5bWvuy7gC/1OqHf+i5MHsot+VH35hH1RUwGrbvAGfr/Xa9HwGBXeGikckFv6x0NZFCy4zm6i3WL1lApRdiaTub2U99V/5/3g0obBif0mJqz5516VOvlhVpYjwq254OTLy3fhgEG1vImDOgdIGjJ/PiBjZGzYxb0V/N/OUkZY7OZgcNpan7gqvG7vC1HGNFBG+3mg5Wh7IpV2V8aagigHRdUceWDoGsnp6kglBYPegh4q4Drit57k8jtXezmf8ZirCUn9Psv2W+q3r31iBCuXi2zB5hYCxw0+uRm83eHRh2R3haaDrFME5r"
        val secretKey = Base64.decode("+dniPBHLXJXaKMpOfHvBc7ixaNoQJFB1+hN7H7suN9o=")
        val iv = Base64.decode("AV3c/ZuMyh2kH16A")
        val dcc = "I am a DCC"

        val encryptedDcc = aesCipher.testEncrypt(dcc.toByteArray(Charsets.UTF_8), secretKey, iv)
        val encryptedSecretKey = secretKeyCipher.testEncrypt(secretKey, CryptoKeyConverter.decodeAsn1DerPkcs1X509Base64ToPublicKey("RSA", publicKeyBase64))

        val appSettings = mock(IApplicationSettings::class.java)
        Mockito.`when`(appSettings.dccEncryptionRsaPrivateKey)
            .thenReturn(privateKeyBase64)

        val decryptedDcc = RsaOaepWithSha256AesGcmSchemeDecryptCommand(
            appSettings = appSettings,
            payloadDecryptionCommand = aesCipher,
            keyDecryptionCommand = secretKeyCipher
        ).execute(encryptedDcc, encryptedSecretKey, iv)
            .toString(Charsets.UTF_8)

        assert(decryptedDcc == dcc)
    }

    @Test
    fun verify()
    {
        val l = LoggerFactory.getLogger(CheckSignatureCommand::class.java) as Logger

        Security.addProvider(BouncyCastleProvider())
        val data = Base64.decode("AAECAwQF")
        val sig = "MEYCIQCwe5M25BzTO3eep5hL/sB8qq6v7uJDIOzdAceL73u/1AIhAPAjS0C0iV3gj6hdoPCwP1esYUkhKCzfYXmJ7rue4AP8"
        val publicKey = "MIIBMzCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////////////////////////////////////v///C8wRAQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHBEEEeb5mfvncu6xVoGKVzocLBwKb/NstzijZWfKBWxb4F5hIOtp3JqPEZV2k+/wOEQio/Re0SKaFVBmcR9CP+xDUuAIhAP////////////////////66rtzmr0igO7/SXozQNkFBAgEBA0IABOCULxUF/y0qMnG8GjQjHKX3kZx1YL/qO23X6ti7Nnx1nSSYPn2UFdHnf6+jtLcr01gHM826KeDK3C4krNmlXzg="
        assert(CheckSignatureCommand(l).isValid(data, sig, "SHA256withECDSA", publicKey))
    }
}