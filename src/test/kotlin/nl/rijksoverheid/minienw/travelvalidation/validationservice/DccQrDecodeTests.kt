package nl.rijksoverheid.minienw.travelvalidation.validationservice

import COSE.Message
import COSE.MessageTag
import com.google.gson.Gson
import com.upokecenter.cbor.CBORObject
import nl.minvws.encoding.Base45
import nl.rijksoverheid.dcbs.verifier.models.*
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.DccDecoder
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.util.zip.Inflater


class DccQrDecodeTests {

    val CBOR = "a401624e4c041a61975598061a60aa0798390103a101a46376657265312e302e30636e616da462666e6eceb5cf80cf8ecebdcf85cebccebf63666e746745504f4e594d4f62676e6acf8ccebdcebfcebcceb163676e74654f4e4f4d4163646f626a323032312d30312d3031617482a8627467693834303533393030366274746661207465737462736374323032312d30342d32355431323a34353a33315a627472606274636347474462636f62475262697378244d696e6973747279206f66204865616c74682057656c6661726520616e642053706f7274626369782f75726e3a757663693a30313a4e4c3a3563336538613834323562623463323938383330623432666431313362633562a8627467693834303533393030366274746661207465737462736374323032312d30342d32355431323a34353a33315a627472693236303431353030306274636062636f6062697378244d696e6973747279206f66204865616c74682057656c6661726520616e642053706f7274626369782f75726e3a757663693a30313a4e4c3a3336373962336263623434393434313438353332333162306165626431653766"
    val COSE = "d2844da201260448e36f053a55313513a05901aea401624e4c041a61975598061a60aa0798390103a101a46376657265312e302e30636e616da462666e6eceb5cf80cf8ecebdcf85cebccebf63666e746745504f4e594d4f62676e6acf8ccebdcebfcebcceb163676e74654f4e4f4d4163646f626a323032312d30312d3031617482a8627467693834303533393030366274746661207465737462736374323032312d30342d32355431323a34353a33315a627472606274636347474462636f62475262697378244d696e6973747279206f66204865616c74682057656c6661726520616e642053706f7274626369782f75726e3a757663693a30313a4e4c3a3563336538613834323562623463323938383330623432666431313362633562a8627467693834303533393030366274746661207465737462736374323032312d30342d32355431323a34353a33315a627472693236303431353030306274636062636f6062697378244d696e6973747279206f66204865616c74682057656c6661726520616e642053706f7274626369782f75726e3a757663693a30313a4e4c3a33363739623362636234343934343134383533323331623061656264316537665840901191fcf100456decc3e001001505bfb3f440d8c25990c8f36b24e8d650217ef03e5a9ccdb10a37d7974c231c745f81082e1c6c02cce3403a9fb535897b7569"
    val COMPRESSED = "78dabbd4e2bb88518dc5e3713eab55a8a1a9f08248c6754b1893fc7c58a412a787ce60934a58c53ec392917921e392e4b2d4a254433d033d83e4bcc4dc2549697979e7b69e6f38df776eeff9d6737bceed4f4ecb2b49770df0f78bf4f54f4acfcb3adf736eefb9fd40998dc9e97925a9fe7efebe8ec929f9495946064686ba06209458d2b422a9243dd3c2c4c0d4d8d2c0c02ca9a4242d51a124b5b824a938b904a2d044d7c834c4d0c8cac4d4cad8302aa9a42821a92439d9dddd2529393fc93d2829b3b842c537332fb3b8a4a852213f4dc1233531a72443213c35272db1285521312f4521b820bfa8242939b342bfb428cfaab42c39d3cac0d0cacfc7ca34d938d522d1c2c4c83429c924d9c8d2c2c2d820c9c4282dc5d0d03829d934890cd7651a991998189a1a1818009d9900746302452e343633b74c02ba25c9c4c4d2c4c4d0c4c2d4d8c8d830c920313529c530d53c2dc26182e0c43f1f195c73df1c7ec0c820caba7ff317871b8722279cf89cadf2e25a8062dd07bba839673772995f9feea32c5312dfc8a12793c374e6b183d5fcada69dd5a599009c1eb95d"
//    val BASE45 = "NCFOXN%TSMAHN-H+XSN-7D\$AUJKAIU099C\$E 43G-VC9B5G2Q7HI9C.H94.OQWOQHIZC4TPIFRMLNKNM8JI0EUG*%NH\$RSC92GF8DTE1KO87J4F8FU:4RETF10UB/9BL5C2F\$KU8VHO0VQK96VP8BS41E3NNM78R-H9NTBY4Z7W99WD2IZD5CC9G%89-8CNNF54DABYYMAHLW 70SO:GOLIROGO3T5ZXK W5WGKZ/MPS4V77GQ0 EQPCR8U6XHQNSPO+Q*ERVH5*XKMB43Q4IOR3/RN95*28-W7AC5ADN.-O8L6IWMW KAHA508XIORW6%5L+L8NR7*.4\$HMDYA 96CX8LCNEAOWP4BD7WJ8+YMHBQ:YM/D7JSPZHQ1CQEPPXKRM.QDNQL*OXQ6CJP.NR:SQKSON64-ZOHZ5-HQM47TKRTEHGAREG3RA3/43KD3O13P-JQ20BNCPX81R69O6IR9/NN\$MPV*OH*OTHQGSOBIR*ER8JP.96SC5 76FR7 POOOGJ O--3.UBB9S31GMDPDSP+7G%/2RL3DE4:\$JE JXVU%JBJMC:/00CL82D.LE:3CQ7U7R5GH2GGP605-VOH7T:TGK.V%2LI0RIFJ6XJNJN"
    val PREFIX = "HC1:NCFOXN%TSMAHN-H+XSN-7D\$AUJKAIU099C\$E 43G-VC9B5G2Q7HI9C.H94.OQWOQHIZC4TPIFRMLNKNM8JI0EUG*%NH\$RSC92GF8DTE1KO87J4F8FU:4RETF10UB/9BL5C2F\$KU8VHO0VQK96VP8BS41E3NNM78R-H9NTBY4Z7W99WD2IZD5CC9G%89-8CNNF54DABYYMAHLW 70SO:GOLIROGO3T5ZXK W5WGKZ/MPS4V77GQ0 EQPCR8U6XHQNSPO+Q*ERVH5*XKMB43Q4IOR3/RN95*28-W7AC5ADN.-O8L6IWMW KAHA508XIORW6%5L+L8NR7*.4\$HMDYA 96CX8LCNEAOWP4BD7WJ8+YMHBQ:YM/D7JSPZHQ1CQEPPXKRM.QDNQL*OXQ6CJP.NR:SQKSON64-ZOHZ5-HQM47TKRTEHGAREG3RA3/43KD3O13P-JQ20BNCPX81R69O6IR9/NN\$MPV*OH*OTHQGSOBIR*ER8JP.96SC5 76FR7 POOOGJ O--3.UBB9S31GMDPDSP+7G%/2RL3DE4:\$JE JXVU%JBJMC:/00CL82D.LE:3CQ7U7R5GH2GGP605-VOH7T:TGK.V%2LI0RIFJ6XJNJN"

//    "2DCODE": "iVBORw0KGgoAAAANSUhEUgAAASMAAAEjAQAAAABRW1qAAAAJ3UlEQVR4nOVavYolSXo9UXEhyqnMGkNGQdTEXVogu724UBB3HTVj6Rmm5gEms0dGOZs/s06BNJnVjrzteYB9gvLiQqO4XiEZCwvbKGriwpiTWY1QXIibn4x+AI0gFgRKO8mT+eX3c75zghH+5+v57FfcBPyfvevE2LrfYD5fz+zEts9vd89v9+xsv5637AzP7Zax86yISC3JxvDeohBqEKm1fHa+MDRbX2lZCEXO543EfI5P91enN9Q/fgyPh++BB40Kp0stS3tg9nSWN6orAPy65v1X66+36Q//Ek+/vVpg/v2rV93elG/k7d3/5u1/LSJesGMGBVZt97wWf7nY7EqRgOuX43+uTG7EM5QR7T3K/rl36QV80afLp3X7eDWJA3s8FOBLbsSend6Km1shb7HGfvPd/U1t5YsAIG/FzTf3z2ybN1eJiMj5QqtFpMrI0vLJ+NbxwahJoDFElDVXQZ1WnaFF+9qiJNROwngIWrSajC8MTSYvYqqQCu0b+EarCZwiauKdCJNBa2VlwiAyI7bEB8Hn6Cug0jRo3ke1QEIHIr7o0OftAEiVSQCYC53mM/EBnCJYlJWR0HwxYckcVRqjZBaFTq1Vg0nMoTJ8EnwSHpp3Wi2Zo0oUUxtDb0OnfWFSZXwZPXMSQlZIEKHL/Y09oXZEEYWQFVJhZGv5ADAXxsg77cvcnRzHVGA1lr7SHmZVAXObbkW62G5+eXPV7W/mzB0gQQOajzEsmiadaidrx8kGsqmMoY+pztwBEnM0W9m61DpZCA9BA9RMqbWyJDXbVOX/j7yPoY+eUeijrHQgG2bLB50KQ71NyN1zSuuhqROKiCaRSptaRzPxRdCiOUWZG5EGqEGnwgSKCTpVJnRGTSaVFDpDi0lF7nrsRFg0jS5VJhWaFp2AxGKqbaqgBsH73LODImDUGCVzYQEfDErnG0FjDAt8pcOcua9SH2kwfHS8J7QxMVIjoXWyAU2GJoQ5+3yELJAqkwqgMmExgawvoAbjWQwUU5E7Vxl5Fj2MrG2YQCPJwvDJSOZCB1kJPubnAHwmGkQYNPVOLZpPCBNkYUKn+aSpy52ri6beypKo02gpNYaPMbUkWysr0Ogky/2No0UhZKHDAFQIvaU5JgCFQKX5bJEZ8ezUiQTNv3NXk0g/0s3F3eHi/NBa1VuwUT6IVOXmqxXWn6wv9nhn19Ue1f6q0zcv+Ph8R+UWDdYXmXcrWkSqtOpJNiI1QtYEILUOjUitVaNLLDuzAkqbWpsqoCY+O9VbCaSW1CJCb9Hm5gCFCPUb/tYcfrTpAVfl5kDtzyP9zNipf9yc7lTmbQ6pJDVpyWyqXWoM2qhGGxahFp1qx0eLMjcHKB0toEFziqGPNBkajGxd6MA7oQZN2ftqT7ISgaIsI0pCTWG0atG+ACqtiFTuaZUa7WsKfUyFoQEobegAaDUYNVMY8Fdgj2JNm/XFG9zuVi8ifb1FoQGgwgc6B3M3397l7QCc94m5U7hffwZ6EKd5DPO44z3exU33OpztM+dqHQHBF4DZMEdfWl86NQm+aJqQ2ijzR/Up9PbjpQi/tPhDCQj+k96N8+ny6QCzotJ/m3srn50iUoP2BVLpUBhakBoT5ugboyZQdmbVuwQdiFIDNQG15X3ki051RGHCHNWQl3WcnX7ZrvAk53NAPytHl+aq3V69FSt6o6bjugC+uc+cq1/qQI9sfBT8Pt0e1xcMxR4Px/Sw8+/uP7blh4fc8/GTvb406QWetqAtvqEPAAohRvv8E3in17/LW4+gxaRKhJlQCFnH1BhfCclITcI3hg/IzaxWp3/Eq3/bg7bh/d9fPZR/fnitvpj/7ofzv5SP5j/O0u+JFTq37rvb9W7THa+mo/nyeFPtVlTiFnjBh+/Fq3Yja5sbUZgfNvzL/eGiBYzj8eMAX7zelHenOZ64c2VennPGv3z9sR93ncbLfjfbU1uq7+bTGT6UIy7Yz8P+BnmjilQ7PmmUlAC1aAC+ACfrS4cKNJj8OkAjqI+ydWgdJ5K1lY3wrU21VXMMFHl2zYpIFprPNjErK6B2snV8MKmOEga1k3V29qjD6KgTvo6ytbImWSAxSx1471LpVGbN6oyP99c/HQ8NQOcH2oKYC/ZAW0bW01a8PT7PeasDqXWpMp5ZVCKV0RegxcjW8cX4Anx0KTtDnrdr7Nd0lx6w/vrNDcThBR9ejv7d/ZpFAOvvc/ecAp49+vL+VQfPLF70mvWywNVz7fF0bLf+Ii8HOMOnx9UDzFuAPa7GDRr4B7Hqtyt2r9oaOK6QmVnRoFMBvgg1EhqhFqNGFxZBk06Npk7IJvPsACD++fWW/5d42clPjzeXL/79hubXp0G88l8d6ru/ZZlzlWaXoD1Ai6DJBHI0mMSsWuAZ+UanJnM9otCvFrzqcPrOroHrDtfdEZX2nxy+3XLuXmXf5phNjUiNAaMwCF+TZ5FGot7xSYO5kF/PEXyBmkyYIJnliw6DTqX1LfnGoHUeuR2WBmHSqicJ4SuBBrIyRESzo0Xw3uX2WEETUDtUWi3wjfAs8tERuQQhS6d6l9r8OnmYKRXgPYXRhUGjpVRBzTZVQg2CMnPyFWiD6oF++oePp/O/OYBe4H/z2y2LYm4//BMAffjVz/qViHz+k+6xvrUrHg9/fHsA+OlM3T7J9/b5N/8qi3r9bW5O/vCaDXo34fjL+WkSm+lp01tP281ylAUwsg+Z9w4QRd+I1DpZRyJKMCi0mh2NDoXwrfXZ3YdGqEWkQssGNBIfhKwpDMIXQjbaQ+f3WFmkBagAFmkRatJ8JL6Az071xBeTXbWmntSkUVoaBCqEkWQZOcXQgWan5uw6AGim1OjQCV9Blha19Y1WFD2EIichsnsBqbWyjoCW1WeFXINZ1MRnUjPxPuZWyc4w34Xy3FcC7/fHS4N3o6Q3nja+2B+Kncf+OvOmA5qEmoAK9FlmbF2YtK9EYhQm7VmUmbXHs9MYd19EvItsEqrbG1afLp+AI/9hA2b59OTn7KcsnJq0Lx2fQD3JRqvZyZI+eyu0mOwTORUgotBHIscnE8iBOd8gVRqF+ezzZq9HPjpFJAE0OpVW9Q4QCSKVLszZdXIQUWKOehsGkaD5gNRG2VrPrK8QBpEy81UkQE0ijCSZk6WTEBI61Y53WjbaQ8jsHKAlWdoEQ7NLtfONThBonS8tKo3a5XeuK/HZ1/AFUACVCIMAkNpIs6PeIjPiGYDTF/PNRasm7b8hyez65RgWw+vt6bpe/25L2c9ZAaDyUO0O76x8D/8eYHTd4fBeoLb4Pa1e/gpnySpIiFNbrueteK7TxYa63dUEcYaPwy79mHsrB1RnUhsVuTBH6km21n92ypijRfPMygP7f3Ca9L8B/fZoRQMtTbcAAAAASUVORK5CYII=",
//    "TESTCTX": {
//        "VERSION": 1,
//        "SCHEMA": "1.0.0",
//        "CERTIFICATE": "MIIDHjCCAsSgAwIBAgIIcszNOjtPyJQwCgYIKoZIzj0EAwIwgYYxFzAVBgNVBAMMDkNTQ0EgSGVhbHRoIE5MMQowCAYDVQQFEwEyMS0wKwYDVQQLDCRNaW5pc3RyeSBvZiBIZWFsdGggV2VsZmFyZSBhbmQgU3BvcnQxIzAhBgNVBAoMGktpbmdkb20gb2YgdGhlIE5ldGhlcmxhbmRzMQswCQYDVQQGEwJOTDAeFw0yMTA0MjYwODU3MTJaFw0zMjA0MjMwODU3MTJaMIGRMQswCQYDVQQGEwJOTDEjMCEGA1UECgwaS2luZ2RvbSBvZiB0aGUgTmV0aGVybGFuZHMxLTArBgNVBAsMJE1pbmlzdHJ5IG9mIEhlYWx0aCBXZWxmYXJlIGFuZCBTcG9ydDEKMAgGA1UEBRMBMTEiMCAGA1UEAwwZSGVhbHRoLURTQy12YWxpZC1mb3ItdGVzdDBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABAWyD0wKI32y0KoS3PanAAWfPc+94bos4DkwS9X2En2tTL5a4f5etlroGmSU4IGv3a3h+95LR9ZCdsHauV2tdtujggENMIIBCTAfBgNVHSMEGDAWgBRWpPfVnlQfVntHUN9yAwrubgs2ZTAbBgNVHRIEFDASpBAwDjEMMAoGA1UEBwwDTkxEMBsGA1UdEQQUMBKkEDAOMQwwCgYDVQQHDANOTEQwFwYDVR0lBBAwDgYMKwYBBAEAjjePZQEBMDcGA1UdHwQwMC4wLKAqoCiGJmh0dHA6Ly9jcmwubnBrZC5ubC9DUkxzL05MRC1IZWFsdGguY3JsMB0GA1UdDgQWBBSyC1SXgWhh2YR/5TUjaRJ10ul6UDArBgNVHRAEJDAigA8yMDIxMDQyNjA4NTcxMlqBDzIwMjExMTIyMDg1NzEyWjAOBgNVHQ8BAf8EBAMCB4AwCgYIKoZIzj0EAwIDSAAwRQIgYXtV0KO+QE2zwT2EWpcyc9GRKh7+Z7AHZflHv+RNR+ICIQCTW45XBctDqhjgL17gs9/yvDoIMaRAZ85obgpSftpIrw==",
//        "VALIDATIONCLOCK": "2021-05-23T09:43:20.384716",
//        "DESCRIPTION": "NL-test"
//    },

