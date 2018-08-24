name := """play-scala"""

version := "1.0-SNAPSHOT"
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.12"
lazy val reactiveMongoVer = "0.12.6-play26"

libraryDependencies ++= Seq(
  jdbc,
  ws,
  guice,
  specs2 % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test,
  "org.reactivemongo" %% "play2-reactivemongo" % reactiveMongoVer,
  "io.swagger" %% "swagger-play2" % "1.6.0",
  "ai.x" %% "play-json-extensions" % "0.10.0",
  "org.mockito" % "mockito-core" % "2.10.0" % Test,
  "com.github.simplyscala" %% "scalatest-embedmongo" % "0.2.4" % Test,
  "org.mongodb.scala" %% "mongo-scala-driver" % "1.2.1" % Test,
  "org.webjars" % "swagger-ui" % "3.1.4", //play-swagger ui integration
  "ch.qos.logback" % "logback-classic" % "1.2.3" % Test,
  "org.reactivemongo" %% "play2-reactivemongo" % reactiveMongoVer
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

scalacOptions += "-Ylog-classpath"

fork in run := true