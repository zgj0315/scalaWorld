ThisBuild / version := "0.1.0"
//ThisBuild / scalaVersion := "2.12.7"
ThisBuild / scalaVersion := "2.11.12" //需要配合依赖包中spark streaming的版本修改scala版本
ThisBuild / organization := "org.after90"

lazy val hello = (project in file("."))
  .settings(
    name := "scalaWorld",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test,
    libraryDependencies += "org.apache.spark" % "spark-streaming_2.11" % "2.4.0",
    libraryDependencies += "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % "2.4.0"
  )
