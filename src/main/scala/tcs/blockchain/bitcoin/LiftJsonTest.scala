import scala.collection.mutable._
import net.liftweb.json._
import net.liftweb.json.Serialization.write

case class Person(name: String, address: Address)

case class Address(city: String, state: String)

case class Persona(nome: String, cognome: String, cap: Int)

object LiftJsonTest extends App {

  val p = Person("Alvin Alexander", Address("Talkeetna", "AK"))

  val pino = Persona("giuseppe", "bellisano", 912)

  // create a JSON string from the Person, then print it
  implicit val formats = DefaultFormats
  val jsonString = write(p)
  println(jsonString)

  val jsonString2 = write(pino)
  println(jsonString2)


}