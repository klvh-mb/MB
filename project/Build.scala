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
    "commons-io" % "commons-io" % "2.1",
     "be.objectify"  %%  "deadbolt-java"     % "2.1-RC2",
     "com.feth" %% "play-easymail" % "0.3-SNAPSHOT",
     "org.mindrot" % "jbcrypt" % "0.3m",
     "org.apache.httpcomponents" % "httpclient" % "4.2.5"
     
    
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
      Keys.fork in (Test) := false,
      resolvers += Resolver.url("Objectify Play Repository (release)", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("Objectify Play Repository (snapshot)", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns)
        
  )

}
