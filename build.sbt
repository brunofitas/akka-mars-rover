name := "akka-mars-rover"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-language:postfixOps"
)

libraryDependencies := Seq(
  "com.typesafe.akka"   %% "akka-actor"     % "2.4.17",
  "com.typesafe.akka"   %% "akka-testkit"   % "2.4.17",
  "org.scalatest"        % "scalatest_2.11" % "3.0.0"
)


    