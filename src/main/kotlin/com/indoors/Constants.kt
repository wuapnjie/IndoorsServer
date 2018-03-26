package com.indoors

import io.vertx.core.http.HttpHeaders

/**
 * @author wupanjie on 2017/12/4.
 */

// qiniu
const val QINIU_ACCESS_KEY = "VVt4kvQGnCzEefCCRPTMDn0ZeXf58fQpLI3rpXrV"
const val QINIU_SECRET_KEY = "tV_E2l1314xCdWzYhJOAzD9Qm9wllfgEzqRB9cIY"
const val QINIU_BUCKET = "indoors"
const val QQINIU_URL_PREFIX = "http://p5vucf7pw.bkt.clouddn.com/"

// com.indoors.common key
const val KEY_STATUS = "status"
const val KEY_MESSAGE = "message"
const val KEY_DATA = "data"

// status code
const val STATUS_SUCCESS = 0
const val STATUS_FAILURE = 1

val APPLICATION_JSON = HttpHeaders.createOptimized("application/json")
