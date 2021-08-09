package me.jameshunt.fifotax

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

internal class ClientFactory {
    private val okhttp = OkHttpClient
        .Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    private val moshi = Moshi
        .Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://min-api.cryptocompare.com/data/v2/")
        .client(okhttp)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val client: CryptoCompareApi = retrofit.create(CryptoCompareApi::class.java)
}

interface CryptoCompareApi {
    @GET("histohour")
    fun getUSDPriceForCurrencyAtTime(
        @Query("fsym") currency: String,
        @Query("toTs") secondsUnix: Long,
        @Query("tsym") usd: String = "USD",
        @Query("limit") limit: Int = 1
    ): Call<Response>

    data class Response(
        val Data: Data
    )

    data class Data(
        val Data: List<Price>
    )

    data class Price(
        val open: Double,
        val close: Double
    )
}