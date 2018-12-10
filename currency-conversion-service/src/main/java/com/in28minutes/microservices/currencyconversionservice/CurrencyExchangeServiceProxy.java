package com.in28minutes.microservices.currencyconversionservice;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//Commented the url attribute due to the use of Ribbon which talks with multiple instances
// the URLs of the service to call are configured in the properties file:
// currency-exchange-service.ribbon.listOfServers=http://localhost:8000,http://localhost:8001
//@FeignClient(name="currency-exchange-service", url="localhost:8000")
//The following line is change to @FeignClient(name="netflix-zuul-api-gateway-server") in order to make the call to exchange service pass through the Zuul APIGateway Server
//@FeignClient(name="currency-exchange-service")
@FeignClient(name="netflix-zuul-api-gateway-server")
@RibbonClient(name="currency-exchange-service")
public interface CurrencyExchangeServiceProxy {


    //The following line was commented and replace by @GetMapping("/currency-exchange-service/currency-exchange/from/{from}/to/{to}") in order to make the call to exchange service pass through the Zuul APIGateway Server
    //@GetMapping("/currency-exchange/from/{from}/to/{to}")
    @GetMapping("/currency-exchange-service/currency-exchange/from/{from}/to/{to}")
    CurrencyConversionBean retrieveExchangeValue(@PathVariable("from") String from, @PathVariable("to") String to);

}
