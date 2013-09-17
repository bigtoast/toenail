

import sbt._
import Keys._
import com.github.bigtoast.sbtliquibase.LiquibasePlugin._
import com.github.bigtoast.sbtthrift.ThriftPlugin._


object ToeNail extends Build {

	lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    	organization := "com.github.bigtoast",
    	version      := "0.1.0",
    	scalaVersion := "2.10.2",
    	crossPaths   := true )

	val spray_repo    = "spray repo" at "http://repo.spray.io"
  val bigtoast_repo = "bigtoast-github" at "http://bigtoast.github.com/repo/"

	val spray_can     = "io.spray"              % "spray-can"            % "1.2-M8"
  val spray_routing = "io.spray"              % "spray-routing"        % "1.2-M8"
  val akka          = "com.typesafe.akka"    %% "akka-actor"           % "2.2.0-RC1"

  val thriftlib     = "org.apache.thrift"     % "libthrift"            % "0.9.1"
  val commons_lang  = "commons-lang"          % "commons-lang"         % "2.6"

	val mysql      = "mysql"                 % "mysql-connector-java" % "5.1.24"
  val querulous  = "com.github.bigtoast"  %% "querulous-core"       % "0.1.0"

  //val dbcp  = "commons-dbcp" % "commons-dbcp"         % "1.4"
  //val pool  = "commons-pool" % "commons-pool"         % "1.5.4"

  val scalatest  = "org.scalatest" % "scalatest_2.10" % "2.0.M7" % "test"

  lazy val toenail = Project("toenail", base = file("."), settings = buildSettings ++ liquibaseSettings ++ Seq (
    libraryDependencies ++= mysql :: Nil
    //liquibaseChangelog := "recommend/src/main/migrations/changelog.xml"
    ,liquibaseUsername  := ""
    ,liquibasePassword  := ""
    ,liquibaseDriver    := "com.mysql.jdbc.Driver"
    ,liquibaseUrl       := "jdbc:mysql://%s:3306/%s?createDatabaseIfNotExist=true".format( "localhost", "testdb")
  ) )
    .aggregate(common, recommend, backtest, emailer)

  lazy val common = Project("common",
    base    = file("common"),
    settings = buildSettings ++ thriftSettings ++ Seq(
      libraryDependencies ++= thriftlib :: commons_lang :: Nil
    ))

	lazy val recommend = Project("recommend",
		base     = file("recommend"),
		settings = buildSettings ++ Seq(
      
      libraryDependencies ++= mysql :: querulous :: spray_can :: akka :: spray_routing :: Nil
      
      ,resolvers ++= spray_repo :: bigtoast_repo :: Nil

      ,mainClass := Some("toenail.Main")
      
      ,publishTo := Some(Resolver.file("bigtoast.github.com", file(Path.userHome + "/Projects/BigToast/bigtoast.github.com/repo")))

      )).dependsOn(common)

  lazy val backtest = Project("backtest",
    base    = file("backtest"),
    settings = buildSettings ++ Seq(

    )).dependsOn(common)

  lazy val emailer = Project("emailer",
    base    = file("emailer"),
    settings = buildSettings ++ Seq(

    )).dependsOn(common)


}