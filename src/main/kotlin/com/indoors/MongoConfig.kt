package com.indoors

import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

/**
 * @author wupanjie on 2017/12/4.
 */

val mongoConfig = json {
  obj(
      "connection_string" to "mongodb://localhost:27017",
      "db_name" to "indoors",
      "maxPoolSize" to 100,
      "minPoolSize" to 1
  )
}