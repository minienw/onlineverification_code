package nl.rijksoverheid.minienw.travelvalidation.validationservice

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InjectionPoint
import org.springframework.context.annotation.*

@Configuration
class LoggingConfiguration {

    @Bean
    @Scope("prototype")
    fun logger(injectionPoint: InjectionPoint): Logger
        = LoggerFactory.getLogger(injectionPoint.methodParameter?.containingClass?: injectionPoint.field?.declaringClass)
}