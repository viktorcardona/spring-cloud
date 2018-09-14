# spring-cloud

This code comes from:
https://github.com/in28minutes/spring-microservices


## Applications:

	1. spring-cloud-config-server: Microservice who manages/serves the configuration properties for all microservices. The properties file resides in the git-localconfig-repo.
	2. limits-service: example microservice who connects to the spring-cloud-config-server microservice in order to fetch the configuration.
	3. currency-exchange-service: microservice who returns the multiply factor from one currency to another one.
	4. currency-conversion-service: microservice for currency converion. This calls currency-exchange-service for getting the multiply factor.

Resources:

	1. git-localconfig-repo: git repository who keep all the configuration properties files for all microservices.


--------------------------------------------------------------------------------

	CurrencyConversionService ---> CurrencyExchangeService ---> LimitsService
	                                           |                       |
	                                           |                       |
	                                           v                       v
	                                        DataBase             Configuration

--------------------------------------------------------------------------------

## Microservices Ports

|     Application       |     Port          |
| ------------- | ------------- |
| Limits Service | 8080, 8081, ... |
| Spring Cloud Config Server | 8888 |
|  |  |
| Currency Exchange Service | 8000, 8001, 8002, ..  |
| Currency Conversion Service | 8100, 8101, 8102, ... |
| Netflix Eureka Naming Server | 8761 |
| Netflix Zuul API Gateway Server | 8765 |
| Zipkin Distributed Tracing Server | 9411 |


## URLs

|     Application       |     URL          |
| ------------- | ------------- |
| Limits Service | http://localhost:8080/limits POST -> http://localhost:8080/actuator/refresh|
|Spring Cloud Config Server| http://localhost:8888/limits-service/default http://localhost:8888/limits-service/dev |
|  Currency Converter Service - Direct Call| http://localhost:8100/currency-converter/from/USD/to/INR/quantity/10|
|  Currency Converter Service - Feign| http://localhost:8100/currency-converter-feign/from/EUR/to/INR/quantity/10000|
| Currency Exchange Service | http://localhost:8000/currency-exchange/from/EUR/to/INR http://localhost:8001/currency-exchange/from/USD/to/INR|
| Eureka | http://localhost:8761/|
| Zuul - Currency Exchange & Exchange Services | http://localhost:8765/currency-exchange-service/currency-exchange/from/EUR/to/INR http://localhost:8765/currency-conversion-service/currency-converter-feign/from/USD/to/INR/quantity/10|
| Zipkin | http://localhost:9411/zipkin/ |
| Spring Cloud Bus Refresh | http://localhost:8080/bus/refresh |




## Open Feing: This is a Rest Client Library

	Feign makes writing java http clients easier
	https://cloud.spring.io/spring-cloud-openfeign/
	https://github.com/OpenFeign/feign

	Example found in the project: currency-conversion-service who calls the service http://localhost:8000/currency-exchange/from/USD/to/COP

	1. We have to add the dependency:
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-openfeign</artifactId>
	</dependency>
	
	2. Then, enable Feing in the SpringBoot Java Class:
	@EnableFeignClients("com.in28minutes.microservices.currencyconversionservice")

	3. This is client using RestTemplate:

			Map<String, String> uriVariables = new HashMap<>();
        	uriVariables.put("from", from);
        	uriVariables.put("to", to);

        	ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().
                		getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                        CurrencyConversionBean.class,
                        uriVariables);

        	CurrencyConversionBean response = responseEntity.getBody();

	4. Build now a Feing Client.
		Add a proxy, A Java interface: CurrencyExchangeServiceProxy

		import org.springframework.cloud.openfeign.FeignClient;
		import org.springframework.web.bind.annotation.GetMapping;
		import org.springframework.web.bind.annotation.PathVariable;

		@FeignClient(name="currency-exchange-service", url="localhost:8000")
		public interface CurrencyExchangeServiceProxy {

		    @GetMapping("/currency-exchange/from/{from}/to/{to}")
		    CurrencyConversionBean retrieveExchangeValue(@PathVariable("from") String from, @PathVariable("to") String to);

		}

	5. Use the proxy from controller to call in a simpler way a REST Service:

		private CurrencyExchangeServiceProxy proxy;

		CurrencyConversionBean response = proxy.retrieveExchangeValue(from, to);



## Ribbons: It is a Local Load Balancer. 
	Local = The microservice client do load balance

	The Currency Converter Service calls the Currency Exchange Service
	The Currency Converter Service is one instance.
	The Currency Exchange Service  is two instances.
	The Currency Converter Service uses Ribbon for Load Balancing the calls to the Currency Exchange Service.
	Used library for doing Ribbong Load Balancing:
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
                </dependency>
    The list of instances of the Currency Exchange Service are defined in the properties file:
    	currency-exchange-service.ribbon.listOfServers=http://localhost:8000,http://localhost:8001
    The following Interface is the Proxy for doing calls to the Exchange Service from the Converter Service:
    	package com.in28minutes.microservices.currencyconversionservice;

		import org.springframework.cloud.netflix.ribbon.RibbonClient;
		import org.springframework.cloud.openfeign.FeignClient;
		import org.springframework.web.bind.annotation.GetMapping;
		import org.springframework.web.bind.annotation.PathVariable;

		@FeignClient(name="currency-exchange-service")
		@RibbonClient(name="currency-exchange-service")
		public interface CurrencyExchangeServiceProxy {

		    @GetMapping("/currency-exchange/from/{from}/to/{to}")
		    CurrencyConversionBean retrieveExchangeValue(@PathVariable("from") String from, @PathVariable("to") String to);

		}

		The proxy uses also Feign for doing the magic to do the call with the Java Interface to the Exchange Service.
		
