#
# Currency Exchange Service
#

The server port of the app can be set in the properties file as:
server.port=8000

h2 console DB:

    http://localhost:8000/h2-console
    JDBC URL: jdbc:h2:mem:testdb


Sample Request:

    http://localhost:8000/currency-exchange/from/USD/to/COP


Sample Response:

    {
        id: 1000,
        from: "USD",
        to: "COP",
        conversionMultiple: 65,
        port: 8000
    }

