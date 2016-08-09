name := "Downloader"

version := "1.0"

scalaVersion := "2.11.7"

resolvers ++= Seq("spray" at "http://repo.spray.io/",
"Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases")

libraryDependencies <++= scalaVersion { v =>
  val sprayVersion = "1.3.3"
  val akkaVersion = "2.3.12"
  Seq(
    "org.specs2" %% "specs2" % "2.4.17" % "test",
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.3",
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
