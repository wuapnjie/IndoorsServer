package com.indoors.verticle

/**
 * @author wupanjie on 2017/11/27.
 */
import com.indoors.*
import com.indoors.common.Result
import com.indoors.common.computePosition
import com.qiniu.util.Auth
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.mongo.MongoClientUpdateResult
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.core.json.JsonArray
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.kotlin.ext.mongo.FindOptions
import jkid.deserialization.deserialize
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.suspendCoroutine

class RESTVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(this::class.java)
  private lateinit var mongoClient: MongoClient

  override fun start() {
    // config sql client
    mongoClient = MongoClient.createShared(vertx, mongoConfig)

    // config router
    val router = Router.router(vertx)

    router.route().handler(BodyHandler.create())

    router.post("/wifi/upload").handler { uploadWifiData(it) }
    router.get("/room/info").handler { fetchRoomInfo(it) }
    router.get("/room/list").handler { fetchRoomList(it) }
    router.post("/room/location").handler { fetchRoomPosition(it) }
    router.post("/room/new").handler { createNewRoom(it) }
    router.post("/room/delete").handler { deleteRoom(it) }
    router.post("/room/positions/clear").handler { clearRoomPosition(it) }
    router.get("/upload/token").handler { createUploadToken(it) }

    // create server
    vertx.createHttpServer().requestHandler({ router.accept(it) }).listen(8080)
  }

  private fun createUploadToken(routingContext: RoutingContext) {
    val response = routingContext.response()
    val auth = Auth.create(QINIU_ACCESS_KEY, QINIU_SECRET_KEY)

    response.successWith(data = json {
      obj("token" to auth.uploadToken(QINIU_BUCKET))
    })
  }

  private fun clearRoomPosition(routingContext: RoutingContext) {
    val request = routingContext.request()
    val response = routingContext.response()
    val roomId = request.getParam("room_id")

    if (roomId.isNullOrEmpty()) {
      response.failWith("need param room_id")
      return
    }

    vertxCoroutine {
      val result = resultWith<MongoClientUpdateResult> { handler ->
        val query = json {
          obj(
              "_id" to roomId
          )
        }

        val update = json {
          obj(
              "\$set" to obj(
                  "positions" to JsonArray()
              )
          )
        }

        mongoClient.updateCollection("room", query, update, handler)
      }

      result.fold(success = { mongoClientUpdateResult ->
        var message = "success"
        if (mongoClientUpdateResult.docMatched == 0L) {
          message = "No room found with $roomId"
        }
        response.successWith(message = message)
      }, failure = { response.failWith(it) })
    }
  }


  private fun deleteRoom(routingContext: RoutingContext) {
    val request = routingContext.request()
    val response = routingContext.response()
    val roomId = request.getParam("room_id")

    if (roomId.isNullOrEmpty()) {
      response.failWith("need param room_id")
      return
    }

    vertxCoroutine {
      val result = resultWith<JsonObject> { handler ->
        val query = json {
          obj("_id" to roomId)
        }
        mongoClient.findOneAndDelete("room", query, handler)
      }

      result.fold(success = { response.successWith() }, failure = { response.failWith(it) })
    }
  }

  // TODO 关于同名
  private fun createNewRoom(routingContext: RoutingContext) {
    val response = routingContext.response()

    val bodyAsJson = routingContext.bodyAsJson;

    val name = bodyAsJson.getString("room_name")
    val roomWidth = bodyAsJson.getDouble("width", -1.0)
    val roomHeight = bodyAsJson.getDouble("height", -1.0)
    val roomUrl = bodyAsJson.getString("image_url")

    if (roomWidth == null || roomHeight == null) {
      response.failWith("room's width or height must e a number")
      return
    }

    if (roomWidth <= 0 || roomHeight <= 0) {
      response.failWith("room's width or height must be greater than 0")
      return
    }

    if (roomUrl.isNullOrEmpty()) {
      response.failWith("need param image_url")
      return
    }

    if (name.isNullOrEmpty()) {
      response.failWith("need param name")
      return
    }

    vertxCoroutine {
      val result = resultWith<String> { handler ->
        val document = json {
          obj("room_name" to name,
              "width" to roomWidth,
              "height" to roomHeight,
              "image_url" to QQINIU_URL_PREFIX + roomUrl,
              "positions" to array())
        }
        mongoClient.insert("room", document, handler)
      }

      result.fold(success = { id ->
        val room = json {
          obj("_id" to id,
              "room_name" to name,
              "width" to roomWidth,
              "height" to roomHeight,
              "image_url" to QQINIU_URL_PREFIX + roomUrl)
        }
        val data = json {
          obj("room" to room)
        }
        response.successWith(data = data)
      }, failure = { response.failWith(it) })
    }
  }

  private fun fetchRoomPosition(routingContext: RoutingContext) {
    val request = routingContext.request()
    val response = routingContext.response()
    val roomId = request.getParam("room_id")

    if (roomId.isNullOrEmpty()) {
      response.failWith("need param room_id")
      return
    }

    vertxCoroutine {
      val result: Result<Position, Exception> = computePosition(roomId, routingContext)
      result.fold(success = { position ->
        logger.debug(position)

        val data = json {
          obj(
              "x" to position.x,
              "y" to position.y
          )
        }

        response.successWith(data = data)
      }, failure = { response.failWith(it) })
    }

  }

  private suspend fun computePosition(roomId: String, routingContext: RoutingContext): Result<Position, Exception> {
    findRoom(roomId).await()
        .fold(success = { roomInfo ->

          return try {
            val needCompute = deserialize<NeedComputePosition>(routingContext.bodyAsString)
            val room = deserialize<Room>(roomInfo.toString())

            Result.of { room.computePosition(needCompute.fingerprint) }
          } catch (e: Exception) {
            logger.error(e)
            Result.error(e)
          }

        }, failure = { error ->
          return Result.error(error)
        })
  }

  private suspend fun findRoom(roomId: String, fields: JsonObject? = null) = async {
    val query = json {
      obj("_id" to roomId)
    }

    val result = resultWith<JsonObject> { handler ->
      mongoClient.findOne("room", query, fields, handler)
    }

    return@async result
  }

  private fun fetchRoomInfo(routingContext: RoutingContext) {
    val request = routingContext.request()
    val response = routingContext.response()
    val roomId = request.getParam("room_id")

    if (roomId.isNullOrEmpty()) {
      response.failWith("need param room_id")
      return
    }

    vertxCoroutine {

      findRoom(roomId, json {
        obj(
            "positions.wifi_stats" to 0
        )
      }).await()
          .fold(success = { room ->
            val data = json {
              obj("room" to room)
            }
            response.successWith(data = data)
          }, failure = { response.failWith(it) })

    }
  }

  private fun uploadWifiData(routingContext: RoutingContext) {
    val request = routingContext.request()
    val response = routingContext.response()
    val roomId = request.getParam("room_id")
    if (roomId.isNullOrEmpty()) {
      response.failWith("need param room_id")
      return
    }

    vertxCoroutine {
      updateOrPushPositions(roomId, routingContext.bodyAsJson)
    }.invokeOnCompletion { cause ->
      if (cause == null) {
        response.successWith()
      } else {
        response.failWith(cause = cause)
      }
    }

  }

  private suspend fun updateOrPushPositions(roomId: String, roomPosition: JsonObject):
      Boolean = suspendCoroutine { cont ->
    launch(vertx.dispatcher()) {
      val wifi_stat = roomPosition.getJsonArray("wifi_stats")

      val x = roomPosition.getDouble("x", 0.0)
      val y = roomPosition.getDouble("y", 0.0)

      val querySet = json {
        obj(
            "_id" to roomId,
            "positions" to obj(
                "\$elemMatch" to obj(
                    "x" to x,
                    "y" to y
                )
            )
        )
      }

      logger.debug(querySet)

      val updateSet = json {
        obj(
            "\$push" to obj(
                "positions.$.wifi_stats" to obj(
                    "wifi_stat" to wifi_stat,
                    "upload_time" to System.currentTimeMillis()
                )
            )
        )
      }

      resultWith<MongoClientUpdateResult> { handler ->
        mongoClient.updateCollection("room", querySet, updateSet, handler)
      }.fold(
          success = { mongoClientUpdateResult ->
            logger.debug(mongoClientUpdateResult.toJson())

            if (mongoClientUpdateResult.docMatched > 0L) {
              cont.resume(true)
              return@launch
            }

            logger.debug("push item")
            // if not update, push it
            val queryPush = json {
              obj("_id" to roomId)
            }

            val updatePush = json {
              obj(
                  "\$push" to obj(
                      "positions" to obj(
                          "x" to x,
                          "y" to y,
                          "wifi_stats" to array(obj(
                              "wifi_stat" to wifi_stat,
                              "upload_time" to System.currentTimeMillis()
                          ))
                      )
                  )
              )
            }

            resultWith<MongoClientUpdateResult> { handler ->
              mongoClient.updateCollection("room", queryPush, updatePush, handler)
            }.fold(
                success = { cont.resume(false) },
                failure = cont::resumeWithException
            )
          },
          failure = cont::resumeWithException
      )
    }

  }

  private fun fetchRoomList(routingContext: RoutingContext) {
    val request = routingContext.request()
    val response = routingContext.response()

    vertxCoroutine {
      val result = resultWith<List<JsonObject>> { handler ->
        var limit = request.getParamOrElse("limit", "10").toInt()
        val offset = request.getParamOrElse("offset", "0").toInt()

        // avoid query all data
        if (limit <= 0) limit = 10

        val options = FindOptions(limit = limit, skip = offset, fields = json {
          obj("positions.wifi_stats" to 0,
              "positions.x" to 0,
              "positions.y" to 0)
        })
        mongoClient.findWithOptions("room", JsonObject(), options, handler)
      }

      result.fold(success = { list ->

        val roomInfos = arrayListOf<JsonObject>()
        list.forEach { item ->
          val positions = item.getJsonArray("positions")
          item.remove("positions")
          item.put("positions_count", positions?.size() ?: 0)
        }

        val data = json {
          obj("rooms" to list)
        }

        response.successWith(data = data)
      }, failure = { response.failWith(it) })

    }
  }
}