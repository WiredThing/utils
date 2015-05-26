name := "play-status"

version := IO.read(baseDirectory.value / "version").trim()

publishTo  := {
  if (isSnapshot.value)
    Some("WiredThing Public Snapshots Repository" at "https://wiredthing.artifactoryonline.com/wiredthing/libs-snapshots-public")
  else
    Some("WiredThing Public Libraries Repository" at "https://wiredthing.artifactoryonline.com/wiredthing/libs-releases-public")
}

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.4",
  "net.ceedubs" %% "ficus" % "1.1.1",
  ws
)

enablePlugins(PlayScala)

