package com.indoors

import com.indoors.common.Result
import com.indoors.common.failureTemplate
import com.indoors.common.successTemplate
import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.awaitEvent
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.launch

/**
 * @author wupanjie on 2017/12/4.
 */

fun AbstractVerticle.vertxCoroutine(task: suspend () -> Unit) = launch(vertx.dispatcher()) {
  task()
}

suspend fun <T : Any> resultWith(block: (h: Handler<AsyncResult<T>>) -> Unit):
    Result<T, Exception> {
  val asyncResult = awaitEvent(block)
  return if (asyncResult.succeeded())
    Result.of { asyncResult.result() }
  else
    Result.of { throw asyncResult.cause() }
}

fun HttpServerRequest.getParamOrElse(paramName: String, orElse: String): String {
  val result = getParam(paramName)
  return if (result.isEmpty()) orElse else result
}

fun HttpServerResponse.failWith(cause: Throwable) {
  failWith(cause.message)
}

fun HttpServerResponse.failWith(message: String?) {
  this.putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
      .end(failureTemplate(message = message).encode())
}

fun HttpServerResponse.successWith(message: String? = "success", data: JsonObject? = null) {
  this.putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
      .end(successTemplate(message = message, data = data).encode())
}