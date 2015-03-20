import NativePackagerKeys._

name := "healthcheck"

version := IO.read(baseDirectory.value / "version").trim()

publishTo  := {
  if (isSnapshot.value)
    Some("WiredThing Internal Snapshots Repository" at "https://wiredthing.artifactoryonline.com/wiredthing/libs-snapshots-local")
  else
    Some("WiredThing Internal Libraries Repository" at "https://wiredthing.artifactoryonline.com/wiredthing/libs-releases-local")
}

enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "net.ceedubs" %% "ficus" % "1.1.1",
  ws
)

maintainer := "Doug Clinton <doug@wiredthing.com>"

dockerBaseImage := "wiredthing/oraclejdk:7u72"

dockerRepository := Some("localhost:9000/wiredthing")

dockerExposedPorts := Seq(9200)

dockerUpdateLatest := true