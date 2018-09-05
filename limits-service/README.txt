#
# Limits Service
#

In the property file bootstrap.properties configure:

- spring.application.name: The name of the application
- spring.cloud.config.uri: The Spring Cloud Config Server. This is the Microservice who manage all the configuration per environment for this application.
- spring.profiles.active: The active profile. This values defines the profile and then the property file to read from the Spring Cloud Server Config application.

