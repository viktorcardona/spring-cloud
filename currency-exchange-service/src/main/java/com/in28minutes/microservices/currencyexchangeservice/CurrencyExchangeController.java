package com.in28minutes.microservices.currencyexchangeservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyExchangeController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Environment environment;

    private ExchangeValueRepository repository;

    public CurrencyExchangeController(Environment environment, ExchangeValueRepository repository) {
        this.environment = environment;
        this.repository = repository;
    }

    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public ExchangeValue retrieveExchangeValue(@PathVariable String from, @PathVariable String to) {
        ExchangeValue exchangeValue = this.repository.findByFromAndTo(from, to);
        exchangeValue.setPort(environment.getProperty("local.server.port", Integer.class));
        logger.info("Target Log ::::  {}", exchangeValue);
        return exchangeValue;
    }

}
