name := "play-status"

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.4",
  "net.ceedubs" %% "ficus" % "1.1.1",
  ws
)

enablePlugins(PlayScala)

