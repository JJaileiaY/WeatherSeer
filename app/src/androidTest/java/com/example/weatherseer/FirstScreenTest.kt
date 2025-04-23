package com.example.weatherseer

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.lifecycle.MutableLiveData
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class FirstScreenTest {

    // Passing separately, but some data is empty
    // Recheck all files for comments and clean them up before turning in


    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun appTitleIsDisplayed() {
        val mockWeatherService = MockServiceUI()
        val viewModel = WeatherViewModel(mockWeatherService)

        // Initialize LiveData with a default value
        viewModel.weatherResult as MutableLiveData
        (viewModel.weatherResult as MutableLiveData<NetworkResponse<WeatherMetaData>>).postValue(null)

        composeTestRule.setContent {
            FirstScreen(
                viewModel = viewModel,
                onNavigateForecastClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        composeTestRule.onNodeWithText("WeatherSeer").assertIsDisplayed()
    }

    @Test
    fun searchButtonClickedWithZipNavigatesToForecastScreen() {
        val mockWeatherService = MockServiceUI()
        val viewModel = WeatherViewModel(mockWeatherService)

        // Initialize LiveData with a default value
        viewModel.weatherResult as MutableLiveData
        (viewModel.weatherResult as MutableLiveData<NetworkResponse<WeatherMetaData>>).postValue(null)
        var navigated = false

        composeTestRule.setContent {
            FirstScreen(
                viewModel = viewModel,
                onNavigateForecastClicked = { navigated = true },
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        composeTestRule.onNodeWithText("").performTextInput("12345")
        composeTestRule.onNodeWithText("16-Day").performClick()
        Assert.assertEquals(true, navigated)
    }

    @Test
    fun searchButtonClickedWithInvalidZipShowsToast() {
        val mockWeatherService = MockServiceUI()
        val viewModel = WeatherViewModel(mockWeatherService)

        // Initialize LiveData with a default value
        viewModel.weatherResult as MutableLiveData
        (viewModel.weatherResult as MutableLiveData<NetworkResponse<WeatherMetaData>>).postValue(null)

        composeTestRule.setContent {
            FirstScreen(
                viewModel = viewModel,
                onNavigateForecastClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        composeTestRule.onNodeWithText("").performTextInput("1234")
        composeTestRule.onNodeWithText("16-Day").performClick()
    }


    ///// Test if the permissions show up and if rationale shows up?
    ////  Test if hasLocation then when click button reload weather to show current?
    /// Test Notification shows up when hasLocation and hasNotifications?


    @Test
    fun weatherDataSucceededAndDisplayed() {
        val mockWeatherService = MockServiceUI()
        val mockWeatherData = WeatherMetaData(
            coord = Coord(0.0, 0.0),
            weather = listOf(
                Weather(
                    id = 0,
                    main = "Sunny",
                    description = "sunny",
                    icon = ""
                )
            ),
            base = "",
            main = Main(
                temp = 25.0,
                feelsLike = 0.0,
                tempMin = 0.0,
                tempMax = 0.0,
                humidity = 0,
                pressure = 1000
            ),
            visibility = 0,
            wind = null,
            clouds = null,
            dt = 0,
            sys = Sys(
                type = 0,
                id = 0,
                country = "US",
                sunrise = 0,
                sunset = 0
            ),
            timezone = 0,
            id = 0,
            name = "Saint Paul",
            cod = 0
        )
        mockWeatherService.mockWeatherResponse = Response.success(mockWeatherData)
        val viewModel = WeatherViewModel(mockWeatherService)

        // Trigger data loading
        viewModel.getData("12345")

        composeTestRule.setContent {
            FirstScreen(
                viewModel = viewModel,
                onNavigateForecastClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        //composeTestRule.onNodeWithText("Saint Paul,US").assertIsDisplayed()
        composeTestRule.onNodeWithText("25°").assertIsDisplayed()
        composeTestRule.onNodeWithText("Feels like 0°").assertIsDisplayed()
        composeTestRule.onNodeWithText("Low 0°").assertIsDisplayed()
        composeTestRule.onNodeWithText("High 0°").assertIsDisplayed()
        composeTestRule.onNodeWithText("Humidity 0% ").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pressure 1000 hPa").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sunny").assertIsDisplayed()
    }

    @Test
    fun weatherDataFailedErrorMessageDisplayed() {
        val mockWeatherService = MockServiceUI()
        val sampleErr = ""

        // How to make it display a message
        mockWeatherService.mockWeatherResponse = Response.error(400, sampleErr.toResponseBody(null))
        val viewModel = WeatherViewModel(mockWeatherService)

        composeTestRule.setContent {
            FirstScreen(
                viewModel = viewModel,
                onNavigateForecastClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        composeTestRule.onNodeWithText(sampleErr).assertIsDisplayed()

    }
}

/**
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.MutableLiveData
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class FirstScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: WeatherViewModel
    private lateinit var service: MockServiceUI

    private val sampleZip = "55155"
    private val sampleLat = 0.0
    private val sampleLon = 0.0
    private val sampleAppId = ""
    private val sampleUnits = "imperial"
    private val sampleDays = 16
    private val sampleErr = ""


    @Before
    fun setup() {
        service = MockServiceUI()
        viewModel = WeatherViewModel(service)
    }

}
        **/