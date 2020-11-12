import java.util._

object Main extends App {
  val props = new Properties()
  props.load(getClass.getResourceAsStream("/keys.properties"))

  val apiKey = props.getProperty("api-key")
}