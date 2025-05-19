package com.vinio.haze.domain
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.jsonObject

suspend fun getAIResponse(lat: Double, lon: Double, name: String): String {
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
        stream = false, // отключаем стриминг
        messages = listOf(
            ChatMessage(
                role = "user",
                content = """Дай краткое описание места "$name широта:$lat, долгота:$lon""""
            )
        )
    )

    try {
        val response: HttpResponse = client.post("http://10.0.2.2:1234/v1/chat/completions") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        // Считаем тело полностью как текст
        val bodyText = response.bodyAsText()

        // Парсим JSON, чтобы достать сгенерированный ответ
        val json = Json.parseToJsonElement(bodyText).jsonObject
        val content = json["choices"]
            ?.jsonArray
            ?.get(0)
            ?.jsonObject
            ?.get("message")
            ?.jsonObject
            ?.get("content")
            ?.jsonPrimitive
            ?.content

        println("AI response: $content")
        return content ?: "Пустой ответ от модели"
    } finally {
        client.close()
    }
}



@Serializable
data class ChatMessage(val role: String, val content: String)

@Serializable
data class ChatRequest(
    val model: String = "gpt-3.5-turbo", // или твой LM Studio model
    val stream: Boolean = true,
    val messages: List<ChatMessage>
)

@Serializable
data class ChatChoice(
    val message: ChatMessage
)

@Serializable
data class ChatResponse(
    val choices: List<ChatChoice>
)