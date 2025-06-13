package com.ecommerce.sharding

import java.security.MessageDigest

class ConsistentHasher(numberOfShards: Int) {
  private val md5 = MessageDigest.getInstance("MD5")

  def getShard(key: String): Int = {
    val hash = md5.digest(key.getBytes).map(b => b & 0xff).mkString
    Math.abs(hash.hashCode) % numberOfShards
  }
}

object ConsistentHasher {
  def apply(shards: Int): ConsistentHasher = new ConsistentHasher(shards)
}
