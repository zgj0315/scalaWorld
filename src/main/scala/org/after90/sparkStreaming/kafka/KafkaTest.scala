package org.after90.sparkStreaming.kafka

import java.io.IOException
import java.net.InetSocketAddress
import java.util.Properties
import java.util.concurrent.TimeoutException

import kafka.admin.AdminUtils
import kafka.api.Request
import kafka.server.{KafkaConfig, KafkaServer}
import kafka.utils.ZkUtils
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.network.ListenerName
import org.apache.spark.{SparkConf, SparkException}
import org.apache.spark.internal.Logging
import org.apache.spark.streaming.Time
import org.apache.spark.util.{ShutdownHookManager, Utils}
import org.apache.zookeeper.server.{NIOServerCnxnFactory, ZooKeeperServer}

import scala.annotation.tailrec
import scala.util.control.NonFatal

object KafkaTest extends Logging {

  // Zookeeper related configurations
  private val zkHost = "127.0.0.1"
  private var zkPort: Int = 0
  private val zkConnectionTimeout = 60000
  private val zkSessionTimeout = 10000

  private var zookeeper: EmbeddedZookeeper = _

  private var zkUtils: ZkUtils = _

  // Kafka broker related configurations
  private val brokerHost = "127.0.0.1"
  private var brokerPort = 0
  private var brokerConf: KafkaConfig = _

  // Kafka broker server
  private var server: KafkaServer = _

  // Kafka producer
  private var producer: KafkaProducer[String, String] = _

  // Flag to test whether the system is correctly started
  private var zkReady = false
  private var brokerReady = false
  private var leakDetector: AnyRef = null

  def main(args: Array[String]): Unit = {
    val topic = "topic_zhaogj"
    createTopic(topic, 1, new Properties())
  }


  /** setup the whole embedded servers, including Zookeeper and Kafka brokers */
  def setup(): Unit = {
    // Set up a KafkaTestUtils leak detector so that we can see where the leak KafkaTestUtils is
    // created.
    val exception = new SparkException("It was created at: ")
    leakDetector = ShutdownHookManager.addShutdownHook { () =>
      logError("Found a leak KafkaTestUtils.", exception)
    }

    setupEmbeddedZookeeper()
    setupEmbeddedKafkaServer()
  }


  // Set up the Embedded Kafka server
  private def setupEmbeddedKafkaServer(): Unit = {
    assert(zkReady, "Zookeeper should be set up beforehand")

    // Kafka broker startup
    Utils.startServiceOnPort(brokerPort, port => {
      brokerPort = port
      brokerConf = new KafkaConfig(brokerConfiguration, doLog = false)
      server = new KafkaServer(brokerConf)
      server.startup()
      brokerPort = server.boundPort(new ListenerName("PLAINTEXT"))
      (server, brokerPort)
    }, new SparkConf(), "KafkaBroker")

    brokerReady = true
  }

  private def brokerConfiguration: Properties = {
    val props = new Properties()
    props.put("broker.id", "0")
    props.put("host.name", "127.0.0.1")
    props.put("advertised.host.name", "127.0.0.1")
    props.put("port", brokerPort.toString)
    props.put("log.dir", brokerLogDir)
    props.put("zookeeper.connect", zkAddress)
    props.put("zookeeper.connection.timeout.ms", "60000")
    props.put("log.flush.interval.messages", "1")
    props.put("replica.socket.timeout.ms", "1500")
    props.put("delete.topic.enable", "true")
    props.put("offsets.topic.num.partitions", "1")
    props.put("offsets.topic.replication.factor", "1")
    props.put("group.initial.rebalance.delay.ms", "10")
    props
  }

  val brokerLogDir = Utils.createTempDir().getAbsolutePath

  def zkAddress: String = {
    assert(zkReady, "Zookeeper not setup yet or already torn down, cannot get zookeeper address")
    s"$zkHost:$zkPort"
  }

  // Set up the Embedded Zookeeper server and get the proper Zookeeper port
  private def setupEmbeddedZookeeper(): Unit = {
    // Zookeeper server startup
    zookeeper = new EmbeddedZookeeper(s"$zkHost:$zkPort")
    // Get the actual zookeeper binding port
    zkPort = zookeeper.actualPort
    zkUtils = ZkUtils(s"$zkHost:$zkPort", zkSessionTimeout, zkConnectionTimeout, false)
    zkReady = true
  }

  private class EmbeddedZookeeper(val zkConnect: String) {
    val snapshotDir = Utils.createTempDir()
    val logDir = Utils.createTempDir()

    val zookeeper = new ZooKeeperServer(snapshotDir, logDir, 500)
    val (ip, port) = {
      val splits = zkConnect.split(":")
      (splits(0), splits(1).toInt)
    }
    val factory = new NIOServerCnxnFactory()
    factory.configure(new InetSocketAddress(ip, port), 16)
    factory.startup(zookeeper)

    val actualPort = factory.getLocalPort

    def shutdown() {
      factory.shutdown()
      // The directories are not closed even if the ZooKeeper server is shut down.
      // Please see ZOOKEEPER-1844, which is fixed in 3.4.6+. It leads to test failures
      // on Windows if the directory deletion failure throws an exception.
      try {
        Utils.deleteRecursively(snapshotDir)
      } catch {
        case e: IOException if Utils.isWindows =>
          logWarning(e.getMessage)
      }
      try {
        Utils.deleteRecursively(logDir)
      } catch {
        case e: IOException if Utils.isWindows =>
          logWarning(e.getMessage)
      }
    }
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
