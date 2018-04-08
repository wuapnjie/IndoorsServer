package com.indoors.common

import com.indoors.Position
import com.indoors.Room
import com.indoors.WiFiInfo

/**
 * @author wupanjie on 2017/12/9.
 */

fun Room.computePosition(wifiList: List<WiFiInfo>, K: Int = 4): Position {
//  val distances = arrayListOf<Double>()
//
//  // 计算所有的距离
//  this.positions.forEach {
//    val distance = wifiList to it
//    distances += distance
//  }
//
//  // 对距离进行排序
//  distances.sort()
//
//  val k = if (K < distances.size) K else distances.size
//
//  // 前k个1/distance的和
//  val d = (0 until k).sumByDouble { 1 / distances[it] }
//
//  // 选出前k个邻近向量计算权值w
//  val w = arrayListOf<Double>()
//  for (i in 0 until k) {
//    w += (1 / distances[i]) / d
//  }
//
//  // 计算出加权位置
//  var x = 0.0
//  var y = 0.0
//  for ((index, element) in this.positions.withIndex()) {
//    x += w[index] * element.x
//    y += w[index] * element.y
//  }
//
//  return Position(x, y)
  return Position(0.0,0.0)
}
//
//// 计算当前WiFi信息与房间基准点WiFi信息之间的欧式距离
//infix fun List<WifiNetwork>.to(other: RoomPosition): Double {
//  val otherRSSIMap = other.macRSSIMap
//
//  val sum = this
//      .asSequence()
//      .map { wifiNetwork ->
//        val otherRSSI = otherRSSIMap[wifiNetwork.BSSID] ?: -1
//        if (otherRSSI != -1) (wifiNetwork.RSSI - otherRSSI) * (wifiNetwork.RSSI - otherRSSI) else 0
//      }
//      .sum()
//      .toDouble()
//
//
//  return sqrt(sum)

//}
