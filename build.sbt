name := "http4s-tutorial"

version := "0.1"

scalaVersion := "2.12.10"

val http4sVersion = "0.20.13"

// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl"          % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-json4s-native" % http4sVersion,
  "org.slf4j"   % "slf4j-api"           % "1.7.5",
  "org.slf4j"   % "slf4j-simple"        % "1.7.5",

  "org.http4s" %% "http4s-circe" % http4sVersion,
  // Optional for auto-derivation of JSON codecs
  "io.circe" %% "circe-generic" % "0.11.1",
  // Optional for string interpolation to JSON model
  "io.circe" %% "circe-literal" % "0.11.1",

  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
)

scalacOptions ++= Seq("-Ypartial-unification")
