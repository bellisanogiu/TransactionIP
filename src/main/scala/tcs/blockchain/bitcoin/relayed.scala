import net.liftweb.json._
import scala.util.matching._
import scala.io.Source._

// a case class to represent a mail server

object JsonParsingExample extends App {

  val patternIP = new Regex("([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})")

  val ipaddress = "37.187.99.192:8333"

  val ipv4 = (patternIP findAllIn ipaddress).mkString(",")
  val apiCountry = "http://geoip.nekudo.com/api/"

  val ipGetCountry: String =  apiCountry + ipv4

  // Country json extract

  case class Country (name: String, code: String)
  case class Location (accuracy_radius: Number, latitude: Number, longitude: Number, time_zone: String)

  case class jsonOut (city: Boolean, country:Country, location: Location, ip: String)

  implicit val formats = DefaultFormats

  val result = fromURL(ipGetCountry).mkString

  // convert a String to a JValue object
  val jValue = parse(result)

  // create a MailServer object from the string
  val countryExtract = jValue.extract[jsonOut]
  val countryName = countryExtract.country.name

  println(countryName)

}
