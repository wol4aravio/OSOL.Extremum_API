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

  case class InfoJSON(name: String, n_dim: Int, search_area: List[(Double, Double)], x_optimal: List[Double])

  def healthCheck: Endpoint[IO, String] = get(pathEmpty) {
    Ok("OK")
  }

  def sphereFunctionInfo: Endpoint[IO, InfoJSON] = get("sphere" :: path[Int] :: "info") {
    n_dim: Int => {
      val f = new SphereFunction(n_dim)
      Ok(InfoJSON(f.name, f.n_dim, f.search_area, f.x_optimal))
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