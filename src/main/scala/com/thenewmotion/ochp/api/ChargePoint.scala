package com.thenewmotion.ochp.api

import com.github.nscala_time.time.Imports._
import org.joda.time.format.ISODateTimeFormat

case class ChargePoint (
  evseId: EvseId,
  locationId: String,
  timestamp: Option[DateTime] = None,
  locationName: String,
  locationNameLang: String,
  images: List[EvseImageUrl] = List(),
  relatedResources: List[RelatedResource] = Nil,
  address: CpAddress,
  chargePointLocation: GeoPoint,
  relatedLocations: List[AdditionalGeoPoint] = Nil,
  timeZone: Option[DateTimeZone] = None,
  category: Option[String] = None,
  operatingTimes: Option[Hours] = None,
  accessTimes: Option[Hours] = None,
  status: Option[ChargePointStatus.Value] = None,
  statusSchedule: List[ChargePointSchedule] = List(),
  telephoneNumber: Option[String] = None,
  location: GeneralLocation.Value = GeneralLocation.unknown,
  floorLevel: Option[String] = None,
  parkingSlotNumber: Option[String] = None,
  parkingRestriction: List[ParkingRestriction.Value] = List(),
  authMethods: List[AuthMethod.Value], //must be non-empty
  connectors: List[Connector], //must be non-empty
  ratings: Option[Ratings] = None,
  userInterfaceLang: List[String] = List()
) {
  def geoUserInterface: Option[AdditionalGeoPoint] =
    relatedLocations.find(_.typ == GeoPointTypes.ui)

  def geoSiteAccess: List[AdditionalGeoPoint] =
    relatedLocations.filter(_.typ == GeoPointTypes.access)

  def geoSiteEntrance: List[AdditionalGeoPoint] =
    relatedLocations.filter(_.typ == GeoPointTypes.entrance)

  def geoSiteExit: List[AdditionalGeoPoint] =
    relatedLocations.filter(_.typ == GeoPointTypes.exit)

  def geoSiteOther: List[AdditionalGeoPoint] =
    relatedLocations.filter(_.typ == GeoPointTypes.other)
}

case class CpAddress (
  houseNumber: Option[String] = None,
  address: String,
  city: String,
  zipCode: String,
  country: String
)

object CpTimestamp {
  val formatter = ISODateTimeFormat.dateTimeNoMillis()
  def apply(s: String) = formatter.parseDateTime(s)
  def unapply(dt: DateTime): String = dt.toString(formatter)
}

case class GeoPoint(lat: Double, lon: Double)

object GeoPoint {
  def apply(lat: String, lon: String) = new GeoPoint(lat.toDouble, lon.toDouble)
}

case class AdditionalGeoPoint(
  point: GeoPoint,
  name: Option[String],
  typ: GeoPointTypes.Value)

object GeoPointTypes extends QueryableEnumeration {
  type GeoPointTypes = Value
  val access = Value("access")
  val entrance = Value("entrance")
  val exit = Value("exit")
  val other = Value("other")
  val ui = Value("ui")
}

case class Hours (
  regularHoursOrTwentyFourSeven: Either[List[RegularHours], Boolean],
  exceptionalOpenings: List[ExceptionalPeriod],
  exceptionalClosings: List[ExceptionalPeriod])

case class RegularHours (
  weekday: Int = 0,
  periodBegin: TimeNoSecs,
  periodEnd: TimeNoSecs)

case class ExceptionalPeriod (
  periodBegin: DateTime,
  periodEnd: DateTime)

object ChargePointStatus extends QueryableEnumeration {
  type ChargePointStatus = Value
  val Unknown = Value("Unknown")
  val Operative = Value("Operative")
  val Inoperative = Value("Inoperative")
  val Planned = Value("Planned")
  val Closed = Value("Closed")
}

case class ChargePointSchedule (
  startDate: DateTime,
  endDate: Option[DateTime],
  status: ChargePointStatus.Value)

object ParkingRestriction extends QueryableEnumeration {
  type parkingRestriction = Value
  val evonly = Value("evonly")
  val plugged = Value("plugged")
  val disabled = Value("disabled")
  val customers = Value("customers")
  val motorcycles = Value("motorcycles")}

object AuthMethod extends QueryableEnumeration {
  type AuthMethod = Value
  val Public = Value("Public")
  val LocalKey = Value("LocalKey")
  val DirectCash = Value("DirectCash")
  val DirectCreditcard = Value("DirectCreditcard")
  val DirectDebitcard = Value("DirectDebitcard")
  val RfidMifareCls = Value("RfidMifareCls")
  val RfidMifareDes = Value("RfidMifareDes")
  val RfidCalypso = Value("RfidCalypso")
  val Iec15118 = Value("Iec15118")
  val OchpDirectAuth = Value("OchpDirectAuth")
  val OperatorAuth = Value("OperatorAuth")
}

object ImageClass extends QueryableEnumeration {
  type ImageClass = Value
  val networkLogo = Value("networkLogo")
  val operatorLogo = Value("operatorLogo")
  val ownerLogo = Value("ownerLogo")
  val stationPhoto = Value("stationPhoto")
  val locationPhoto = Value("locationPhoto")
  val entrancePhoto = Value("entrancePhoto")
  val otherPhoto = Value("otherPhoto")
  val otherLogo = Value("otherLogo")
  val otherGraphic = Value("otherGraphic")
}

object GeneralLocation extends QueryableEnumeration {
  type GeneralLocation = Value
  val `on-street` = Value("on-street")
  val `parking-garage` = Value("parking-garage")
  val `underground-garage` = Value("underground-garage")
  val `parking-lot` = Value("parking-lot")
  val other = Value("other")
  val unknown = Value("unknown")
}

case class RelatedResource(
  uri: String,
  `class`: RelatedResourceTypes.Value)

object RelatedResourceTypes extends QueryableEnumeration {
  type RelatedResourceTypes = Value
  val operatorMap = Value("operatorMap")
  val operatorPayment = Value("operatorPayment")
  val stationInfo = Value("stationInfo")
  val surroundingInfo = Value("surroundingInfo")
  val ownerHomepage = Value("ownerHomepage")
  val feedbackForm = Value("feedbackForm")
}

case class EvseImageUrl (
  uri: String,
  thumbUri: Option[String] = None,
  clazz: ImageClass.Value,
  `type`: String,
  width: Option[Integer] = None,
  height: Option[Integer] = None)

case class Ratings (
  maximumPower: Float,
  guaranteedPower: Option[Float],
  nominalVoltage: Option[Int])

case class EvseId(value: String) {
  require(value.matches(EvseId.pattern), s"evseId needs to conform to ${EvseId.pattern} but was $value")
}

object EvseId {
  val pattern = """([A-Za-z]{2})\*?([A-Za-z0-9]{3})\*?[Ee][A-Za-z0-9][A-Za-z0-9\*]{0,30}"""
  val regex = pattern.r
}
