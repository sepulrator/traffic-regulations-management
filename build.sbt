lazy val root = (project in file("."))
  .settings(name := "online-auction-scala")
  .aggregate(driverApi, driverImpl,
//    lawApi, lawImpl
  )
  .settings(commonSettings: _*)


name := "traffic-regulations-management"

version := "0.1"

scalaVersion := "2.12.7"

resolvers += Resolver.mavenLocal
publishTo := Some(Resolver.mavenLocal)

val lombok = "org.projectlombok" % "lombok" % "1.16.18"
val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val playJsonDerivedCodecs = "org.julienrf" %% "play-json-derived-codecs" % "4.0.0"

val slickVersion = "3.2.1"
val slick = "com.typesafe.slick" %% "slick" % slickVersion
val slickHikariCP = "com.typesafe.slick" %% "slick-hikaricp" % slickVersion

val mysqlConnector = "mysql" % "mysql-connector-java" % "5.1.44"
val h2 = "com.h2database" % "h2" % "1.4.196"


lazy val common = (project in file("services/common"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      lagomScaladslServer % Optional,
      playJsonDerivedCodecs
    )
  )
  .settings(commonSettings: _*)

/*lazy val lawApi = (project in file("services/law-service/law-api"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      lagomScaladslServer % Optional,
      playJsonDerivedCodecs
    )
  )
  .settings(commonSettings: _*)


lazy val lawImpl = (project in file("services/law-service/law-impl"))
  .enablePlugins(LagomScala, SbtReactiveAppPlugin)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceJdbc,
      lagomScaladslServer,
      lagomScaladslTestKit,
      lagomScaladslKafkaBroker,
      "com.datastax.cassandra" % "cassandra-driver-extras" % "3.0.0",
      macwire,
      mysqlConnector,
      h2,
      scalaTest
    )
  )
  .settings(commonSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(lawApi)*/

lazy val driverApi = (project in file("services/driver-service/driver-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      lagomScaladslServer % Optional,
      playJsonDerivedCodecs
    )
  )
  .settings(commonSettings: _*)
  .dependsOn(common)


lazy val driverImpl = (project in file("services/driver-service/driver-impl"))
  .enablePlugins(LagomScala, SbtReactiveAppPlugin)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceJdbc,
      lagomScaladslServer,
      lagomScaladslTestKit,
      lagomScaladslKafkaBroker,
      "com.datastax.cassandra" % "cassandra-driver-extras" % "3.0.0",
      macwire,
      mysqlConnector,
      h2,
      slick,
      slickHikariCP,
      scalaTest
    )
  )
  .settings(commonSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(driverApi)


def commonSettings: Seq[Setting[_]] = Seq(
)

lagomCassandraCleanOnStart in ThisBuild := false