    @Test
    fun serviceDecodeTest() {
        val result = DccDecoder().parse(PREFIX)
        assert(result.dcc.name.givenName == "όνομα")
    }

    //https://github.com/eu-digital-green-certificates/dgc-testdata/blob/main/NL/2DCode/raw/004-NL-test.json
    val PREFIX_TEST2 = "HC1:NCFOXN%TSMAHN-HHYOUUHO.KY66F7P:D4*Y6-363RV%CMNV4*873.PP-I5MN5YRQHIZC4TPIFRMLNKNM8JI0EUG*%NH\$RSC9KIFA171BSHLN0/V+6TI*LAAAQQNDZB2%66ALW.I\$E7W37Y\$VL40:21/7I*DKC.U3JA\$:Q44E/%R1C9P8QGLPWGLZBV9/9-3APF6846R3R736/PK0QKH:SUZ4+FJE 4Y3LL/II 0SC9BX8+FDI J43M7EDFLEZMIM9FZ.CV*GX3E1.BLEELEACI8:NJC19BJCCD96I8YJD8JBQEATH8\$.B/H8QC8JCA6IAZE3GVMK3MSCA+G9BYI6%0795BD7WJ8+YMHBQ:YM/D7JSPZHQ1CQBOPZ016XHL:PZ%PUU1TPQVQI1OA3W1T%PEIAZ0QHTILMA0.PZO345GKIVYLSFJVPZUYF2A+SI-M*F14 7Q9EN/84Z8D.AZ:K%NIP3FHCWSB1V 7TQAAWJ4ZU%6FDZH\$VB65NJCU1IJJ-EWTFL7NMMI/HOJ+3DFJ"
    @Test
    fun serviceDecodeTest2() {
        val result = DccDecoder().parse(PREFIX_TEST2)
        assert(result.dcc.name.givenName == "\u0627\u0644\u0627\u0633\u0645 \u0627\u0644\u0623\u0648\u0644")
    }


