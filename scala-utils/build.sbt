name := "scala-utils"

version := IO.read(baseDirectory.value / "version").trim()

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.chuusai" %% "shapeless" % "2.1.0" withSources(),
  "com.typesafe.akka" %% "akka-actor" % "2.3.7"
)