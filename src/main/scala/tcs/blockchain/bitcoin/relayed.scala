import net.liftweb.json._
import scala.util.matching._
import scala.io.Source._


object JsonParsingExample extends App {

  // Country json extract
  case class Country (name: String, code: String)
  case class Location (accuracy_radius: Number, latitude: Number, longitude: Number, time_zone: String)
  case class jsonOut (city: String, country:Country, location: Location, ip: String)

  implicit val formats = DefaultFormats

  // pattern regex IPV4
  val patternIPv4 = new Regex("(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])")

  // pattern regex IPV6
  val patternIPv6 = new Regex("(?<![:.\\w])(?:(?:(?:[A-Fa-f0-9]{1,4}:){6}|(?=(?:[A-Fa-f0-9]{0,4}:){0,6}(?:[0-9]{1,3}\\.){3}[0-9]{1,3}(?![:.\\w]))(([0-9A-Fa-f]{1,4}:){0,5}|:)((:[0-9a-fA-F]{1,4}){1,5}:|:)|::(?:[A-Fa-f0-9]{1,4}:){5})(?:(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])|(?:[A-Fa-f0-9]{1,4}:){7}[A-Fa-f0-9]{1,4}|(?=(?:[A-Fa-f0-9]{0,4}:){0,7}[A-Fa-f0-9]{0,4}(?![:.\\w]))(([0-9a-fA-F]{1,4}:){1,7}|:)((:[0-9a-fA-F]{1,4}){1,7}|:)|(?:[A-Fa-f0-9]{1,4}:){7}:|:(:[A-Fa-f0-9]{1,4}){7})(?![.\\w])")

  //val ipaddress = "2a00:1450:4007:816::2003"
  val ipaddress = "37.187.99.192:8080"

  /** URI di GeoIp
    *
    * schema: http://geoip.nekudo.com/api/{ip}/{language}/{type}
    */
  val apiCountry = "http://geoip.nekudo.com/api/"

  // verifica se l'ip è di tipo Ipv4
  val searchIpv4 = (patternIPv4 findFirstIn ipaddress)

  val ipGetCountry = apiCountry

  if(searchIpv4 == None) {

    println("stampa e nullo. Ip4: " + searchIpv4)

    val searchIpv6 = (patternIPv6 findFirstIn ipaddress)
    if (searchIpv6 == None) println("stampa e nullo. Ip6: " + searchIpv6)
    else {
      println("valore trovato. Ip6: " + searchIpv6.mkString)
      val ipGetCountry: String =  apiCountry + searchIpv6.mkString
      val result = fromURL(ipGetCountry).mkString
      // convert a String to a JValue object
      val jValue = parse(result)

      // alternative method for extract country name (liftweb api)
      //val jsearch = ((jValue \ "country") \ "name").extract[String]
      //println(jsearch)

      // method for extract country name
      val countryExtract = jValue.extract[jsonOut]
      val countryName = countryExtract.country.name

      //println(countryExtract)

      println(countryName)
    }
  }
  else {
    println("valore trovato. Ip4: ")
    val ipGetCountry: String = apiCountry + searchIpv4.mkString
    val result = fromURL(ipGetCountry).mkString

    // convert a String to a JValue object
    val jValue = parse(result)

    // alternative method for extract country name (liftweb api)
    //val jsearch = ((jValue \ "country") \ "name").extract[String]

    // method for extract country name
    val countryExtract = jValue.extract[jsonOut]
    val countryName = countryExtract.country.name

    //println(countryExtract)

    println(countryName)

  }







  /////



}
