package com.thenewmotion.chargenetwork.eclearing.client

import com.thenewmotion.chargenetwork.eclearing.api.{EmtId, DateTimeNoMillis, TokenSubType, Card}
import com.thenewmotion.chargenetwork.eclearing.{EclearingConfig}
import com.typesafe.config._
import org.specs2.mutable.SpecificationWithJUnit


/**
 * EclearingClient integration tests.
 * need a soapUI mock service running on 8088.
 * The respective soapui project can be found in
 * test/resources/soapui/E-Clearing-soapui-project.xml (SoauUI v5.0.0) *
 *
 * The mock service will be starting automatically during the pre-integration-test phase
 * so this test can run during integration-test phase
 *
 *
 * @author Christoph Zwirello
 */
class EclearingClientSpecIT extends SpecificationWithJUnit with CardTestScope with CpTestScope
with CdrTestScope{
  args(sequential = true)


  "EclearingClient" should {


    " receive cdrs" >> {
      val cdrs = client.getCdrs()
      cdrs(0).cdrId === "123456someId123456"
      success
    }

    " add CDRs" >> {
      val result = client.addCdrs(Seq(cdr1))
      result.resultCode === "ok"
    }

    " confirm CDRs" >> {
      val result = client.confirmCdrs(Seq(cdr1), Seq(cdr2))
      result.resultCode === "ok"
    }


    " receive roamingAuthorisationList" >> {
      val authList = client.roamingAuthorisationList()
      val cards = authList
      cards.length === 7
      cards(0).contractId === "YYABCC00000003"
    }

    " send roamingAuthorisationList" >> {
      val cards = List(card1)
      val rais = cards
      val result = client.setRoamingAuthorisationList(rais)
      result.resultCode === "ok"
    }

    " return an error for rejected roamingAuthorisationList" >> {
      val cards = List(card2)
      val result = client.setRoamingAuthorisationList(cards)
      result.resultCode === "nok"
    }

    " receive roamingAuthorisationListUpdate" >> {
      val authList = client.roamingAuthorisationListUpdate()
      val cards = authList
      cards.length === 1
      cards(0).contractId === "YYABCC00000001"
    }

    " send roamingAuthorisationListUpdate" >> {
      val cards = List(card2)
      val result = client.setRoamingAuthorisationListUpdate(cards)
      result.resultCode === "ok"
    }


    " receive chargepointList" >> {
      val cps = client.chargePointList()
      cps(0).evseId === chargePoint1.evseId
      success
    }

    " set charge point list" >> {
      val result = client.setChargePointList(Seq(chargePoint1))
      result.resultCode === "ok"
      success
    }

  }
}

trait CardTestScope {

  val conf: Config = ConfigFactory.load()

  val client = EclearingClient(
    new EclearingConfig(
      conf.getString("e-clearing.service-uri"),
      conf.getString("e-clearing.user"),
      conf.getString("e-clearing.password"))
  )

  val faultyClient = EclearingClient(
    new EclearingConfig(
      "http://localhost:8",
      conf.getString("e-clearing.user"),
      conf.getString("e-clearing.password"))
  )

  val card1 = Card(
    contractId = "YYABCC00000003",
    emtId=EmtId(
      tokenSubType = Some(TokenSubType.withName("mifareCls")),
      tokenId = "96B0149B4EA098BE769EFDE5BD6A7403F3A25BA0"),
    printedNumber = Some("YYABCC00000003J"),
    expiryDate = DateTimeNoMillis("2014-07-14T00:00:00Z")
  )

  val card2 = Card(
    contractId = "YYABCC00000003",
    emtId=EmtId(
      tokenSubType = Some(TokenSubType.withName("mifareCls")),
      tokenId = "96B0149B4EA098BE769EFDE5BD6A7403F3A25BA1"), // cf. last digit!)
    printedNumber = Some("YYABCC00000003J"),

    expiryDate = DateTimeNoMillis("2014-07-14T00:00:00Z")
  )
}
