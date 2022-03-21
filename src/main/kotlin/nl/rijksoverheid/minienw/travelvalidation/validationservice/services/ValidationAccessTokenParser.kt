package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import nl.rijksoverheid.minienw.travelvalidation.validationservice.api.ValidationAccessTokenPayload
import com.google.gson.Gson
import io.jsonwebtoken.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

@Service
class ValidationAccessTokenParser(
    private val appSettings: IApplicationSettings,
    private val timeProvider: IDateTimeProvider,
    private val verificationKeyProvider: SigningKeyResolverAdapter
)
{
    fun parse(value: String): ResponseEntity<ValidationAccessTokenPayload>
    {
        val errors = ArrayList<String>()
        val stripped = if (value.startsWith("bearer", true)) value.substring(6).trim() else value

        try{
            var parsed = build().parse(stripped)
            val gson = Gson()

//            val header = gson.fromJson(gson.toJson(parsed.body), JwtHeader::class.java)
//            if (header.kid.isNullOrBlank())
//                errors.add("Kid missing.")
//
//            //TODO base64 check on kid.
//
//            if (header.alg.isNullOrBlank())
//                errors.add("Algorithm missing.")
//            else
//            {
//                if (!appSettings.acceptedValidationTokenAlgorithms.any { it.equals(header.alg, ignoreCase = true) })
//                    errors.add("Algorithm not support.")
//            }

            //TODO https://github.com/jwtk/jjwt#json-support - use jackson instead and this should pop out of the above parse.
            val result = gson.fromJson(gson.toJson(parsed.body), ValidationAccessTokenPayload::class.java)

            //TODO may be worth doing by hand instead.
            val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
            val validator: Validator = factory.validator
            val validationErrors = validator.validate(result)
            for(i in validationErrors)
                errors.add(i.toString())

            if (result.whenIssued > timeProvider.snapshot().epochSecond)
                errors.add("Issued in the future.")

            //TODO audience == validate url outside...
            //TODO subject == url subject from url... outside...

            if (!errors.isEmpty()) {
                //var sb = StringBuilder("Token is invalid:\n")
                //for(i in errors) sb.append(i)
                //TODO log sb.toString()
                return ResponseEntity(HttpStatus.UNAUTHORIZED)
            }

            return ResponseEntity(result,HttpStatus.OK)

        } catch(e: JwtException) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
    }

    private fun build() : JwtParser {
        val result = Jwts.parserBuilder()
        result.setAllowedClockSkewSeconds(120) //TODO setting?
        result.setSigningKeyResolver(verificationKeyProvider)
        //TODO .setClock() //Wrap time provider Tests?
        return result.build()
    }
}