    //https://github.com/eu-digital-green-certificates/dgc-testdata/blob/main/NL/2DCode/raw/191-NL-recovery.json
    val PREFIX_TEST3 = "HC1:NCF/Y4B8O8D0000D9CXOP/C3S/62GNVJG83UMIFK1SYC5T9AZRAQGJLQV6UNU 7N4FB*7QJKQBHNG3EX5JJM:ETBZMS4VQZM3*MC22ST0-P0IU1F-F-CV%CLXSEWZQ3IMEH0 OGQ3UPCEHH0D8LXVQ*%QTI9NZF*CCJIK5OAI*RRVEP8RCKGV01UZ8X90T7DBJ85%PW956V1WCA7SHJ+ORVJUSATQKXF1 Z2HDTAH5T A.PT 0CSXO/W6YRIB90*T0K41+:VL PE1N/600XKJ52JXH:AHG/CMKSQCC:32930:E0:WJXR8OJP2U4SMCJ:I-J4B%2OMCGGS3B12/LN4CFLL/JC76IVQ5H:DYI7QVU80DFJ8BJPGND6LNMWSRW9E6UOYR7GM.84 M08S97IK-C0.O2\$547H5XS8DXOJ.KW:GI99J9A*Q8V.KX H*QV4.H46HD7JEDAJ%SEFF-KBW1EZETO-IH*TL8TYESG5SX/PA0UQ.203ONNOU8Q6DFZMVIXTR+F/0WPLNQ7FW4VL-H/%F"
    @Test
    fun serviceDecodeTest3() {
        val result = DccDecoder().parse(PREFIX_TEST3)
        assert(result.dcc.name.givenName == "Voornaam")
    }

