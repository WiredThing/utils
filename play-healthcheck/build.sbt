import NativePackagerKeys._

name := "play-healthcheck"

version := IO.read(baseDirectory.value / "version").trim()

enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "net.ceedubs" %% "ficus" % "1.1.1",
  ws
)

maintainer := "Doug Clinton <doug@wiredthing.com>"

dockerBaseImage := "wiredthing/oraclejdk:7u72"

dockerRepository := Some("dockerhost:9000/wiredthing")

dockerExposedPorts := Seq(9000)

dockerUpdateLatest := true