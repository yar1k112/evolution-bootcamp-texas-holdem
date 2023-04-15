ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val http4sVersion = "0.23.18"
val circeVersion  = "0.14.1"

lazy val root = (project in file("."))
  .settings(
    name         := "texas-holdem",
    organization := "courseWork",
    libraryDependencies ++= Seq(
      "org.http4s"    %% "http4s-ember-client" % http4sVersion,
      "org.http4s"    %% "http4s-ember-server" % http4sVersion,
      "org.http4s"    %% "http4s-dsl"          % http4sVersion,
      "org.http4s"    %% "http4s-circe"        % http4sVersion,
      "io.circe"      %% "circe-generic"       % circeVersion,
      "org.typelevel" %% "cats-effect"         % "3.4.8",
      "ch.qos.logback" % "logback-classic"     % "1.4.6" % Runtime
    )
  )
