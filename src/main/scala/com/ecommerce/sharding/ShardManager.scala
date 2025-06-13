package com.ecommerce.sharding

import com.ecommerce.storage.RedisClient

class ShardManager(shards: List[RedisClient]) {
  private val hasher = new ConsistentHasher(shards.size)

  def getShardFor(key: String): RedisClient = {
    shards(hasher.getShard(key))
  }

  def closeAll(): Unit = shards.foreach(_.close())
}
