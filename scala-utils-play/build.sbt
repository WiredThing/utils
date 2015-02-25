name := "scala-utils-play"

version := IO.read(baseDirectory.value / ".." / "scala-utils" / "version").trim()

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.3.8" withSources()
)