    //https://github.com/eu-digital-green-certificates/dgc-testdata/blob/main/NL/2DCode/raw/111-NL-vaccination.json
    val PREFIX_TEST4 = "HC1:NCFOXN%TSMAHN-H0RGPJB/IB-OMVGR099:OP3XH8/F6R5SEHP0IPIM+W4D5M%OM:UC*GP-S4FT5D75W9AV88E34L/5R3FMIAA:BIOS%XOT02NU7:EUOUT3PF 2EQ*NQIU8N86TI/QSI2ITGKO*3VHIV1IV1ILF9\$DFN1LG6H3W3 D1V5N:KU3WOO%1Q28J3UV+S6TI5MBD14\$9EW0P3*I\$PC Y3MP1+AU*YNTHP4XF5DR7:M9NT/U48KDQ58GQ3RK44R7AN8T\$7NZB.HQYW68IR-.QOU6*YQ W0XWUL0V9/9-3APF6846R3R736/PK1RM8ZAUZ4+FJE 4Y3LL/II 0OC9SX0+*B85T%6213PPHN:JJ3NVAOVOGH%IHIE9-JHGLO+IFS\$CRMP6.9GMV+KL%83.A59*TS-O-P3395L24CIGIC3-E3SP499T3QGAC5ADN.-O8L6IWMW KAHA508XIORW6%5L+L8NR7*.4\$HMDYA 96CX8LCNEAOWP4BD7WJ8+YMHBQ:YM/D7JSPZHQ1CQ/OP\$MPO76+DPH.P4NPTHQ-*O*+R2HPHU6575QQ5AKQHKR\$-OPC5QOSCUVPDD/4SY-76:L6J5H-AK%N%MS9LH-BV+4I/0GP95- QYZRSD27:RH:BPAWTNK53A7XMC.5-5K3TJYKV+K22EI0NQ A4A1V 48D00QG2 4"
    @Test
    fun serviceDecodeTest4() {
        val result = DccDecoder().parse(PREFIX_TEST4)
        assert(result.dcc.name.givenName == "V\u00e6\u00e5\u00eb\u00fd\u00fe\u00ff\u00fc\u00ef\u00f6\u0153\u0153\u00e4\u00df\u00f0\u00e8\u00e9\u00f9\u00fa\u0133\u00f0\u00f8\u00e1\u00e9\u00f3\u00fa\u00ed")
    }

