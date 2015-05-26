name := "scala-utils-play"

version := IO.read(baseDirectory.value / ".." / "scala-utils" / "version").trim()

publishTo  := {
  if (isSnapshot.value)
    Some("WiredThing Public Snapshots Repository" at "https://wiredthing.artifactoryonline.com/wiredthing/libs-snapshots-public")
  else
    Some("WiredThing Public Libraries Repository" at "https://wiredthing.artifactoryonline.com/wiredthing/libs-releases-public")
}

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.3.8",
  "com.typesafe.play" %% "play-json" % "2.3.8",
  "com.typesafe.play" %% "play-ws" % "2.3.8",
  "com.typesafe.play" %% "play-test" % "2.3.8" % Test,
  "com.ning" % "async-http-client" % "1.8.15"
)
