/**
 * @author wupanjie on 2017/12/4.
 */
package com.indoors

import jkid.JsonName

data class WifiNetwork(val SSID: String,
                       val BSSID: String,
                       val RSSI: Int,
                       val capabilities: String,
                       val channel: Int,
                       val frequency: String)

data class RoomPosition(val x: Double, val y: Double, val wifi_stats: List<WifiNetwork>) {

  val macRSSIMap : HashMap<String, Int>
    get() {
      val map = hashMapOf<String, Int>()
      wifi_stats.forEach {
        map.put(it.BSSID, it.RSSI)
      }

      return map
    }
}

data class Position(val x: Double, val y: Double)

data class Room(
    @JsonName("_id") val id: String,
    @JsonName("room_name") val name: String,
    val positions: List<RoomPosition>)