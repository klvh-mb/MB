MB
==
1. /project/build.properties 
        sbt.version=0.13.0
2. /project/plugins.sbt 
        addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.1")
3. /conf/application.conf 
        db.default.url="jdbc:mysql://localhost:3306/parentsocial?characterEncoding=UTF-8"
        #db.default.password=admin