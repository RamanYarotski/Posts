package com.homeassignment.posts.data.remote.core

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.append
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import javax.inject.Inject

class ApiClient @Inject constructor() {

    val client = HttpClient(OkHttp) {
        install(HttpTimeout) {
            connectTimeoutMillis = TIMEOUT_MS
            requestTimeoutMillis = TIMEOUT_MS
            socketTimeoutMillis = TIMEOUT_MS
        }
        install(ContentNegotiation) { json(Json { defaultSettings() }) }

        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = Url(BASE_URL).host

//                path(BASE_PATH) Could be used in real project
            }
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json)

//                Any header params like token etc could be passed here
//                append(HttpHeaders.Authorization, "Bearer $accessToken")
            }
        }
    }

    /**
     * Default implementation for making a request and handling the response.
     */
    suspend inline fun <reified S, reified B : Any> makeRequest(
        endpoint: String,
        method: HttpMethod,
        body: B? = null,
        queryParams: Map<String, Any?>? = null
    ): Result<ErrorType, S> {
        try {
            val response = when (method) {
                HttpMethod.Get -> client.get(endpoint) {
                    queryParams?.forEach { param ->
                        if (param.value != null) {
                            (param.value as? List<*>)?.forEach { element ->
                                parameter(
                                    param.key,
                                    element
                                )
                            } ?: run {
                                parameter(
                                    param.key,
                                    param.value
                                )
                            }
                        }
                    }
                }

                HttpMethod.Delete -> client.delete(endpoint) {
                    queryParams?.forEach { param ->
                        if (param.value != null) parameter(
                            param.key,
                            param.value
                        )
                    }
                }

                HttpMethod.Post -> client.post(endpoint) {
                    queryParams?.forEach { param ->
                        if (param.value != null) parameter(
                            param.key,
                            param.value
                        )
                    }
                    body?.let { body -> setBody(body) }
                }

                else -> client.get(endpoint)
            }

            return when (response.status.value) {
                in 200..299 -> {
                    try {
                        Result.Success(response.body())
                    } catch (exception: Exception) {
                        Result.Error(ErrorType.ClientError.DecodingError)
                    }
                }

                else -> {
                    when (response.status.value) {
                        400 -> Result.Error(ErrorType.ClientError.BadRequestError)
                        401 -> Result.Error(ErrorType.ClientError.AuthenticationError)
                        402 -> Result.Error(ErrorType.ClientError.PaymentRequiredError)
                        403 -> Result.Error(ErrorType.ClientError.ForbiddenError)
                        404 -> Result.Error(ErrorType.ClientError.NotFoundError)
                        405 -> Result.Error(ErrorType.ClientError.NotAllowedError)
                        in 406..499 -> Result.Error(ErrorType.ClientError.OtherClientError)
                        else -> Result.Error(ErrorType.ServerError)
                    }
                }
            }
        } catch (exception: SocketTimeoutException) {
            return Result.Error(ErrorType.NetworkError.TimeoutException)
        } catch (exception: IOException) {
            return Result.Error(ErrorType.NetworkError.ConnectionError) // <-- network failures end up here on JVM
        }
    }

    companion object {
        // In real project could be moved into BuildConfig
        private const val BASE_URL = "https://jsonplaceholder.typicode.com/"
        private const val TIMEOUT_MS = 60000L
    }
}

/**
 * Provides default settings for Json encoding/decoding
 * Should be used in configuration for all api calls
 */
@OptIn(ExperimentalSerializationApi::class)
internal fun JsonBuilder.defaultSettings() {
    explicitNulls = false
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    useAlternativeNames = false
}
