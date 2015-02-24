name := "scala-utils-play"


libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.3.8" withSources()
)

publishTo := {
  Some("WiredThing Internal Libraries Repository" at "https://wiredthing.artifactoryonline.com/wiredthing/libs-releases-local")
}
