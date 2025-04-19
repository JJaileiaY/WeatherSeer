package com.example.weatherseer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.mockito.Mockito.`when`
import retrofit2.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class WeatherViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: WeatherViewModel
    private lateinit var dispatcher: TestDispatcher
    private lateinit var service: MockService

    private val sampleZip = "55155"
    private val sampleAppId = ""
    private val sampleUnits = "imperial"
    private val sampleErrMessage = "Couldn't find what you were looking for."

    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {}
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)            }
        }
        this.observeForever(observer)

        try {
            afterObserve.invoke()

            // Don't wait indefinitely if the LiveData is not set.
            if (!latch.await(time, timeUnit)) {
                this.removeObserver(observer)
                throw TimeoutException("LiveData value was never set.")
            }

        } finally {
            this.removeObserver(observer)
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        dispatcher = UnconfinedTestDispatcher()
        service = MockService()
        viewModel = WeatherViewModel(service)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getData is successful`() = runTest(dispatcher) {

        val sampleResponse = WeatherMetaData(
            name = "Saint Paul",
            coord = Coord(0.0, 0.0),
            weather = mutableListOf(),
            base = "",
            main = Main(
                0.0,
                feelsLike = 0.0,
                tempMin = 0.0,
                tempMax = 0.0,
                humidity = 0,
                pressure = 0,
                seaLevel = 0,
                grndLevel = 0
            ),
            visibility = 0,
            wind = null,
            rain = null,
            clouds = null,
            dt = 0,
            sys = Sys(
                type = 0,
                id = 0,
                country = "",
                sunrise = 0,
                sunset = 0
            ),
            timezone = 0,
            id = 0,
            cod = 0
        )
        val successResponse = Response.success(sampleResponse)

        service.mockWeatherResponse = successResponse

        viewModel.getData(sampleZip)
        advanceUntilIdle()

        val expectedResult = NetworkResponse.Success(sampleResponse)
        assertEquals(expectedResult, viewModel.weatherResult.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getData fails returns error`() = runTest(dispatcher) {


        //// Doesn't pass with actual message, may not need getOrAwaitValue()
        val errorMessage = ""
        val errorResponse: Response<WeatherMetaData> = Response.error(400,
            errorMessage.toResponseBody()
        )

        service.mockWeatherResponse = errorResponse

        viewModel.getData(sampleZip)
        advanceUntilIdle()


        val expectedResult = NetworkResponse.Error(errorMessage)
        val actualResult = viewModel.weatherResult.getOrAwaitValue()
        assertEquals(expectedResult, actualResult)
        //assertEquals(expectedResult, viewModel.weatherResult.value)
    }

    @Test
    fun `getData exception returns error`() = runTest {

        // make = to an exception for the error
        //service.mockWeatherResponse =

        // When
        viewModel.getData(sampleZip)
        advanceUntilIdle()

        // Then
        val expectedResult = NetworkResponse.Error(sampleErrMessage)
        assertEquals(expectedResult, viewModel.weatherResult.value)
    }

    //////// Create 3 more each ^^ for getData(lat, lon), getForecastData, and
    //////// getForecastData(lat, lon). Total 9 more.


}