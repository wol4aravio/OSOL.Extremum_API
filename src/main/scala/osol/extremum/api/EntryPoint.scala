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

  def listAvailableFunctions: Endpoint[IO, Seq[String]] = get("functions") {
    Ok(Seq("sphere", "rastrigin"))
  }

  def service: Service[Request, Response] =
    Bootstrap
     .serve[Text.Plain](healthCheck)
     .serve[Application.Json](listAvailableFunctions)
     .serve[Application.Json](SphereFunction.functionInfo :+: SphereFunction.functionCalc)
     .serve[Application.Json](RastriginFunction.functionInfo :+: RastriginFunction.functionCalc)
     .toService

  override def main(args: Array[String]): Unit = {
    val conf = new Conf(args)
    Await.ready(Http.server.serve(s":${conf.port()}", service))
  }
}