name := "scala-utils"

version := IO.read(baseDirectory.value / "version").trim()

publishTo  := {
  if (isSnapshot.value)
    Some("WiredThing Public Snapshots Repository" at "https://wiredthing.artifactoryonline.com/wiredthing/libs-snapshots-public")
  else
    Some("WiredThing Public Libraries Repository" at "https://wiredthing.artifactoryonline.com/wiredthing/libs-releases-public")
}

resolvers in ThisBuild += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.chuusai" %% "shapeless" % "2.2.0-RC5",
  "com.typesafe.akka" %% "akka-actor" % "2.3.7"
)