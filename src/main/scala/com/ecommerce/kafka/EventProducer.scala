package com.ecommerce.kafka

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

class EventProducer(topic: String, producerSettings: ProducerSettings[String, String]) {
  def send(key: String, value: String): Unit = {
    val producer = producerSettings.createKafkaProducer()
    producer.send(new ProducerRecord[String, String](topic, key, value))
    producer.close()
  }
}

object EventProducer {
  def apply(topic: String, bootstrapServers: String)(implicit system: ActorSystem): EventProducer = {
    val settings = ProducerSettings(
      system,
      new StringSerializer,
      new StringSerializer
    ).withBootstrapServers(bootstrapServers)
    new EventProducer(topic, settings)
  }
}
