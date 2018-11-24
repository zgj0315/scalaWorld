ThisBuild / scalaVersion := "2.12.7"
ThisBuild / organization := "org.after90"

lazy val hello = (project in file("."))
  .settings(
    name := "scalaWorld",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  )
