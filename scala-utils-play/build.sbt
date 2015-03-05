name := "scala-utils-play"

version := IO.read(baseDirectory.value / ".." / "scala-utils" / "version").trim()

publishTo  := {
  if (isSnapshot.value)
    Some("WiredThing Internal Snapshots Repository" at "https://wiredthing.artifactoryonline.com/wiredthing/libs-snapshots-local")
  else
    Some("WiredThing Internal Libraries Repository" at "https://wiredthing.artifactoryonline.com/wiredthing/libs-releases-local")
}

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.3.8" withSources()
)
