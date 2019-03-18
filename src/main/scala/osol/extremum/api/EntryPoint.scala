import cats.effect.IO
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.http.filter.JsonpFilter
import com.twitter.util.Await
import io.finch._
import io.finch.catsEffect._
import io.finch.circe._
import io.circe.generic.auto._

object Main extends App {

  case class Message(hello: String)

  def healthcheck: Endpoint[IO, String] = get(pathEmpty) {
    Ok("OK")
  }

  def helloWorld: Endpoint[IO, Map[String, String]] = get("hello") {
    Ok(Map(
      "hello" -> "World",
      "bye" -> "bye"
    ))
  }

  def service: Service[Request, Response] = JsonpFilter
      .andThen(helloWorld.toServiceAs[Application.Json])

  Await.ready(Http.server.serve(":8080", service))
}