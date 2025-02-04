name := "2gisTest"
organization := "ru.mukov"
version := "0.1.0"

scalaVersion := "2.13.16"

libraryDependencies ++= Seq(
  // HTTP
  "org.http4s"          %% "http4s-blaze-server"   % "0.23.16",
  "org.http4s"          %% "http4s-blaze-client"   % "0.23.16",
  "org.http4s"          %% "http4s-dsl"            % "0.23.16",
  "org.http4s"          %% "http4s-circe"          % "0.23.16",

  // JSON(circe)
  "io.circe"            %% "circe-core"            % "0.14.6",
  "io.circe"            %% "circe-generic"         % "0.14.6",

  // HTML
  "org.jsoup"            % "jsoup"                 % "1.17.2",

  // CE
  "org.typelevel"       %% "cats-effect"           % "3.5.4",

  // Config
  "com.github.pureconfig" %% "pureconfig"          % "0.17.6",

  // Logging
  "org.typelevel"       %% "log4cats-slf4j"        % "2.6.0",
  "ch.qos.logback"       % "logback-classic"       % "1.4.14"
)

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Ymacro-annotations",
  "-Ywarn-unused:imports"
)

Compile / run / fork := true