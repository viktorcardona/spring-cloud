# spring-cloud

This code comes from:
https://github.com/in28minutes/spring-microservices


Applications:

	1. spring-cloud-config-server: Microservice who manages/serves the configuration properties for all microservices. The properties file resides in the git-localconfig-repo.
	2. limits-service: example microservice who connects to the spring-cloud-config-server microservice in order to fetch the configuration.
	3. currency-exchange-service: microservice who returns the multiply factor from one currency to another one.

Resources:

	1. git-localconfig-repo: git repository who keep all the configuration properties files for all microservices.


--------------------------------------------------------------------------------

	CurrencyCalculationService ---> CurrencyExchangeService ---> LimitsService
	                                           |                       |
	                                           |                       |
	                                           v                       v
	                                        DataBase             Configuration

--------------------------------------------------------------------------------

	Application Ports

	
	Application 						Port

	Limits Service 						8080, 8081, ...
	Spring Cloud Config Server 			8888
	Currency Exchange Service 			8000, 8001, 8002, ..
	Currency Conversion Service 		8100, 8101, 8102, ...
	Netflix Eureka Naming Server 		8761
	Netflix Zuul API Gateway Server 	8765
	Zipkin Distributed Tracing Server 	9411


--------------------------------------------------------------------------------
