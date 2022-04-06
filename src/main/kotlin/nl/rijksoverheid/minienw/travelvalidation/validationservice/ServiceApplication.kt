package nl.rijksoverheid.minienw.travelvalidation.validationservice

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.ApplicationPropertiesFile
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.HttpRemoteAirlineSigningKeyProvider
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IAirlineSigningKeyProvider
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.FileWriterPublicKeyProvider
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.HttpRemoteBusinessRulesSource
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.IBusinessRulesProvider
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.IPublicKeysProvider
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.StringParserBusinessRulesProvider
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import java.security.Security

//import springfox.documentation.oas.annotations.EnableOpenApi
//import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableWebMvc //For swagger
@EnableConfigurationProperties(ApplicationPropertiesFile::class)
class ServiceApplication

fun main(args: Array<String>) {
	Security.addProvider(BouncyCastleProvider())
	runApplication<ServiceApplication>(*args)
}

//@Profile("")
@Bean
fun businessRulesProvider(settings: IApplicationSettings) : IBusinessRulesProvider
{
	return StringParserBusinessRulesProvider(HttpRemoteBusinessRulesSource(settings))
}

//@Profile
@Bean
fun publicKeysProvider(settings: IApplicationSettings) : IPublicKeysProvider
{
	return FileWriterPublicKeyProvider(settings, HttpRemoteBusinessRulesSource(settings))
}
