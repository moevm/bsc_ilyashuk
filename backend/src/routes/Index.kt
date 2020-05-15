package org.moevm.bsc_ilyashuk.routes

import io.ktor.application.call
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.Route
import org.moevm.bsc_ilyashuk.Index

fun Route.index() {
    get<Index> {
        call.respond("Bcs Ilyashuk Danil 6303")
    }
}