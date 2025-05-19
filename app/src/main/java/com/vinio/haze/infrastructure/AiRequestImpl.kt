package com.vinio.haze.infrastructure

import android.util.Log
import com.vinio.haze.domain.AiRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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

    override suspend fun getPoiFact(
        name: String,
        description: String,
        lan: Double,
        lon: Double
    ): String {
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

    override fun streamPoiDescription(name: String, lat: Double, lon: Double): Flow<String> = flow {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 180_000
            }
        }

        val request = ChatRequest(
            model = "mistral-7b-instruct-v0.3",
            stream = true,
            messages = listOf(
                ChatMessage(
                    role = "user",
                    content = """Дай краткое описание места "$name широта:$lat, долгота:$lon""""
                )
            )
        )

        Log.d("AiRequest", "request: $request")

        try {
            val jsonRequest = Json.encodeToString(ChatRequest.serializer(), request)
            Log.d("AiRequest", "Serialized request JSON: $jsonRequest")
            val response = client.request("http://10.0.2.2:1234/v1/chat/completions") {
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            Log.d("AiRequest", "Response status: ${response.status}")
            Log.d("AiRequest", "Response headers: ${response.headers}")

            val channel = response.bodyAsChannel()

            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break
                Log.d("AiRequest", "Received line: $line")
                if (line.isBlank()) continue

                if (line.startsWith("data: ")) {
                    val jsonPart = line.removePrefix("data: ").trim()
                    if (jsonPart == "[DONE]") break

                    val element = Json.parseToJsonElement(jsonPart).jsonObject
                    val deltaContent = element["choices"]
                        ?.jsonArray?.get(0)
                        ?.jsonObject?.get("delta")
                        ?.jsonObject?.get("content")
                        ?.jsonPrimitive
                        ?.contentOrNull

                    if (!deltaContent.isNullOrEmpty()) {
                        Log.d("AiRequest", "Emitting token: $deltaContent")
                        emit(deltaContent) // emit token to flow
                    }
                }
            }
        } catch (e: Exception) {
            emit("\n[Ошибка: ${e.localizedMessage}]")
        } finally {
            client.close()
        }
    }
}

@Serializable
data class ChatRequest(
    val model: String,
    val stream: Boolean,
    val messages: List<ChatMessage>
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)