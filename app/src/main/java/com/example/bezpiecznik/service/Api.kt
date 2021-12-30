package com.example.bezpiecznik.service

import com.example.bezpiecznik.data.AppDatabase
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*

abstract class Api {
    protected val client = HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }
    protected val database = AppDatabase.getConnection()

    protected fun getUrl(pathname: String): String {
        return "http://10.0.2.2:3000/${pathname}"
    }
}