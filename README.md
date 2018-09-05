# spring-cloud

This code comes from:
https://github.com/in28minutes/spring-microservices


Applications:

	spring-cloud-config-server: Microservice who manages/serves the configuration properties for all microservices. The properties file resides in the git-localconfig-repo.
	limits-service: example microservice who connects to the spring-cloud-config-server microservice in order to fetch the configuration.

Resources:

	git-localconfig-repo: git repository who keep all the configuration properties files for all microservices.


--------------------------------------------------------------------------------

	Ports
	Application							Port
	Limits Service						8080, 8081, ...
	Spring Cloud Config Server			8888
	Currency Exchange Service			8000, 8001, 8002, ..
	Currency Conversion Service			8100, 8101, 8102, ...
	Netflix Eureka Naming Server		8761
	Netflix Zuul API Gateway Server		8765
	Zipkin Distributed Tracing Server	9411


--------------------------------------------------------------------------------
