package org.after90.sparkStreaming.kafka

import java.util.Properties
import java.util.concurrent.TimeoutException

import kafka.admin.AdminUtils
import kafka.api.Request
import kafka.server.KafkaServer
import kafka.utils.ZkUtils
import org.apache.spark.internal.Logging
import org.apache.spark.streaming.Time

import scala.annotation.tailrec
import scala.util.control.NonFatal

object KafkaTest extends Logging{
  private var zkUtils: ZkUtils = _
  // Kafka broker server
  private var server: KafkaServer = _
  def main(args: Array[String]): Unit = {

  }
  /** Create a Kafka topic and wait until it is propagated to the whole cluster */
  def createTopic(topic: String, partitions: Int, config: Properties): Unit = {
    AdminUtils.createTopic(zkUtils, topic, partitions, 1, config)
    // wait until metadata is propagated
    (0 until partitions).foreach { p =>
      waitUntilMetadataIsPropagated(topic, p)
    }
  }

  private def waitUntilMetadataIsPropagated(topic: String, partition: Int): Unit = {
    def isPropagated = server.apis.metadataCache.getPartitionInfo(topic, partition) match {
      case Some(partitionState) =>
        val leader = partitionState.basePartitionState.leader
        val isr = partitionState.basePartitionState.isr
        zkUtils.getLeaderForPartition(topic, partition).isDefined &&
          Request.isValidBrokerId(leader) && !isr.isEmpty
      case _ =>
        false
    }
    eventually(Time(10000), Time(100)) {
      assert(isPropagated, s"Partition [$topic, $partition] metadata not propagated after timeout")
    }
  }


  // A simplified version of scalatest eventually, rewritten here to avoid adding extra test
  // dependency
  def eventually[T](timeout: Time, interval: Time)(func: => T): T = {
    def makeAttempt(): Either[Throwable, T] = {
      try {
        Right(func)
      } catch {
        case e if NonFatal(e) => Left(e)
      }
    }

    val startTime = System.currentTimeMillis()
    @tailrec
    def tryAgain(attempt: Int): T = {
      makeAttempt() match {
        case Right(result) => result
        case Left(e) =>
          val duration = System.currentTimeMillis() - startTime
          if (duration < timeout.milliseconds) {
            Thread.sleep(interval.milliseconds)
          } else {
            throw new TimeoutException(e.getMessage)
          }

          tryAgain(attempt + 1)
      }
    }

    tryAgain(1)
  }
}
