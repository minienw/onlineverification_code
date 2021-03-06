package nl.rijksoverheid.minienw.travelvalidation.validationservice

//import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.DccVerificationHttpProxy
import org.junit.jupiter.api.Test

class VerifyDccTests
{
    //Requires nl-covid19-coronacheck-restapi to be up and running on port 8088
    @Test
    fun sanityCheck() {

        //TODO config file of validationservice does not match this test data
        //Do(2, "")
        //Do(2, "safdsadfsdfsf")

        //TODO provenence?
        //Do(3, "HC1:NCFD/KU9OOJ2.N2YAENAL%SRKIB3R2F*SP.CD4EV75NY5FQ4ZMMTLN-53PE4Y3EPMI:NI%PC/$5IT0N\$DXUDUUNNJAY*GN.M7VG\$D1KX34+HP8C3ZCQDWEN7S/O7AU.5SRFV1%8F*B*SVC\$VIVB90F37FFKUKMN0QK%UILJ960OGD7E6B+0GCUE07RJAO6X5I 4EHGZDL388INT7P978P:3NIGQMY9LLDQCPGURH1W\$G2KZBKSQ73EQKQNOL$ KUSTO+D$5U.82K2H6N0:\$R .QN%V4 7QJ48AA8IN%H8P%R4JPCL5NVP-GMYTH/D82R2IBH:HI*/3/MBIKN9J6P4PYZ8M+52U3L PPSK92347LA/IBCRZJ97PQ20ARM4N2NIJ2/-EXBH%VCOAEH5QF0U6OA0$145G8AM-NQ6P1JXGOYI*LK:IQ3TB-F0QN0+50SP4WCJ-709%7\$V8MOAKV2RL8-OFXGKRK3X/L8OH+.N**4116:.U6CG512OS0HU9PP1EA8R49Z*S*+T30AB/AP:ANS9F02$:S6+TS P9U1C:1RT41.P592OINBJ6OFS6SD 7N-/F*+7FBW22WS%6L/3KC83U6H3WD3WF7K0NNC+L2IVT:A7L3XBW-FF%EV$*T7.RJE6T6WY3MT6FDAQ2\$F%-4*10\$CQI0")

        //https://github.com/ehn-dcc-development/dcc-testdata/blob/main/NL/2DCode/raw/028-NL-test.json
        //Get Status 3...
        //Do(1, "HC1:NCFOXN%TSMAHN-H+XSN-7D\$AUJKAIU099C\$E 43G-VC9B5G2Q7HI9C.H94.OQWOQHIZC4TPIFRMLNKNM8JI0EUG*%NH\$RSC92GF8DTE1KO87J4F8FU:4RETF10UB/9BL5C2F\$KU8VHO0VQK96VP8BS41E3NNM78R-H9NTBY4Z7W99WD2IZD5CC9G%89-8CNNF54DABYYMAHLW 70SO:GOLIROGO3T5ZXK W5WGKZ/MPS4V77GQ0 EQPCR8U6XHQNSPO+Q*ERVH5*XKMB43Q4IOR3/RN95*28-W7AC5ADN.-O8L6IWMW KAHA508XIORW6%5L+L8NR7*.4\$HMDYA 96CX8LCNEAOWP4BD7WJ8+YMHBQ:YM/D7JSPZHQ1CQEPPXKRM.QDNQL*OXQ6CJP.NR:SQKSON64-ZOHZ5-HQM47TKRTEHGAREG3RA3/43KD3O13P-JQ20BNCPX81R69O6IR9/NN\$MPV*OH*OTHQGSOBIR*ER8JP.96SC5 76FR7 POOOGJ O--3.UBB9S31GMDPDSP+7G%/2RL3DE4:\$JE JXVU%JBJMC:/00CL82D.LE:3CQ7U7R5GH2GGP605-VOH7T:TGK.V%2LI0RIFJ6XJNJN")

        //https://github.com/ehn-dcc-development/dcc-testdata/blob/main/NL/2DCode/raw/056-NL-vaccination.json
        //Do(1, "HC1:NCF7X3W08$\$Q030:+HGHBFO026M6M4NHSU5S8CUHC9:R7YL8YW7.6JUVM0\$U7UQ%-B/$5X$59498\$BLQH*CT4TJ603TRTIHKK5H6%EJTEPTSIWEJFAYMPRMQ%BOPUGIL3P2CHZMZ35D:2G7JU5E0B180OZ0W7:O/C5TAVVO5X\$D+BTE\$C:EWP 1Z$29%IDM5-6Q+F56U0ZBI0*8UEADP2LBLM9QH7HWPIYMGCXA5773R7 HFOV0-VG::AP9POXD6-J/WM4*R*O7HZSJG3NYTLQFT5D51V 4C5D8M-JOYLNTC*%MG5LWI1 H7%DNGUQ%3RQ\$H4DQULL905%*1ZV6S9GIUHL103BHVUVZU9 0I%:DVF1H21LCCLCB\$W4HVN%2BN7CLK07BG/PS:W3\$M91WI5N02 QASQNY81+6BJ9OFERUK-AHWU5I7L-%2ZY9J/G:-A/UFGIIETL6 G41W00JIRP-NMJLP.W7 7BHJ0J V%%HR/HMWQG+CVX9NQABH8129 G6KD38L4I6G$6WT.73ZR80WH+582DUZ1O3G.9NGWNTZJ*.6NCVLUR0IH2SLH.SMPU 0PL:MDAOENSYUIJ9NH9OK8BZ6S:38IME6OE*QVUSR1FW+:QU0")

        //Generated inside original go project tests but fails with 4
        //Do(1, "NL2:3QYLJN7UR5G2CL%+8% UO8HSB1+3HHLS74 FIJ*U UBX\$W4-VOYADSWPXC%6+JH1B-YCF*\$ZSXEDK30GCC6M%%%N76/JRJH%/ A4YPOTRR*ROX-EP*NFI/-XM6E0NZ/6SC8CBM\$I40:U/%U0-CFT+E73N0IE7+C+JB15YQ7CC8P3Q+3W7 BDS-T-O ZUH5YH/953KES4/+3I09A9YH+MH:Q8T87E9L.2CKGZGNAIDV3AEJ7TH05.VENH 88WD19F%XJ6*C-W-1TC:T+*XQDQ+3+*%01VQ3JM.L84416I34NH\$J.CBCVWX1\$PMI9E.CZ3QXPA52X2L0EU\$M-VI*UNF7KAA5Q6VVLL+ZHW7DU:KS21. : D4+BW/PXAVJDHC:XSFXVZK/AOH2DR7R:J%E4ZX+E36WJRVABO3PX8VM4QSAWX1X24K6B*T9J..9Z+8PK\$DQ4+Z3*6C1D:L$/0DDQ0PO+6LEJDZ00F%KD756H5THVF6NHXCY.VPRGNWFXACAWXA:QY8A3PGOL\$QH1HENCNOIAJHVBSJ*\$BZ703D7X0%WI9.VUQ 9BP*39-ADB5IM SX QSGIG2Q761E/HUI.\$OE1.R//-W8V8J7.GPICQYUGS96/5P7K9SOMTUN4T1:*MKW/L2CME.-VNJ8J% P5/H-ROOMW -1QUEI NW: *IB0DJSN1-1QR4:1UEBNL*.5 Q+5X11-W4-8ZKG:T978TG*K3TW06CM+Y:YG+*LCMNZW /5O1E:U:X7JN31C:09A4$0PRWW30* /R-V81Y10B%QFZAIVS%F.8D*QWD*9+W5+PF* Q QHN\$R5V.6UJ*4X-TU050ISEAOKSV338I0I25/C\$TI5M9Y:EV/E-TC0N4AYDZTE-9/ZQCIE/K.P5\$FB4-2ECR4:Z47R36RTK0SO*/3DPLS:6: HFASQZHPBHB.ODC/8CZX+--VGZ7:QU2OOM*KT5OQ8Z\$F7G*9W/K30+ZL8K6 PS-H/00I7EMZK2GZB:-S1+IGCATNE2YHL2QPARW8\$JK5+GVS0.B0$%FLL--XVSP5EJ5KHZZQCV2R-SLD EHB5PKG4::PDYUMS%+2K02RKYA9G7Q.8MM 8V3XJ.QQNQ/EJPC7*4.8SX.X-E2HUGOV-FK.%-F-RQGB6R6Z22E82F DFS3$:/%%VAF/ND-IZEV*YS-/ZNLWGTFDIPYR2 ZHO0\$WOTEK/I%RX-JS5OHB5ECTG7E$*NYP5:*6.G4XOD 30CR5L0V7PXU1AXLWINJR9DB/T4V7JRVYH\$G6MWW:M*6/T6-R5KO9NSJHWYQH0X4H+/EMJEYT/O*BXO")
        //Do(1, "NL2:SJQZYV54X%DN:ZWF:7NLCJQ.%3\$VAG\$E-ZE 0YU5V4ECN4BZ8+CU.TZ3E-RP+W8.0KHEJK1CQD I\$L/QW11I6W6NW-N5SW*O8L296KBE/P5F82AFSCXWQ+R BBDA%XUN B\$S\$.C 9 83HC05BFGNPJFOX\$I L3.3-TT5E2HF4ENKME6F.3++06%:UOQ%H9-:/5IC1\$LRG--7LY6BD6TON+-1251V9\$VWLAE\$GE09I%LG1X-BU6YXSUOVTUJLWDI.EQJY2IIOOK/UV0LV2\$Z.77I.3X*QB:$4/LAKU+SH7S5Z6XI5:%O8HZT/FD*B6TLYG7-2Q75MWWRACX53KZG\$R EBT O$1H* NIKQ.S2./P+WJTYB5CQIQ%V%YEGMZ GF.%D:$\$QIZIA-:0OAEO-WKDG6$%Z:R*EBU/4LP1Z-H5$4T IJ4U\$E%3G%CNK+BFFAOH:YHSX/F+TRRUOTU0\$EH:42B$/U/2%WI3NA-UT6BB2RU5IT%Y4 DZ%5:4\$XUJLN6224B+XP\$C%\$MTB55%.QDZVZVRWTRD5PXUCC5.QJN2I+0ES8+382N*AZWC7+Z5/I\$RS8A98.A 9QXW+:ZDZ.SYE2KS1Z9TNAAV5+6J7C KS7\$MHYQ2E99MQWQ9CTG3-UJOU0YQ9W/F4T8T--NP\$PL9BPN5YU1/BVVLQ+B4O5/5VK6:\$G7.SYWY%IDQ7X0N5YR++HZ*O-.S08F-AP$.NHX77GBVLZ/BXK XFHNFNTL\$C.S/5MT7OFL1%Q $/9TZDK:D QW6$758HALE6IU%L+25GFK.I8XD8QL$6NCADZ 07HU.J:EX2GP:96FASJH9UWB\$ZC 5.AW%$4\$A6GYQU.KIAC/IEE2P*T/0 W8+6J1M9I2WK:DNSX2*9$-7947XDL/21A.SL0PFQT54KOHP5:0.5/3ZIZXH/Q%V62LWA%5OJI%6O26Z+K22 O\$OIS%1-2:PT%D*7%CVT0UQZBALIPEO5KLC5T0I53E/%WK*2OGS-40/LHW20RP6PQ8XOFIUTWSMKO77E/A*NJYTY23H0EX8W\$DOYI/ O0P5/C10FA509XFMEZ9/V:QR83+04O\$O0Q\$MJGSO07E0R6N8MD1ES$8J %D08BNH5PT.XU34YU+-\$R6QK KS$9* %WBO.DS.3BZ96I88IDHK%\$V$*QNYTZGT$-0U59Q/98OQMIST T3IL9TP*-D\$Q/EPITWE$4JUL59JTDE:MCD0:2798 E6T8W*E474-1TZK*ODB1G.N/6P6X*IMMO%219K-5IM.VS*P4/DV/ISJ6*U54.2VGRBSOTS7QDJ-JD92%M4 PJG")
    }

//    fun Do(expectedResult:Int, hcertDcc:String)
//    {
////        val result = DccVerificationHttpProxy().verify(hcertDcc)
////        assert(result.Status == expectedResult)
//    }
}