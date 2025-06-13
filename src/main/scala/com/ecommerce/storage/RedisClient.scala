package com.ecommerce.storage

import redis.clients.jedis.Jedis

class RedisClient(host: String, port: Int) {
  private val jedis = new Jedis(host, port)

  def store(key: String, value: String): Boolean = {
    try {
      jedis.set(key, value)
      true
    } catch {
      case e: Exception => 
        println(s"Redis error: ${e.getMessage}")
        false
    }
  }

  def close(): Unit = jedis.close()
}

object RedisClient {
  def apply(host: String, port: Int): RedisClient = new RedisClient(host, port)
}
