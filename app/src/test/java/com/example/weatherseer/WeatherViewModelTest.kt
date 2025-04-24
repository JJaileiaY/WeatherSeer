package com.example.weatherseer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import retrofit2.Response

class WeatherViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: WeatherViewModel
    private lateinit var dispatcher: TestDispatcher
    //private lateinit var service: MockService

    private val sampleZip = "55155"
    private val sampleLat = 0.0
    private val sampleLon = 0.0
    private val sampleAppId = ""
    private val sampleUnits = "imperial"
    private val sampleDays = 16
    private val sampleErr = ""

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
        //service = MockService()
        //viewModel = WeatherViewModel(service)
        }


    // Fix variables ^^, and error message, and getQueryInfo()
    // Unit Tests for getData(zip)



    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getData try is successful`() = runTest {

        val service = MockService()
        viewModel = WeatherViewModel(service)

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
    fun `getData try fails returns error`() = runTest {

        val service = MockService()
        viewModel = WeatherViewModel(service)

        val errorResponse: Response<WeatherMetaData> = Response.error(400,
            sampleErr.toResponseBody()
        )

        service.mockWeatherResponse = errorResponse
        viewModel.getData(sampleZip)
        advanceUntilIdle()

        val expectedResult = NetworkResponse.Error(sampleErr)
        assertEquals(expectedResult, viewModel.weatherResult.value)
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getData exception returns error`() = runTest {

        val mockService: WeatherService = mock()
        val service = MockService()
        viewModel = WeatherViewModel(service)

        doAnswer { throw RuntimeException(sampleErr) }
            .`when`(mockService).getWeather(sampleZip, sampleAppId, sampleUnits)

        viewModel.getData(sampleZip)
        advanceUntilIdle()

        val expectedResult = NetworkResponse.Error(sampleErr)
        assertEquals(expectedResult, viewModel.weatherResult.value)
    }



    // Unit Tests for getData(lat, lon)



    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getData(lat, lon) try is successful`() = runTest {

        val service = MockService()
        viewModel = WeatherViewModel(service)

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
        viewModel.getData(sampleLat, sampleLon)
        advanceUntilIdle()

        val expectedResult = NetworkResponse.Success(sampleResponse)
        assertEquals(expectedResult, viewModel.weatherResult.value)
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getData(lat, lon) try fails returns error`() = runTest {

        val service = MockService()
        viewModel = WeatherViewModel(service)

        val errorResponse: Response<WeatherMetaData> = Response.error(400,
            sampleErr.toResponseBody()
        )

        service.mockWeatherResponse = errorResponse
        viewModel.getData(sampleLat, sampleLon)
        advanceUntilIdle()

        val expectedResult = NetworkResponse.Error(sampleErr)
        assertEquals(expectedResult, viewModel.weatherResult.value)
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getData(lat, lon) exception returns error`() = runTest {

        val mockService: WeatherService = mock()
        val service = MockService()
        viewModel = WeatherViewModel(service)

        doAnswer { throw RuntimeException(sampleErr) }
            .`when`(mockService).getWeatherLL(sampleLat, sampleLon, sampleAppId, sampleUnits)

        viewModel.getData(sampleLat, sampleLon)
        advanceUntilIdle()

        val expectedResult = NetworkResponse.Error(sampleErr)
        assertEquals(expectedResult, viewModel.weatherResult.value)
    }



    // Unit Tests for getForecastData(zip)



    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getForecastData try is successful`() = runTest {

        val service = MockService()
        viewModel = WeatherViewModel(service)

        val sampleResponse = ForecastMetaData(
            city = City(
                id = 0,
                name = "Saint Paul",
                coord = Coord(0.0, 0.0),
                country = "",
                population = 0,
                timezone = 0),
            cod = "0",
            message = 0.0,
            cnt = 0,
            list = arrayListOf()
        )
        val successResponse = Response.success(sampleResponse)

        service.mockForecastResponse = successResponse
        viewModel.getForecastData(sampleZip)
        advanceUntilIdle()

        val expectedResult = NetworkResponse.Success(sampleResponse)
        assertEquals(expectedResult, viewModel.forecastResult.value)
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getForecastData try fails returns error`() = runTest {

        val service = MockService()
        viewModel = WeatherViewModel(service)

        val errorResponse: Response<ForecastMetaData> = Response.error(400,
            sampleErr.toResponseBody()
        )

        service.mockForecastResponse = errorResponse
        viewModel.getForecastData(sampleZip)
        advanceUntilIdle()

        val expectedResult = NetworkResponse.Error(sampleErr)
        assertEquals(expectedResult, viewModel.forecastResult.value)
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getForecastData exception returns error`() = runTest {

        val mockService: WeatherService = mock()
        val service = MockService()
        viewModel = WeatherViewModel(service)

        doAnswer { throw RuntimeException(sampleErr) }
            .`when`(mockService).getForecast(sampleZip, sampleAppId, sampleDays, sampleUnits)

        viewModel.getForecastData(sampleZip)
        advanceUntilIdle()

        val expectedResult = NetworkResponse.Error(sampleErr)
        assertEquals(expectedResult, viewModel.forecastResult.value)
    }



    // Unit Tests for getForecastData(lat, lon)



    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getForecast(lat, lon) try is successful`() = runTest {

        val service = MockService()
        viewModel = WeatherViewModel(service)

        val sampleResponse = ForecastMetaData(
            city = City(
                id = 0,
                name = "Saint Paul",
                coord = Coord(0.0, 0.0),
                country = "",
                population = 0,
                timezone = 0),
            cod = "0",
            message = 0.0,
            cnt = 0,
            list = arrayListOf()
        )
        val successResponse = Response.success(sampleResponse)

        service.mockForecastResponse = successResponse
        viewModel.getForecastData(sampleLat, sampleLon)
        advanceUntilIdle()

        val expectedResult = NetworkResponse.Success(sampleResponse)
        assertEquals(expectedResult, viewModel.forecastResult.value)
    }




    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getForecast(lat, lon) try fails returns error`() = runTest {

        val service = MockService()
        viewModel = WeatherViewModel(service)

        val errorResponse: Response<ForecastMetaData> = Response.error(400,
            sampleErr.toResponseBody()
        )

        service.mockForecastResponse = errorResponse
        viewModel.getForecastData(sampleLat, sampleLon)
        advanceUntilIdle()

        val expectedResult = NetworkResponse.Error(sampleErr)
        assertEquals(expectedResult, viewModel.forecastResult.value)
    }




    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getForecast(lat, lon) exception returns error`() = runTest {

        val mockService: WeatherService = mock()
        val service = MockService()
        viewModel = WeatherViewModel(service)

        doAnswer { throw RuntimeException(sampleErr) }
            .`when`(mockService).getForecastLL(sampleLat, sampleLon, sampleAppId, sampleDays, sampleUnits)

        viewModel.getForecastData(sampleLat, sampleLon)
        advanceUntilIdle()

        val expectedResult = NetworkResponse.Error(sampleErr)
        assertEquals(expectedResult, viewModel.forecastResult.value)
    }

}