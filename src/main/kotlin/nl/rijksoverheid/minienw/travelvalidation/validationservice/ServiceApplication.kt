package nl.rijksoverheid.minienw.travelvalidation.validationservice

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.ApplicationPropertiesFile
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import java.security.Security

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
fun iBusinessRulesProvider(settings: IApplicationSettings) : IBusinessRulesProvider
{
	return StringParserBusinessRulesProvider(HttpRemoteBusinessRulesSource(settings))
}

@Bean
fun stringReader(settings: IApplicationSettings) : IStringReader
{
	return HttpRemoteBusinessRulesSource(settings)
}

@Bean
fun publicKeysProvider(settings: IApplicationSettings) : IPublicKeysProvider
{
	return FileWriterPublicKeyProvider(settings, HttpRemoteBusinessRulesSource(settings))
}
