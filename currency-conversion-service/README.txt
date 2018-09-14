## Currency Converter

Sample Request:

Call with Rest Template:

    http://localhost:8100/currency-converter/from/USD/to/COP/quantity/1200

Call with Feing client and Ribbon load balancer:

    http://localhost:8100/currency-converter-feing/from/USD/to/COP/quantity/1200

    This service calls the Currency Exchange Service.
    It is done using:
        - RestTemplate
        - Feing Cloud Client: allows call the remote service using a Java Interface
            Library:
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-openfeign</artifactId>
                </dependency>
            Class:
                org.springframework.cloud.openfeign.FeignClient

    It uses Ribbon Load Balancing
        - Allows the service to call multiple instances of the Currency Exchange Service doing balancing
            Library:
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
                </dependency>
            Class:
                org.springframework.cloud.netflix.ribbon.RibbonClient