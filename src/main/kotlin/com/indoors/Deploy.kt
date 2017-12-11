package com.indoors

/**
 * @author wupanjie on 2017/11/27.
 */
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import com.indoors.verticle.RESTVerticle

fun main(args: Array<String>) {

  System.setProperty("vertx.disableDnsResolver","true")
  System.setProperty("vertx.logger-delegate-factory-class-name",
      "io.vertx.core.logging.SLF4JLogDelegateFactory")

  val vertx:Vertx = Vertx.vertx()

  vertx.deployVerticle(RESTVerticle::class.java.name, DeploymentOptions().setInstances(1))

}