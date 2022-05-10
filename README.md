#Intro
This service is Springboot REST API application in Kotlin using IntelliJ-IDEA IDE and Gradle.
#Build and deploy
##Pre-requisites
* Git
* Java 17
* Gradle
* Go
* GCC
* Docker
* Docker Compose
* Access to the HCERT Verifier - https://github.com/minvws/nl-covid19-travelscan-mobile-core-private
* Overall instructions on https://github.com/minienw/onlineverification_code/blob/main/Configuration%20for%20Travel%20Validation%20Services.docx
## Build Validation Service
Requires:
* This repo
* Java 17
* Gradle
#Development
The required Redis server can be setup and run using the script docker-redis-dev.cmd (Windows).
Development does not require the HCERT Verifier. This is achieved by running/debugging the application using the 'demo' profile which returns a positive result for any attempt to verify a HCERT.
## Build HCERT Verifier
Requires:
* Git
* Go
* GCC
1. Clone the repo https://github.com/minvws/nl-covid19-coronacheck-hcert to an adjacent folder.
2. Build using the command line.
## Airline-Stub and Demo Wallet
This repo is one of 3 co-operating components. For development or running a demo, see the implementations at:
* Wallet Demo - https://github.com/minienw/HTMLwallet2.git
* Airline Stub - https://github.com/minienw/airlinestub


