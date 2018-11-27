package org.after90

import org.apache.spark.internal.Logging

object HelloWorld extends Logging {
  def main(args: Array[String]): Unit = {
    println("李伟，等我开着普拉多去找你喝酒--20181127")
    logInfo("this is log info")
    logError("this is log error")
    logWarning("this is log warn")
  }
}
