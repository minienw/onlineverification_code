package nl.rijksoverheid.minienw.travelvalidation.validationservice

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.*
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.FileBusinessRulesConfig
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.IBusinessRulesConfig
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

@Bean
fun businessRulesConfig(settings: IApplicationSettings) : IBusinessRulesConfig
{
	return InMemoryBusinessRulesConfig(FileBusinessRulesConfig(settings))
}




