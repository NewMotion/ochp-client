# OCHP client [![Build Status](https://secure.travis-ci.org/thenewmotion/ochp-client.png)](http://travis-ci.org/thenewmotion/ochp-client)

Client for [OCHP](http://ochp.eu) written in Scala, for Scala 2.11+

## Includes

* Open Clearing House Protocol v1.3 generated client and bean classes with help of [cxf](http://cxf.apache.org)

* API trait to communicate with the clearing house:
    ```scala
    trait OchpApi {
      def recvAllTokens(): List[ChargeToken]
      def sendAllTokens(tokens: List[ChargeToken]): Result[ChargeToken]
      def recvNewTokens(lastUpdate: DateTime): List[ChargeToken]
      def sendNewTokens(tokens: List[ChargeToken]): Result[ChargeToken]

      def sendAllChargePoints(chargePoints: List[ChargePoint]): Result[ChargePoint]
      def recvAllChargePoints():List[ChargePoint]
      def sendNewChargePoints(chargePoints: List[ChargePoint]): Result[ChargePoint]
      def recvNewChargePoints(lastUpdate: DateTime):List[ChargePoint]

      def sendCdrs(cdrs: List[CDR]): Result[CDR]
      def recvCdrs(): List[CDR]
      def confCdrs(approvedCdrs: List[CDR], declinedCdrs: List[CDR])
    }

    ```

* Service trait that can be instantiated like here:
    ```scala
    val service = new OchpService {
      val conf = OchpConfig(
        wsUri = "http://localhost:8088/mockeCHS-OCHP_1.3",
        liveWsUri = "http://localhost:8088/mockeCHS-OCHP_1.3-live",
        user = "me",
        password = "mypass"
      )
      val client = OchpClient.createCxfClient(conf)
    }
    ```

## Integration tests

Integration tests can be run as follows:

```sbt it:test```

In order for these tests to work, valid credentials need to be provided (see `src/it/resources/reference.conf` for reference).

## Setup

### Maven

1. Add this repository to your pom.xml:
    ```xml
    <repository>
        <id>thenewmotion</id>
        <name>The New Motion Repository</name>
        <url>http://nexus.thenewmotion.com/content/groups/public"</url>
    </repository>
    ```

2. Add dependency to your pom.xml:
    ```xml
    <dependency>
        <groupId>com.thenewmotion</groupId>
        <artifactId>ochp-client_2.11</artifactId>
        <version>1.3.7</version>
    </dependency>
    ```

### SBT

1. Add the following resolver:
    ```scala
    resolvers += "TNM" at "http://nexus.thenewmotion.com/content/groups/public"
    ```

2. Add the following dependency:
    ```scala
    "com.thenewmotion" %% "ochp-client" % "1.3.7"
    ```
