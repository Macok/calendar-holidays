name := "calendar-holidays"

version in Global := "1.0"

scalaVersion in Global := "2.11.8"

val `com.typesafe.scala-logging_scala-logging` = "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"
val `com.typesafe.play` = "com.typesafe.play" %% "play-ws" % "2.4.0-M2"

lazy val restapi = Project("restapi", file("restapi"))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin).settings(
  libraryDependencies ++= Seq(
    `com.typesafe.scala-logging_scala-logging`
  )
)

libraryDependencies ++= Seq(
  `com.typesafe.scala-logging_scala-logging`,
  `com.typesafe.play`
)