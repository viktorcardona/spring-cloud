# Configuration Properties of All Microservices in Just One Place

Configure the property:
spring.cloud.config.server.git.uri

Which points to a local git repository.

For each Microservice create the properties files that contains the Microservice's configuration per environment.
For instance, the configuration properties files for the Microservice limits-service are:
    limits-service.properties
    limits-service-dev.properties
    limits-service-qa.properties
The property file load depends on the Microservice's profile.


#
#   Start the App.
#

Go to:

http://localhost:8888/limits-service/default
http://localhost:8888/limits-service/dev
http://localhost:8888/limits-service/qa