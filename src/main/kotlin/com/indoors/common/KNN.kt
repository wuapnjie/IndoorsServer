package com.indoors.common

import com.indoors.Position
import com.indoors.Room
import com.indoors.RoomPosition
import com.indoors.WiFiInfo
import java.util.*
import kotlin.math.sqrt

/**
 * @author wupanjie on 2017/12/9.
 */

fun Room.computePosition(wifiList: List<WiFiInfo>, K: Int = 4): Position {

  val positionsWithDistance = arrayListOf<Pair<RoomPosition, Double>>()
  // 计算所有的距离
  this.positions.forEach {
    val distance = wifiList to it
    positionsWithDistance += it to distance
  }

  // 对距离进行排序
  positionsWithDistance.sortWith(Comparator { o1, o2 ->
    o1.second.compareTo(o2.second)
  })

  return Position(positionsWithDistance[0].first.x, positionsWithDistance[1].first.y)
//  val k = if (K < positionsWithDistance.size) K else positionsWithDistance.size
//
//  // 前k个1/distance的和
//  val d = (0 until k).sumByDouble { 1 / positionsWithDistance[it].second }
//
//  // 选出前k个邻近向量计算权值w
//  val w = arrayListOf<Double>()
//  for (i in 0 until k) {
//    w += (1 / positionsWithDistance[i].second) / d
//  }
//
//  // 计算出加权位置
//  var x = 0.0
//  var y = 0.0
//
//  for (i in 0 until k) {
//    x += w[i] * positionsWithDistance[i].first.x
//    y += w[i] * positionsWithDistance[i].first.y
//  }
//
//  return Position(x, y)
}

// 计算当前WiFi信息与房间基准点WiFi信息之间的欧式距离
infix fun List<WiFiInfo>.to(other: RoomPosition): Double {
  val otherRSSIMap = other.macRSSIMap

  val sum = this
      .asSequence()
      .map { wifiNetwork ->
        val otherRSSI = otherRSSIMap[wifiNetwork.BSSID]
        if (otherRSSI != null) (wifiNetwork.RSSI - otherRSSI) * (wifiNetwork.RSSI - otherRSSI)
        else 0
      }
      .sum()
      .toDouble()


  return sqrt(sum)

}
