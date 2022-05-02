# Overview
Deploy and configure the services in the following order:
	1. Wallet
	2. Validation Service
	3. Airline service

The 2 services are Spring Boot applications and use the standard configuration file mechanism (application.properties) and profiles for configuring different environments.

Configuration for the services is present in both the configuration files and their identity documents which themselves are JSON files.
The two services require at least one suitable key pair (RSA4096 or ECDSA) each for signing and verifying tokens. The public verification keys must be added to the service’s identity document. 

The intended endpoints of each service must also be added to the corresponding entry in the service's identity document or configuration file. Care should be taken when configuring the endpoint values – the URL must be reachable by the ultimate user of the value (which is often NOT the location of the service which has the value in its configuration). Point of View (POV) is given for each configured URL.

Logging is log4j.

In general, start with the basic profile or nearest file to your profile/environment and change the values as per the following sections.
Note the encoding (the one that starts with ASN1…) specified for public and private keys are the most commonly used formats and are usually obtained simply by using the default encoding of the key from Bouncy Castle, .NET or Java.
Several working examples of configuration files and identity files are present in the repositories.

## Configuration Items

### Authorised list of service providers
Config file entry: validation-service.airlineIdentityUris

Comma separated list of service provider identity URIs. If the list is empty or at least one public key is not found, the service will not start. (TODO recent change request – service will start if at least one url is in the accept list, no attempt to obtain a public key.) All URIs are from the POV of the Validation Service

### Key Pair for Result Token Signature
Generate a RSA4096 key pair.

For the private key, get the base 64 string of the byte array of the key in Asn1/Der/Pkcs8 format.

For the public key, which is configured in JWK format as:

x5c: base 64 string of byte array of the private key in Asn1/Der/Pkcs1/X509 format

kid: base64 string of first 8 bytes of the SHA256 of private key in Asn1/Der/Pkcs1/X509

Configuration file entry:
validation-service.validationResultJwsSigningKey: private key

Identity file elements:
verificationMethod with id ending ‘#ValidationServiceSignKey-1’ or other digit:  public key

### Key Pair for DCC Encryption
Generate a RSA4096 Key Pair.

For the private key, get the base 64 string of the byte array of the key in Asn1/Der/Pkcs8 format.

For the public key, which is configured in JWK format as:

x5c: base 64 string of byte array of the private key in Asn1/Der/Pkcs1/X509 format

kid: base64 string of first 8 bytes of the SHA256 of private key in Asn1/Der/Pkcs1/X509

Configuration file entry:
validation-service.dccEncryptionRsaPrivateKey: private key

Identity file elements:
verificationMethod with id ending ‘#ValidationServiceEncKey-1’ or other digit:  public key

### Session Lifetime
The maximum time, in seconds, a session should last, where a session is the time between the /initialize and /validate call. A reasonable value e.g. 15mins= 900, should take into account the length of time an unprepared user may take to obtain the DCC for upload (e.g. not necessarily the lifetime of the validation access token set by the airline, although it would be preferable for all parties to use the same lifetime value)

validation-service.sessionMaxDurationSecondsString = 900

### Result Token Expiry
The maximum time, in seconds, between issuing a result token when it expires. TODO does this just have to last until after the confirmation token is posted to the airline? Or would they be re-evaluated at a later date? Are there not other mechanisms for this e.g. valid at? Currently set to an hour = 3600.
validation-service.resultTokenLifetimeSeconds = 3600

### Folder and name of the the identity.json file
Configuration file entry:
validation-service.configFileFolderPath = build\\resources\\main\\dev

Do not change this entry:
validation-service.publicKeysFileName = public_keys.json

### Host name of the redis service
Note. If hosted in docker, this will be the name of the redis service in the docker compose yml file.

Configuration file entry:
validation-service.redisHost = localhost

### URI of the DCC verification service
The URI must be in the format http://HOSTNAME:PORT/verify_signature where HOSTNAME and PORT are the configurable values.
Note. If hosted in docker, the hostname will be the name of the verification service in the compose file.
validation-service.dccVerificationServiceUri = http://verifier:4003/verify_signature
Business Rules and DCC Verification Keys
The following URI must be present and correspond to the OTAP/DTAP set required:

In order:

* Configuration for color codes

* NL custom business_rules

* EU distributed business_rules

* Values Sets referred to in the business rules

* Trustlist (public keys) issuers

Configuration file entries (ACC shown):

* validation-service.configUri = https://verifier-api.acc.coronacheck.nl/v4/dcbs/config

* validation-service.customBusinessRulesUri = https://verifier-api.acc.coronacheck.nl/v4/dcbs/custom_business_rules

* validation-service.businessRulesUri = https://verifier-api.acc.coronacheck.nl/v4/dcbs/business_rules

* validation-service.valueSetsUri = https://verifier-api.acc.coronacheck.nl/v4/dcbs/value_sets

* validation-service.publicKeysUri = https://verifier-api.acc.coronacheck.nl/v4/dcbs/public_keys

# Configuration for other services
Make a note of the identity URL for the Airline service (POV – from Wallet) which will be used when generating Initiating QR Code Tokens.
