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

  def healthCheck: Endpoint[IO, String] = get(pathEmpty) {
    Ok("OK")
  }

  def service: Service[Request, Response] =
    Bootstrap
      .serve[Text.Plain](healthCheck)
      .serve[Application.Json](SphereFunction.functionInfo)
      .serve[Application.Json](SphereFunction.functionCalc)
      .toService

  override def main(args: Array[String]): Unit = {
    val conf = new Conf(args)
    Await.ready(Http.server.serve(s":${conf.port()}", service))
  }
}