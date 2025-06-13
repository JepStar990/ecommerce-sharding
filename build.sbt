ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.12.17"
ThisBuild / organization := "com.ecommerce"

lazy val root = (project in file("."))
  .settings(
    name := "ecommerce-sharding",
    
    // Library dependencies
    libraryDependencies ++= Seq(
      // Kafka
      "com.typesafe.akka" %% "akka-stream-kafka" % "2.1.1",
      
      // Redis
      "redis.clients" % "jedis" % "4.3.1",
      
      // Config
      "com.typesafe" % "config" % "1.4.2",
      
      // Logging
      "ch.qos.logback" % "logback-classic" % "1.2.11",
      
      // Testing
      "org.scalatest" %% "scalatest" % "3.2.14" % Test,
      
      // Akka streams
      "com.typesafe.akka" %% "akka-stream" % "2.6.20"
    ),
    
    // Assembly plugin settings
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case PathList("META-INF", xs @ _*) => MergeStrategy.last
      case PathList("version.conf") => MergeStrategy.concat
      case PathList("reference.conf") => MergeStrategy.concat
      case PathList("scala", "annotation", "nowarn.class") => MergeStrategy.first
      case PathList("scala", "annotation", "nowarn$.class") => MergeStrategy.first
      case x => MergeStrategy.defaultMergeStrategy(x)
    },
    
    // Runtime settings
    fork in run := true,
    javaOptions in run ++= Seq(
      "-Xmx1G",
      "-XX:+UseG1GC",
      "-Dlogback.configurationFile=src/main/resources/logback.xml"
    ),
    
    // Resource handling
    Compile / unmanagedResources / includeFilter := "*.xml" || "*.conf" || "*.properties",
    
    // Packaging
    assembly / assemblyJarName := "ecommerce-sharding.jar",
    assembly / mainClass := Some("com.ecommerce.Main")
  )
