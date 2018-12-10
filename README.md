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


## Eureka Naming Server

	Keeping the list of instances of a service in a properties file would be a nightmare.
	This is why the Naming Servers exist to exposes the following services:

		- Service discovery
		- Service registration

	All the services are registered automatically and dinamically to the Naming Server.
	If one service needs to connect with another one, the service talks to the Naming Server to know the details of the service to connect with.
	That is service discovery.

	Eureka Server Project:
		netflix-eureka-naming-server
			The following is the main libreary:
				<dependency>
					<groupId>org.springframework.cloud</groupId>
					<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
				</dependency>
			The following properties are added:
				spring.application.name=netflix-eureka-naming-server
				server.port=8761
				eureka.client.register-with-eureka=false
				eureka.client.fetch-registry=false
			When the app is running we can go to the Eureka Server Web Console:
				http://localhost:8761/
	Eureka Client Project:
		currency-conversion-service
			In order to connect this app to the Eureka Naming Server.
			Add the following dependency:
				<dependency>
		            <groupId>org.springframework.cloud</groupId>
		            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		        </dependency>
		    Add the following properties:
				eureka.client.service-url.default-zone=http://localhost:8761/eureka
			In the application class spring boot component add the annotation:
				@EnableDiscoveryClient
			Start the app.
			Then in the Eureka Naming Server App the app is listed:
				http://localhost:8761/
				Application					AMIs	Availability Zones	Status
				CURRENCY-CONVERSION-SERVICE	n/a (1)	(1)					UP (1) - 192.168.0.4:currency-conversion-service:8100
			Since we are now using the Eureka Naming Server, we can now remove the hard coded list of Currency Exchange Service from the 
				properties file. The following line from the properties file should be removed:
				currency-exchange-service.ribbon.listOfServers=http://localhost:8000,http://localhost:8001
				This information is now provided by the Eureka Naming Server
				We can add new instances of Exchange Service or we can Remove instances of the Exchange Service and 
					the conversion service should continue working with the available instances.
	Eureka Client Project:
		currency-exchange-service
			In order to connect this app to the Eureka Naming Server.
			Add the following dependency:
				<dependency>
		            <groupId>org.springframework.cloud</groupId>
		            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		        </dependency>
		    Add the following properties:
				eureka.client.service-url.default-zone=http://localhost:8761/eureka
			In the application class spring boot component add the annotation:
				@EnableDiscoveryClient
			Start the app.
			Then in the Eureka Naming Server App the app is listed:
				http://localhost:8761/
				Application					AMIs	Availability Zones	Status
				CURRENCY-EXCHANGE-SERVICE	n/a (2)	(2)	UP (2) - 192.168.0.4:currency-exchange-service:8001 , 192.168.0.4:currency-exchange-service:8000

	
## API Gateway: Zuul

	Offers common functionality like:
		- Authentication, Authorization, and Security
		- Rate Limits
		- Fault Tolerance
		- Service Aggregation (A cliemt calls only 1 service instead of 20 services)

	Netflix provides an implementation called: Zuul
	Steps:
		1. Create a component for Zuul API Gateway Server
			webapp name:
				netflix-zuul-api-gateway-server
			port: 
				8765
		2. Decide what should do the API Gateway
			It will log all requests that pass through the API Gateway server.
				Class name: ZuulLoggingFilter
		3. Start the following Apps:
			- netflix-eureka-naming-server
			- currency-exchange-service
			- currency-conversion-service
			- netflix-zuul-apigateway-server
		4. Call:
			http://localhost:8000/currency-exchange/from/USD/to/COP
		5. It is needed to do the above call through the zuul apigateway server
			Use the following URL pattern:
			http://localhost:8765/{application-name}/{uri}
			where:
				{application-name} is found in the properties file of the currency-eschange app which is:
									currency-exchange-service
				{uri} is the full URI of the service which is:
									/currency-exchange/from/USD/to/COP
			Therefore:
			http://localhost:8765/{application-name}/{uri}
			It is 
			http://localhost:8765/currency-exchange-service/currency-exchange/from/USD/to/COP
		6. After calling the above URL through the Zuul API Gateway Server we got the log:
			request -> org.springframework.cloud.netflix.zuul.filters.pre.Servlet30RequestWrapper@32c1e40 request uri -> /currency-exchange-service/currency-exchange/from/USD/to/COP

			Therefore, calling the following URL:
			http://localhost:8765/currency-exchange-service/currency-exchange/from/USD/to/COP
			Redirects to the URL:
			http://localhost:8000/currency-exchange/from/USD/to/COP
		7. Setting the call from the conversion service to the Exchange Service passing through the Zuul Server.
			Currency-Conversion-Service
				In the interface:
				CurrencyExchangeServiceProxy
				We have:
				@FeignClient(name="currency-exchange-service")
				So this should be changed in order to connect with the Zull APIGateway Server
				We need the application name of the Zuul APIGateway Server which is found in the properties file:
				netflix-zuul-api-gateway-server
				The value 
				@FeignClient(name="currency-exchange-service")
				It is replaced by:
				@FeignClient(name="netflix-zuul-api-gateway-server")
				It is also required to change in the GET Mapping the URL:
				@GetMapping("/currency-exchange/from/{from}/to/{to}")
				By this, where we append the application name:
				@GetMapping("/currency-exchange-service/currency-exchange/from/{from}/to/{to}")
			Now we can call:
			http://localhost:8100/currency-converter-feing/from/USD/to/COP/quantity/3
			Which call Exchange Service through Zuul Server API Gateway
			And we get the log in the Zuul Server APIGatway:
			c.i.m.n.ZuulLoggingFilter                : request -> org.springframework.cloud.netflix.zuul.filters.pre.Servlet30RequestWrapper@42e7af59 request uri -> /currency-exchange-service/currency-exchange/from/USD/to/COP
		8. Now call the Converter Service through Zuul Server, wich call Exchange Service through Zuul Server:
			URL of Converter Service:
			http://localhost:8100/currency-converter-feing/from/USD/to/COP/quantity/3
			To call the above service through Zuul Server:
			http://localhost:8765/{app-name}{uri}
			Therefore:
			http://localhost:8765/currency-conversion-service/currency-converter-feing/from/USD/to/COP/quantity/3
			So we get 2 logs on the Zuul Server APIGateway:
			
			request -> org.springframework.cloud.netflix.zuul.filters.pre.Servlet30RequestWrapper@7f69dfc6 request uri -> /currency-conversion-service/currency-converter-feing/from/USD/to/COP/quantity/3

			request -> org.springframework.cloud.netflix.zuul.filters.pre.Servlet30RequestWrapper@3c29823b request uri -> /currency-exchange-service/currency-exchange/from/USD/to/COP




