@file:OptIn(KtorExperimentalLocationsAPI::class)

package org.moevm.bsc_ilyashuk

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.routing.routing
import org.tensorflow.SavedModelBundle
import io.ktor.locations.*
import org.moevm.bsc_ilyashuk.routes.index
import org.moevm.bsc_ilyashuk.routes.predict


@Location("/")
class Index

@Location("/predict")
class Predict

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CORS) {
        anyHost()
    }
    install(ContentNegotiation) {
        gson {}
    }
    install(Locations)

    val model = SavedModelBundle.load("saved_model", "serve")

    routing {
        index()
        predict(model)
    }
}

