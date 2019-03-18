package osol.extremum.api

object Functions {

  abstract class OptimizationBenchmark {
    val search_area: List[(Double, Double)]
    val x_optimal: List[Double]

    def calculate(x: List[Double]): Double
  }

  class SphereFunction(n_dim: Int) extends OptimizationBenchmark {
    val search_area: List[(Double, Double)] = (1 to n_dim).map(_ => (-25.0, 25.0)).toList
    val x_optimal: List[Double] = List.fill[Double](n_dim)(0.0)

    def calculate(x: List[Double]): Double = x.map(v => v * v).sum
  }

}
