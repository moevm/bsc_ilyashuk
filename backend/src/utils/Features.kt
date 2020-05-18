package org.moevm.bsc_ilyashuk.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.moevm.bsc_ilyashuk.models.Features
import java.io.*

fun getFeaturesFromFile(filename: String): Features {
    val p = Runtime.getRuntime().exec("python3 extract_features.py $filename")
    val reader = BufferedReader(InputStreamReader(p.inputStream))
    val json = reader.readLine()
    val mapper = jacksonObjectMapper()
    return mapper.readValue(json)
}