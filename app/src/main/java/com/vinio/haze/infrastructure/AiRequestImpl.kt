package com.vinio.haze.infrastructure


import com.vinio.haze.domain.AiRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.plugins.sse.sse
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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
            install(SSE)
        }

        val jsonBody = Json.encodeToString(
            ChatRequest.serializer(), ChatRequest(
                model = "mistral-7b-instruct-v0.3",
                stream = true,
                messages = listOf(
                    ChatMessage(
                        role = "user",
                        content = "Дай краткое описание места \"$name\" (широта: $lat, долгота: $lon)"
                    )
                )
            )
        )

        client.sse(
            urlString = "http://10.0.2.2:1234/v1/chat/completions",
            request = {
                method = HttpMethod.Post
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    append("Content-Type", "application/json")
                    append("Accept", "text/event-stream")
                }
//                setBody(jsonBody)
                setBody(TextContent(jsonBody, ContentType.Application.Json))

            }
        ) {
            incoming.collect { event ->
                if (event.data == "[DONE]") return@collect
                try {
                    val json =
                        event.data?.let { Json.parseToJsonElement(it).jsonObject }
                    val delta = json?.get("choices")
                        ?.jsonArray?.get(0)
                        ?.jsonObject?.get("delta")
                        ?.jsonObject?.get("content")
                        ?.jsonPrimitive
                        ?.contentOrNull

                    if (!delta.isNullOrEmpty()) {
                        emit(delta)
                    }
                } catch (e: Exception) {
                    emit("[Ошибка парсинга: ${e.localizedMessage}]")
                }
            }
        }
    }.catch { e ->
        emit("[Ошибка: ${e.localizedMessage}]")
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
