name := "swarm-msg"

version := "1.0"

scalaVersion := "2.12.1"

resolvers += "Akka snapshot repository" at "http://repo.akka.io/snapshots"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.14",
	"com.typesafe.akka" %% "akka-stream" % "2.4.14"
)

libraryDependencies += "com.sparkjava" % "spark-core" % "2.5.3"
libraryDependencies += "com.sparkjava" % "spark-template-jade" % "2.3"
