name := "TouK"

version := "0.1"

scalaVersion := "2.12.6"

retrieveManaged := true

mainClass in assembly := Some("Main")

test in assembly := {}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard // needed to avoid deduplicate errors
  case PathList("reference.conf") => MergeStrategy.concat // needed for akka config files
  case x => MergeStrategy.first
}

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.11",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "com.typesafe.akka" %% "akka-http" % "10.1.0",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.1",
  "ch.megard" %% "akka-http-cors" % "0.3.0",

  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",

  "com.typesafe" % "config" % "1.3.2",

  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.1" % Test,
  "org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % Test,
  "org.scalamock" %% "scalamock" % "4.1.0" % Test,
  "org.scalacheck" %% "scalacheck" % "1.13.5" % Test
)