name := "Downloader"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "spray" at "http://repo.spray.io/"

libraryDependencies <++= scalaVersion { v =>
  val sprayVersion = "1.3.3"
  val akkaVersion = "2.3.12"
  Seq(
    "org.specs2" %% "specs2" % "2.4.17" % "test",
    "io.spray" %% "spray-testkit" % sprayVersion % "test",
    "io.spray" %% "spray-routing" % sprayVersion,
    "org.scalatest" % "scalatest_2.11" % "3.0.0-M15",
    "com.jcraft" % "jsch" % "0.1.53",
    "org.specs2" %% "specs2-mock" % "2.4.17" % "test",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "io.spray" % "spray-json_2.11" % "1.3.2",
    "io.spray" %% "spray-can" % sprayVersion
  )
}
