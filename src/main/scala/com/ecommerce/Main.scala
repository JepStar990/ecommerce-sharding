package com.ecommerce

import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import com.ecommerce.sharding.ConsistentHasher
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import redis.clients.jedis.Jedis
import scala.concurrent.duration._
import scala.jdk.CollectionConverters._

object Main extends App {
  // Load config
  val config = ConfigFactory.load()
  
  // Create Redis clients for shards
  val redisShards = config.getConfigList("redis.shards").asScala.map { shardConfig =>
    new Jedis(shardConfig.getString("host"), shardConfig.getInt("port"))
  }.toList
  
  // Consistent hasher for sharding
  val hasher = new ConsistentHasher(redisShards.length)
  
  def getShard(userId: String): Jedis = {
    redisShards(hasher.getShard(userId))
  }
  
  // Kafka setup
  implicit val system: ActorSystem = ActorSystem("EcommerceSystem")
  implicit val ec = system.dispatcher
  
  // Producer - for testing
  val producerSettings = ProducerSettings(
    system,
    new StringSerializer,
    new StringSerializer
  ).withBootstrapServers(config.getString("kafka.bootstrap-servers"))
  
  // Consumer - main pipeline
  val consumerSettings = ConsumerSettings(
    system,
    new StringDeserializer,
    new StringDeserializer
  ).withBootstrapServers(config.getString("kafka.bootstrap-servers"))
   .withGroupId(config.getString("kafka.group-id"))
   .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  
  // Start consuming
  Consumer
    .plainSource(consumerSettings, Subscriptions.topics(config.getString("kafka.topic")))
    .runWith(Sink.foreach { record =>
      val userId = record.key()
      val eventData = record.value()
      
      // Get shard and store
      val shard = getShard(userId)
      try {
        shard.set(s"user:$userId:last_event", eventData)
        println(s"Stored event for $userId in shard ${shard.getDB}")
      } catch {
        case e: Exception => println(s"Failed to store event: ${e.getMessage}")
      }
    })
  
  // Simple producer to test - send sample events
  system.scheduler.scheduleOnce(3.seconds) {
    val producer = producerSettings.createKafkaProducer()
    (1 to 10).foreach { i =>
      val userId = s"user_$i"
      producer.send(new org.apache.kafka.clients.producer.ProducerRecord(
        config.getString("kafka.topic"), 
        userId, 
        s"event_data_$i"
      ))
    }
    producer.close()
  }
  
  // Shutdown hook
  sys.addShutdownHook {
    system.terminate()
    redisShards.foreach(_.close())
  }
}
