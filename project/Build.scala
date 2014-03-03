import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "parent-social"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaJpa,
    "mysql" % "mysql-connector-java" % "5.1.18",
    "org.hibernate" % "hibernate-entitymanager" % "4.2.7.Final",
    "org.hamcrest" % "hamcrest-all" % "1.3",
    "commons-io" % "commons-io" % "2.1"
    
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    Keys.fork in (Test) := false     
  )

}
