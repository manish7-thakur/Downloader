name := "Downloader"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "spray" at "http://repo.spray.io/"

libraryDependencies <++= scalaVersion { v =>
  val sprayVersion = "1.3.3"
  Seq(
    "org.specs2" %% "specs2" % "2.4.17" % "test",
    "io.spray" %% "spray-testkit" % sprayVersion % "test",
    "io.spray" %% "spray-routing" % sprayVersion,
    "org.scalatest" % "scalatest_2.11" % "3.0.0-M15",
    "com.jcraft" % "jsch" % "0.1.53"
  )
}
