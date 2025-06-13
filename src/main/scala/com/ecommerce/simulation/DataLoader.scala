package com.ecommerce.simulation

import scala.io.Source
import scala.util.Try

class DataLoader(filePath: String) {
  def loadSampleEvents(): List[(String, String)] = {
    Try {
      val lines = Source.fromFile(filePath).getLines()
      lines.drop(1) // Skip header
        .map(_.split(","))
        .map(fields => (fields(0), fields.mkString(","))) // (user_id, full_event)
        .toList
    }.getOrElse {
      println(s"Warning: Could not load dataset from $filePath")
      List(("user_1", "sample,event,data,1"), ("user_2", "sample,event,data,2"))
    }
  }
}