    //Acceptance 2020-03-07
    val PREFIX_TEST5 = "HC1:NCF520B90T9WTWGVLK-49NJ3B0J\$OCC*AX*4FBB%91*70J+9FN0ZQC%PQWY04GC+2KD97TK0F90\$PC5\$CUZC\$\$5Y\$5JPCT3E5JDLA73467463W5/A6..DX%DZJC2/D.H8B%E5\$CLPCG/DX-CE1AL1BUIAI3DUOCT3EMPCG/DUOC+0AKECTHG4KCD3DX47B46IL6646H*6Z/E5JD%96IA74R6646307Q\$D.UDRYA 96NF6L/5SW6Y57B\$D% D3IA4W5646946846.96XJC%+D3KC/SCXJCCWENF6OF64W5KF6946WJCT3EJ+9%JC+QENQ4ZED+EDKWE3EFX3ET34X C:VDG7D82BUVDGECDZCCECRTCUOA04E4WEOPCN8FHZA1+92ZAQB9746VG7TS9FTA1N8I%63:6SM87N8 L6T0AUM8OZA.Q6.K427B.MAA2J9MPDYM+KARBW.LSU39L79-2D15RECO1EQMFP10N7CPWGI6LLPEHMHFN/R1IEV*MR1F%6RPNE07SU\$J:/7J:O238.W3V50U50QEWMQDR4"
    @Test
    fun serviceDecodeAcc() {
        val result = DccDecoder().parse(PREFIX_TEST5)
        //assert(result.dcc.name.givenName == "V\u00e6\u00e5\u00eb\u00fd\u00fe\u00ff\u00fc\u00ef\u00f6\u0153\u0153\u00e4\u00df\u00f0\u00e8\u00e9\u00f9\u00fa\u0133\u00f0\u00f8\u00e1\u00e9\u00f3\u00fa\u00ed")
        assert(result.dcc.version.isNotEmpty())
        println(Gson().toJson(result))
    }

