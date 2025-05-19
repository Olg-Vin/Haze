package com.vinio.haze.infrastructure

import com.vinio.haze.domain.AiRequest
import com.vinio.haze.domain.ChatMessage
import com.vinio.haze.domain.ChatRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.jsonArray
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRequestImpl @Inject constructor() : AiRequest {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 180_000
        }
    }

    override suspend fun getPoiDescription(name: String, lan: Double, lon: Double): String {
        return sendRequest(
            prompt = """Дай краткое описание места "$name широта:$lan, долгота:$lon""""
        )
    }

    override suspend fun getPoiFact(name: String, description: String, lan: Double, lon: Double): String {
        return sendRequest(
            prompt = """Приведи интересный факт о месте "$name". Вот описание: $description"""
        )
    }

    private suspend fun sendRequest(prompt: String): String {
        val request = ChatRequest(
            model = "mistral-7b-instruct-v0.3",
            stream = false,
            messages = listOf(ChatMessage(role = "user", content = prompt))
        )

        return try {
            val response = client.post("http://10.0.2.2:1234/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val bodyText = response.bodyAsText()
            val json = Json.parseToJsonElement(bodyText).jsonObject
            json["choices"]
                ?.jsonArray
                ?.get(0)
                ?.jsonObject
                ?.get("message")
                ?.jsonObject
                ?.get("content")
                ?.jsonPrimitive
                ?.content ?: "Пустой ответ от модели"
        } catch (e: Exception) {
            "Ошибка при запросе к модели: ${e.message}"
        }
    }
}
