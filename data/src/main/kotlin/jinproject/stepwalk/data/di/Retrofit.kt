package jinproject.stepwalk.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jinproject.stepwalk.data.BuildConfig
import jinproject.stepwalk.data.local.datasource.CurrentAuthDataSource
import jinproject.stepwalk.data.remote.api.RankBoardApi
import jinproject.stepwalk.data.remote.api.UserApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.io.IOException
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RetrofitWithTokenModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitWithInterceptor

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OkHttpClientWithInterceptor

    @Singleton
    @Provides
    fun provideHeaderInterceptor(authDataSource: CurrentAuthDataSource): HeaderInterceptor =
        HeaderInterceptor(authDataSource)

    @Singleton
    @Provides
    fun provideNullOnEmptyConverterFactory(): NullOnEmptyConverterFactory =
        NullOnEmptyConverterFactory()

    @OkHttpClientWithInterceptor
    @Singleton
    @Provides
    fun provideOkHttpClient(
        headerInterceptor: HeaderInterceptor,

        ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(headerInterceptor)
            .build()
    }

    @RetrofitWithInterceptor
    @Singleton
    @Provides
    fun provideRetrofitWithInterceptor(
        @OkHttpClientWithInterceptor okHttpClient: OkHttpClient,
        nullOnEmptyConverterFactory: NullOnEmptyConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(nullOnEmptyConverterFactory)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .baseUrl(BuildConfig.SERVER_IP)
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideRankBoardApi(@RetrofitWithInterceptor retrofit: Retrofit): RankBoardApi =
        retrofit.create()

    @Singleton
    @Provides
    fun provideUserApi(@RetrofitWithInterceptor retrofit: Retrofit): UserApi =
        retrofit.create()

}

class HeaderInterceptor @Inject constructor(private val authDataSource: CurrentAuthDataSource) :
    Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking {
            authDataSource.getAccessToken().firstOrNull()
                ?: throw IllegalArgumentException("엑세스 토큰이 없습니다.")
        }

        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", accessToken)
            .build()

        val response = chain.proceed(newRequest)

        if (!response.isSuccessful && response.code == 402) { // AccessToken 의 만료인 경우 402

            val refreshToken = runBlocking {
                authDataSource.getRefreshToken().firstOrNull()
                    ?: throw IllegalArgumentException("리프레시 토큰이 없습니다.")
            }

            val reissueRequest = Request.Builder()
                .url("${BuildConfig.SERVER_IP}reissue")
                .addHeader("Authorization", refreshToken)
                .build()

            val reissueResponse = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build().newCall(reissueRequest).execute()

            if (reissueResponse.isSuccessful) {
                val gson = Gson()
                val refreshResponseJson =
                    gson.fromJson(reissueResponse.body?.string(), Map::class.java)
                val result = refreshResponseJson["result"] as Map<*, *>
                val newAccessToken = result["accessToken"] as String

                runBlocking {
                    authDataSource.setAccessToken(newAccessToken)
                }

                val request = newRequest.newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", newAccessToken)
                    .build()

                response.close()
                return chain.proceed(request)
            }
        }

        return response
    }
}

class NullOnEmptyConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): Converter<ResponseBody, *> {
        val delegate: Converter<ResponseBody, *> =
            retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
        return Converter { body -> if (body.contentLength() == 0L) "" else delegate.convert(body) }
    }
}