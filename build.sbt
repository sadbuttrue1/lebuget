import sbt.Keys._

name := "lebuget"

organization := "tk.sadbuttrue"

version := "0.1"

lazy val `lebuget` = (project in file(".")).aggregate(`identity-manager`)

val akkaV = "2.4.10"
val slickV = "3.1.1"
val postgresV = "9.4-1206-jdbc41"

val `akka-actor` = "com.typesafe.akka" %% "akka-actor" % akkaV
val `akka-stream` = "com.typesafe.akka" %% "akka-stream" % akkaV
val `akka-http` = "com.typesafe.akka" %% "akka-http-experimental" % akkaV
val `akka-http-spray` = "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV
val slick = "com.typesafe.slick" %% "slick" % slickV
val postgresql = "org.postgresql" % "postgresql" % postgresV

lazy val `identity-manager` = (project in file("identity-manager")).settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      `akka-actor`,
      `akka-stream`,
      `akka-http`,
      `akka-http-spray`,
      slick,
      postgresql
    ),
    Revolver.settings
  )

lazy val commonSettings = Seq(
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
  scalaVersion := "2.11.8",
  resolvers ++= Seq("Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/")
)

val runAll = inputKey[Unit]("Runs all subprojects")

runAll := {
  (run in Compile in `identity-manager`).evaluated
}

fork in run := true

// enables unlimited amount of resources to be used :-o just for runAll convenience
concurrentRestrictions in Global := Seq(
  Tags.customLimit(_ => true)
)
