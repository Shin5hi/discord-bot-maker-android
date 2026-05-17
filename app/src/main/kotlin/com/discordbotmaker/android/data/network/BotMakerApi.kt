package com.discordbotmaker.android.data.network

import com.discordbotmaker.android.data.model.AutoModConfigDto
import com.discordbotmaker.android.data.model.BotConfigDto
import com.discordbotmaker.android.data.model.BotLogEntryDto
import com.discordbotmaker.android.data.model.BotRegistrationRequest
import com.discordbotmaker.android.data.model.logsWebSocketUrl
import com.discordbotmaker.android.data.model.normalizeBackendUrl
import java.io.Closeable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class BotMakerApi(
    private val client: OkHttpClient = OkHttpClient(),
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    suspend fun registerBot(baseUrl: String, botName: String, token: String): BotConfigDto =
        request(
            baseUrl = baseUrl,
            path = "/api/bot",
            method = "POST",
            body = BotRegistrationRequest(botName = botName, token = token),
        )

    suspend fun getBot(baseUrl: String): BotConfigDto =
        request(baseUrl = baseUrl, path = "/api/bot", method = "GET")

    suspend fun startBot(baseUrl: String): BotConfigDto =
        request(baseUrl = baseUrl, path = "/api/bot/start", method = "POST")

    suspend fun stopBot(baseUrl: String): BotConfigDto =
        request(baseUrl = baseUrl, path = "/api/bot/stop", method = "POST")

    suspend fun getAutoMod(baseUrl: String): AutoModConfigDto =
        request(baseUrl = baseUrl, path = "/api/automod", method = "GET")

    suspend fun updateAutoMod(baseUrl: String, config: AutoModConfigDto): AutoModConfigDto =
        request(baseUrl = baseUrl, path = "/api/automod", method = "PUT", body = config)

    fun openLogStream(baseUrl: String, callback: LogStreamCallback): Closeable {
        val request = Request.Builder()
            .url(logsWebSocketUrl(baseUrl))
            .build()

        val socket = client.newWebSocket(
            request,
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    callback.onConnected()
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    runCatching { json.decodeFromString(BotLogEntryDto.serializer(), text) }
                        .onSuccess(callback::onLog)
                        .onFailure(callback::onError)
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    webSocket.close(code, reason)
                    callback.onDisconnected()
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    callback.onDisconnected()
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    callback.onError(t)
                }
            },
        )

        return Closeable {
            socket.close(1000, "Client disconnect")
        }
    }

    private suspend inline fun <reified T> request(
        baseUrl: String,
        path: String,
        method: String,
        body: Any? = null,
    ): T = withContext(Dispatchers.IO) {
        val requestBuilder = Request.Builder()
            .url(normalizeBackendUrl(baseUrl) + path)

        if (body == null) {
            requestBuilder.method(method, null)
        } else {
            val payload = when (body) {
                is BotRegistrationRequest -> json.encodeToString(BotRegistrationRequest.serializer(), body)
                is AutoModConfigDto -> json.encodeToString(AutoModConfigDto.serializer(), body)
                else -> error("Unsupported body type: ${body::class.java.simpleName}")
            }
            requestBuilder.method(
                method,
                payload.toRequestBody("application/json; charset=utf-8".toMediaType()),
            )
        }

        client.newCall(requestBuilder.build()).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException(response.body?.string().orEmpty().ifBlank { "Backend request failed." })
            }

            val rawBody = response.body?.string().orEmpty()
            when (T::class) {
                BotConfigDto::class -> json.decodeFromString(BotConfigDto.serializer(), rawBody) as T
                AutoModConfigDto::class -> json.decodeFromString(AutoModConfigDto.serializer(), rawBody) as T
                else -> error("Unsupported response type: ${T::class}")
            }
        }
    }
}

interface LogStreamCallback {
    fun onConnected()
    fun onLog(entry: BotLogEntryDto)
    fun onDisconnected()
    fun onError(error: Throwable)
}