    @Test
    fun devDecodeTest() {
        devDecodeTest(PREFIX)
    }

    fun devDecodeTest(prefix: String) {
        //Base45 boom -> //val encodedDcc = "HC1:NCFOXN%TSMAHN-HOWO8P6GXQ-5LC+A9EH6/NAD6ZLPLX8PQV2GK/+6N+O9APPV5-FJLF6CB9YPD.+IKYJ1A4DBCT\$S*3T062OMCB/SDVT C2CVT3\$T4YC6NDOI27OTQR3JZI20262K0PI*NIXJA  CG8CRCK        *9C1QDMCIZEB5%TC1TIZI.EJJ144Z1646C46846OR67PPDFPVX1R270:6NEQ0R6AOMUF5KDCPF5RBQ746B46O1N646RM93O5RF6\$T61R63B0 %PPRAAUICO1WV5Z2L.T4RZ4E%5MK9EO9:X9UH9.V5.B9P8Q521SW5\$W5521*T56G        A1Y9%PP4T9 UP.C9\$ZE5LE8T11C9P8QJ85K7E6DE\$MMJ7EW2VSNUTS1RVEG%5TW5A 6+O67N6MCE%QD.RB\$YASUKNMU\$ZRR/KANDZ$6G:RPW29/A6IPD4OWRP23D.0J1.RP7R6BVNQBAU2XOH+AUWNRF:7P1NUXF1TE7NQY 9%1AL4GYM0EDCH0F"
        val base45 = prefix.substring(4)
        val base45Bytes = base45.toByteArray()
        val compressed = Base45.getDecoder().decode(base45Bytes)
        assert(COMPRESSED == hex(compressed))
        val cose = decompress(compressed!!)
        assert(COSE == hex(cose))
        val cbor = Message.DecodeFromBytes(cose, MessageTag.Sign1)
        assert(CBOR == hex(cbor.GetContent()))

        val cborObject: CBORObject = CBORObject.DecodeFromBytes(cbor.GetContent())

        //Tests

        val vaccinations = ArrayList<DCCVaccine>(10)
        val recoveries = ArrayList<DCCRecovery>(10)

        val tests = ArrayList<DCCTest>(10)

        val cborStartOfDcc = cborObject[-260][1]
        if (cborStartOfDcc.ContainsKey("t")) {
            val t_content = cborStartOfDcc["t"]
            for (i in t_content.values) {
                val tg = i["tg"].AsString()
                val tt = i["tt"].AsString()
                val sc = i["sc"].AsString()
                val tr = i["tr"].AsString()
                val tc = i["tc"].AsString()
                val co = i["co"].toString()
                val iss = i["is"].AsString()
                val ci = i["ci"].AsString()

                tests.add(
                    DCCTest(
                        targetedDisease = tg,
                        NAATestName = tt,
                        dateOfSampleCollection = sc,
                        testResult = tr,
                        testingCentre = tc,
                        countryOfTest = co,
                        typeOfTest = "??",
                        certificateIssuer = iss,
                        RATTestNameAndManufac = null, //TODO
                        certificateIdentifier = ci
                    )
                )
            }
        }

        if (cborStartOfDcc.ContainsKey("v")) {
            val t_content = cborStartOfDcc["v"]
            for (i in t_content.values) {
                val tg = i["tg"].AsString()
                val vp = i["vp"].AsString()
                val mp = i["mp"].AsString()
                val ma = i["ma"].AsString()
                val dn = i["dn"].AsInt32()
                val sd = i["sd"].AsInt32()
                val dt = i["dt"].AsString()
                val co = i["co"].AsString()
                val iss = i["is"].AsString()
                val ci = i["ci"].AsString()

                vaccinations.add(
                    DCCVaccine(
                        targetedDisease = tg,
                        vaccine = vp,
                        vaccineMedicalProduct = mp,
                        marketingAuthorizationHolder = ma,
                        doseNumber = dn,
                        totalSeriesOfDoses = sd,
                        dateOfVaccination = dt,
                        certificateIssuer = iss,
                        certificateIdentifier = ci,
                        countryOfVaccination = co
                    )
                )
            }
        }

        if (cborStartOfDcc.ContainsKey("r"))
        {
            val t_content = cborStartOfDcc["r"]
            for(i in t_content.values) {
            val tg = i["tg"].AsString()
            val fr = i["fr"].AsString()
            val co = i["co"].AsString()
            val iss = i["is"].AsString()
            val df = i["df"].AsString()
            val du = i["du"].AsString()
            val ci = i["ci"].toString()

            recoveries.add(DCCRecovery(
                targetedDisease = tg,
                countryOfTest = co,
                certificateIssuer = iss,
                certificateIdentifier = ci,
                dateOfFirstPositiveTest = df,
                certificateValidFrom = fr,
                certificateValidTo = du
            ))
        }
        }

        val thing = cborStartOfDcc
        val dob = thing["dob"].AsString()
        val nameRoot = thing["nam"]
        val nameObject = DCCNameObject(
            givenName = nameRoot["gn"].AsString(), familyName = nameRoot["fn"].AsString(),
            familyNameTransliterated = nameRoot["fnt"].AsString(), givenNameTransliterated = nameRoot["gnt"].AsString(), )

        val result = DCC(
            version = thing["ver"].AsString(),
            dateOfBirth = dob,
            name = nameObject,
            vaccines = vaccinations,
            tests = tests,
            recoveries = recoveries,
            from = null,
            to=null
        )

        //val cborAsString = cborObject.toString()
        //val result2 : DigitalCovidCertificate = CBORObject.DecodeObjectFromBytes(cbor.GetContent(),  DigitalCovidCertificate::class.java)
        //val result3 = DigitalCovidCertificate.decode(cbor.GetContent())

        assert(result.name.givenName == "όνομα")
    }

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

    fun hex(bytes: ByteArray) :String
    {
        val result = StringBuilder()
        for (i in bytes) {
            result.append(String.format("%02x", i))
        }
        return result.toString()
    }

}