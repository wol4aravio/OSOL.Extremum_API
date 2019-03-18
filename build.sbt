name := "OSOL.Extremum_API"

version := "0.0.1"

scalaVersion := "2.12.8"

val finchVersion = "0.26.0"
val circeVersion = "0.10.1"
val scalatestVersion = "3.0.5"

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finchx-core"  % finchVersion,
  "com.github.finagle" %% "finchx-circe"  % finchVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion % "test"
)