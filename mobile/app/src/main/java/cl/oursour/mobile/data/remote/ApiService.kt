package cl.oursour.mobile.data.remote

import retrofit2.http.GET

data class PingResponse(
    val status: String,
    val timestamp: String,
)

interface ApiService {
    @GET("api/ping")
    suspend fun ping(): PingResponse
}
