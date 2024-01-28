package jinproject.stepwalk.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import jinproject.stepwalk.data.remote.api.StepMateApi
import jinproject.stepwalk.data.remote.dto.response.Response
import jinproject.stepwalk.data.remote.dto.response.Token
import jinproject.stepwalk.data.remote.dto.response.TokenResponse
import jinproject.stepwalk.data.repositoryimpl.AuthRepositoryImpl
import jinproject.stepwalk.domain.model.UserData
import jinproject.stepwalk.domain.model.onException
import jinproject.stepwalk.domain.model.onSuccess
import jinproject.stepwalk.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


@OptIn(ExperimentalCoroutinesApi::class)
internal abstract class AuthMockWebServerTest : FunSpec({
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    Dispatchers.setMain(testDispatcher)
}) {
    lateinit var server : MockWebServer
    private lateinit var service : StepMateApi
    lateinit var repository : AuthRepository

    override suspend fun beforeSpec(spec: Spec) {
        server = MockWebServer()
        server.start()

        val baseUrl = server.url("/")
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(client)
            .build()

        service = retrofit.create(StepMateApi::class.java)
        repository = AuthRepositoryImpl(
            service,
            UserDataSourceTest(),
            CurrentAuthDataSourceTest()
        )
    }

    override suspend fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        server.shutdown()
    }
}

internal class CheckDuplicationIdTest : AuthMockWebServerTest(){
    init {
        test("로그인 테스트"){
            val response = MockResponse().addResponse(404,Response(code = 200, message = ""))
            val response2 = MockResponse().addResponse(200,Response(code = 200, message = ""))

            server.enqueue(response)
            server.enqueue(response2)

            val responseTest = repository.checkDuplicationId("testId")
            responseTest.onSuccess {
                it shouldBe true
            }.onException { code, message ->
                code shouldBe 404
            }

            val responseTest2 = repository.checkDuplicationId("testId")
            responseTest2.onSuccess {
                it shouldBe true
            }.onException { code, message ->
                code shouldBe 404
            }

        }

    }
}

internal class SignUpAccountTest : AuthMockWebServerTest(){
    init {
        test("회원가입 테스트"){
            val response = MockResponse().addResponse(200,TokenResponse(code = 200, message = "", result = Token("1231424")))
            server.enqueue(response)

            val responseTest = repository.signUpAccount(UserData())
            runTest {
                responseTest.onEach {state ->
                    state.onSuccess {
                        it shouldBe true
                    }.onException { code, message ->
                        code shouldBe 404
                    }
                }.collect()
            }
        }
    }
}

fun MockResponse.addResponse(code : Int, res: Any) =
    this.addHeader("Content-Type", "application/json; charset=utf-8")
        .setResponseCode(code)
        .setBody(Gson().toJson(res))