package nl.rijksoverheid.minienw.travelvalidation.validationservice.swagger

import com.fasterxml.classmate.TypeResolver
import io.swagger.annotations.Api
import nl.rijksoverheid.minienw.travelvalidation.api.data.ValidationAccessTokenPayload
import nl.rijksoverheid.minienw.travelvalidation.api.data.ValidationAccessTokenPayloadCondition
import nl.rijksoverheid.minienw.travelvalidation.api.data.identity.IdentityResponse
import nl.rijksoverheid.minienw.travelvalidation.api.data.initialize.ValidationInitializeRequestBody
import nl.rijksoverheid.minienw.travelvalidation.api.data.initialize.ValidationInitializeResponse
import nl.rijksoverheid.minienw.travelvalidation.api.data.validate.ResultTokenPayload
import nl.rijksoverheid.minienw.travelvalidation.api.data.validate.ValidateRequestBody
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.HttpAuthenticationBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.oas.annotations.EnableOpenApi
import springfox.documentation.service.*
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import java.util.*

@Configuration
@EnableOpenApi
class SwaggerConfig {
    @Bean
    fun docket(): Docket {
        return Docket(DocumentationType.OAS_30)
        .select()
		.apis(RequestHandlerSelectors.withClassAnnotation(Api::class.java))
    	.build()
//      .pathMapping("/")
//		.directModelSubstitute(LocalDate::class.java, String::class.java)
//		.genericModelSubstitutes(ResponseEntity::class.java)
//		.alternateTypeRules(
//			newRule(
//				typeResolver.resolve(
//					DeferredResult::class.java,
//					typeResolver.resolve(ResponseEntity::class.java, WildcardType::class.java)
//				),
//				typeResolver.resolve(WildcardType::class.java)
//			)
//		)
      .useDefaultResponseMessages(false)
//		.globalResponses(HttpMethod.GET,
//			singletonList(ResponseBuilder()
//				.code("500")
//				.description("500 message")
//				.representation(MediaType.TEXT_XML)
//				.apply { r ->
//					r.model { m ->
//						m.referenceModel { ref ->
//							ref.key { k ->
//								k.qualifiedModelName { q ->
//									q.namespace("some:namespace")
//										.name("ERROR")
//								}
//							}
//						}
//					}
//				}
//				.build()))
		.securitySchemes(listOf(securitySchemeJwt())) //Uncomment this line and go boom.
//		.securityContexts(listOf(securityContext()))
//		.enableUrlTemplating(true)
//		.globalRequestParameters(
//			singletonList(
//				RequestParameterBuilder()
//					.name("someGlobalParameter")
//					.description("Description of someGlobalParameter")
//					.`in`(ParameterType.QUERY)
//					.required(true)
//					.query(Consumer { q: SimpleParameterSpecificationBuilder ->
//						q.model { m: ModelSpecificationBuilder ->
//							m.scalarModel(
//								ScalarType.STRING
//							)
//						}
//					})
//					.build()
//			)
//		)
//            .tags(Tag("Airline Stub", "Stuff..."))
            .additionalModels(
                typeResolver!!.resolve(IdentityResponse::class.java),

                typeResolver!!.resolve(ValidationAccessTokenPayload::class.java),
                typeResolver!!.resolve(ValidationAccessTokenPayloadCondition::class.java),

                typeResolver!!.resolve(ValidationInitializeRequestBody::class.java),
                typeResolver!!.resolve(ValidationInitializeResponse::class.java),

                typeResolver!!.resolve(ValidateRequestBody::class.java),
                typeResolver!!.resolve(ResultTokenPayload::class.java),
            )
            .apiInfo(apiInfo())
    }

    private fun securitySchemeJwt() : SecurityScheme {
        val argle = HttpAuthenticationBuilder()
            .name("ValidationAccessTokenJWT")
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("Value is signed JWT of ValidationAccessTokenPayload")
            .build()

        return argle
    }

    private fun apiInfo(): ApiInfo? {
        return ApiInfo(
            "Online Verification for Travellers - Validation Service",
            "Validation Service component of Online Verification for Travellers at https://github.com/minienw/onlineverification_overview.",
            "0.0.1-SNAPSHOT",
            "-",
            Contact("Steve Kellaway", "https://www.linkedin.com/in/steve-kellaway-019ba7142/", "steve.kellaway@mefitihe.com"),
            "EUPL-1.2", "https://spdx.org/licenses/EUPL-1.2.html", Collections.emptyList()
        )
    }

    @Autowired
    private var typeResolver: TypeResolver? = null
}

