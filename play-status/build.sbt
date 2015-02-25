name := "play-status"

version := IO.read(baseDirectory.value / "version").trim()

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.4",
  "net.ceedubs" %% "ficus" % "1.1.1",
  ws
)

enablePlugins(PlayScala)

