package com.ecommerce.config

import com.typesafe.config.Config
import scala.jdk.CollectionConverters._

case class RedisShardConfig(host: String, port: Int)
case class AppConfig(
  kafka: KafkaConfig,
  redisShards: List[RedisShardConfig],
  datasetPath: String,
  simulationSpeed: Int
)

object AppConfig {
  def load(): AppConfig = {
    val config = com.typesafe.config.ConfigFactory.load()
    
    AppConfig(
      kafka = KafkaConfig(
        bootstrapServers = config.getString("kafka.bootstrap-servers"),
        topic = config.getString("kafka.topic"),
        groupId = config.getString("kafka.group-id")
      ),
      redisShards = config.getConfigList("redis.shards").asScala.map { shardConfig =>
        RedisShardConfig(
          host = shardConfig.getString("host"),
          port = shardConfig.getInt("port")
        )
      }.toList,
      datasetPath = config.getString("dataset.path"),
      simulationSpeed = config.getInt("simulation.speed-factor")
    )
  }
}

case class KafkaConfig(
  bootstrapServers: String,
  topic: String,
  groupId: String
)
