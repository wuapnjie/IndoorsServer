package com.indoors

import io.vertx.core.http.HttpHeaders

/**
 * @author wupanjie on 2017/12/4.
 */

// com.indoors.common key
const val KEY_STATUS = "status"
const val KEY_MESSAGE = "message"
const val KEY_DATA = "data"

// status code
const val STATUS_SUCCESS = 0
const val STATUS_FAILURE = 1

val APPLICATION_JSON = HttpHeaders.createOptimized("application/json")
