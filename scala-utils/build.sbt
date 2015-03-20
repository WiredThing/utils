name := "scala-utils"

version := IO.read(baseDirectory.value / "version").trim()

publishTo  := {
  if (isSnapshot.value)
    Some("WiredThing Internal Snapshots Repository" at "https://wiredthing.artifactoryonline.com/wiredthing/libs-snapshots-local")
  else
    Some("WiredThing Internal Libraries Repository" at "https://wiredthing.artifactoryonline.com/wiredthing/libs-releases-local")
}

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.chuusai" %% "shapeless" % "2.1.0",
  "com.typesafe.akka" %% "akka-actor" % "2.3.7"
)