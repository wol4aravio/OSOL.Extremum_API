import org.rogach.scallop._

import cats.effect.IO
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Await
import io.finch._
import io.finch.catsEffect._
import io.finch.circe._
import io.circe.generic.auto._

import osol.extremum.api.Functions._


class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val port = opt[Int](default = Some(8080), validate = (0 <))
  verify()
}

object Main extends App {

  case class Message(hello: String)

  def healthCheck: Endpoint[IO, String] = get(pathEmpty) {
    Ok("OK")
  }

  def sphereFunctionInfo: Endpoint[IO, Map[String, Any]] = get("sphere" :: path[String] :: "info") {
    n_dim_str: String => {
      val f = new SphereFunction(n_dim_str.toInt)
      Ok(Map("name" -> f.name, "n_dim" -> f.n_dim, "search_area" -> f.search_area, "x_optimal" -> f.x_optimal))
    }
  }

  def service: Service[Request, Response] =
    Bootstrap
      .serve[Text.Plain](healthCheck)
      .serve[Application.Json](sphereFunctionInfo)
      .toService

  override def main(args: Array[String]): Unit = {
    val conf = new Conf(args)
    Await.ready(Http.server.serve(s":${conf.port()}", service))
  }
}