package com.indoors.common

import com.indoors.KEY_DATA
import com.indoors.KEY_MESSAGE
import com.indoors.KEY_STATUS
import com.indoors.STATUS_FAILURE
import com.indoors.STATUS_SUCCESS
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

/**
 * @author wupanjie on 2017/12/4.
 */
fun successTemplate(status: Int = STATUS_SUCCESS,
                    message: String? = "success",
                    data: JsonObject? = null
): JsonObject = if (data == null)
  json {
    obj(
        KEY_STATUS to status,
        KEY_MESSAGE to (message ?: "success")
    )
  }
else
  json {
    obj(
        KEY_STATUS to status,
        KEY_MESSAGE to (message ?: "success"),
        KEY_DATA to data
    )
  }


fun failureTemplate(status: Int = STATUS_FAILURE,
                    message: String? = "failure"
): JsonObject = json {
  obj(
      KEY_STATUS to status,
      KEY_MESSAGE to (message ?: "failure")
  )
}