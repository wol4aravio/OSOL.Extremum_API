package osol.extremum.api

import cats.effect.IO
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Await
import io.finch._
import io.finch.catsEffect._
import io.finch.circe._
import io.circe.generic.auto._

object Functions {

  case class InfoJSON(name: String, n_dim: Int, search_area: Seq[(Double, Double)], x_optimal: Seq[Double])
  case class ResultJSON(result: Double)
  case class InputJSON(x: Seq[Double])

  abstract class OptimizationBenchmark {
    val name: String
    val slug_name: String

    def search_area(n_dim: Int): Seq[(Double, Double)]
    def x_optimal(n_dim: Int): Seq[Double]
    def calculate(x: Seq[Double]): Double

    def functionInfo: Endpoint[IO, InfoJSON] = get(slug_name :: "info" :: param[Int]("n_dim")) {
      n_dim: Int => Ok(InfoJSON(this.name, n_dim, this.search_area(n_dim), this.x_optimal(n_dim)))
    }

    def functionCalc: Endpoint[IO, ResultJSON] = get(slug_name :: "calc" :: jsonBody[InputJSON]) {
      inputJSON: InputJSON => Ok(ResultJSON(calculate(inputJSON.x)))
    }
  }

  object SphereFunction extends OptimizationBenchmark {
    val name = "SphereFunction"
    val slug_name = "sphere"

    def search_area(n_dim: Int): Seq[(Double, Double)] = (1 to n_dim).map(_ => (-10.0, 10.0))
    def x_optimal(n_dim: Int): Seq[Double] = List.fill[Double](n_dim)(0.0)
    def calculate(x: Seq[Double]): Double = x.map(v => v * v).sum
  }

  object RastriginFunction extends OptimizationBenchmark {
    val name = "RastriginFunction"
    val slug_name = "rastrigin"

    def search_area(n_dim: Int): Seq[(Double, Double)] = (1 to n_dim).map(_ => (-5.12, 5.12))
    def x_optimal(n_dim: Int): Seq[Double] = List.fill[Double](n_dim)(0.0)
    def calculate(x: Seq[Double]): Double = x.map(v => v * v - 10.0 * math.cos(2.0 * math.Pi * v)).sum + 10.0 * x.size
  }

  object AckleyFunction extends OptimizationBenchmark {
    val name = "AckleyFunction"
    val slug_name = "ackley"

    def search_area(n_dim: Int): Seq[(Double, Double)] = (1 to n_dim).map(_ => (-32.768, 32.768))
    def x_optimal(n_dim: Int): Seq[Double] = List.fill[Double](n_dim)(0.0)
    def calculate(x: Seq[Double]): Double = {
      val a = 20
      val b = 0.2
      val c = 2 * math.Pi
      val d = x.size
      val part_1 = -a * math.exp(-b * math.sqrt(x.map(v => v * v).sum / d))
      val part_2 = -math.exp(x.map(v => math.cos(c * v)).sum / d)
      a + math.exp(1) + part_1 + part_2
    }
  }

}
