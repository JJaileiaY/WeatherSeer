package com.example.weatherseer

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class FirstScreenTest {

    // Recheck all files for comments and clean them up before turning in
    // Test notifications


    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var mockService: MockServiceUI
    private lateinit var viewModel: WeatherViewModel
    private lateinit var mockWeatherData: WeatherMetaData
    private lateinit var sampleZip: String
    private lateinit var uiDevice: UiDevice

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        mockService = MockServiceUI()
        viewModel = WeatherViewModel(mockService)
        mockWeatherData = WeatherMetaData(
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
        sampleZip = "12345"
        uiDevice = UiDevice.getInstance(getInstrumentation())
    }



    @Test
    fun appTitleIsDisplayed() {

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
    fun textFieldSearchButtonLocationButtonDisplayed() {

        composeTestRule.setContent {
            FirstScreen(
                viewModel = viewModel,
                onNavigateForecastClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        composeTestRule.onNodeWithTag("textField").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Location").assertIsDisplayed()
        composeTestRule.onNodeWithText("16-Day").assertIsDisplayed()
    }



    // Text Field and Search Button Tests


    @Test
    fun textFieldExceedsFiveDisplaysToast() {

        composeTestRule.setContent {
            FirstScreen(
                viewModel = viewModel,
                onNavigateForecastClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        composeTestRule.onNodeWithText("").performTextInput("123456")
    }



    @Test
    fun searchButtonClickedWithValidZipNavigatesToForecastScreen() {

        var navigateForecastClicked = false

        composeTestRule.setContent {
            FirstScreen(
                viewModel = viewModel,
                onNavigateForecastClicked = { navigateForecastClicked = true },
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        composeTestRule.onNodeWithText("").performTextInput("12345")
        composeTestRule.onNodeWithText("16-Day").performClick()
        Assert.assertEquals(true, navigateForecastClicked)
    }



    @Test
    fun searchButtonClickedWithInvalidZipDisplaysToastAndNotNavigate() {

        var navigateForecastClicked = false

        composeTestRule.setContent {
            FirstScreen(
                viewModel = viewModel,
                onNavigateForecastClicked = { navigateForecastClicked = true },
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        composeTestRule.onNodeWithText("").performTextInput("1234")
        composeTestRule.onNodeWithText("16-Day").performClick()
        Assert.assertEquals(false, navigateForecastClicked)
    }



    // Notification Tests



    //



    // Tests for Displaying weather Data



    @Test
    fun hasLocationWeatherDataSucceededAndDisplayed() {

        mockService.mockWeatherResponse = Response.success(mockWeatherData)
        viewModel.getData(0.0, 0.0)

        composeTestRule.setContent {
            FirstScreen(
                viewModel = viewModel,
                onNavigateForecastClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        //composeTestRule.onNodeWithText(" Saint Paul, US").assertIsDisplayed()
        composeTestRule.onNodeWithText("25°").assertIsDisplayed()
        composeTestRule.onNodeWithText("Feels like 0°").assertIsDisplayed()
        composeTestRule.onNodeWithText("Low 0°").assertIsDisplayed()
        composeTestRule.onNodeWithText("High 0°").assertIsDisplayed()
        composeTestRule.onNodeWithText("Humidity 0% ").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pressure 1000 hPa").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sunny").assertIsDisplayed()
    }



    @Test
    fun hasLocationWeatherDataFailedErrorMessageDisplayed() {

        mockService.mockWeatherResponse = Response.error(400, "".toResponseBody("text/plain".toMediaTypeOrNull()))
        viewModel.getData(0.0, 0.0)

        composeTestRule.setContent {
            FirstScreen(
                viewModel = viewModel,
                onNavigateForecastClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.errMessage)).assertIsDisplayed()
    }



    @Test
    fun noLocationWeatherDataSucceededAndDisplayed() {

        mockService.mockWeatherResponse = Response.success(mockWeatherData)
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
    fun noLocationWeatherDataFailedErrorMessageDisplayed() {

        mockService.mockWeatherResponse = Response.error(400, "".toResponseBody("text/plain".toMediaTypeOrNull()))
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

        composeTestRule.onNodeWithText(context.getString(R.string.errMessage)).assertIsDisplayed()
    }



    // Location Button/Permission Tests


    // Requires clicking on don't allow for the permissions on the emulator
    @Test
    fun locationButtonClickedDisplaysPermissionsAndRationaleWhenDenied() {

        composeTestRule.setContent {
            FirstScreen(
                viewModel = viewModel,
                onNavigateForecastClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        // Must click don't allow for both or either permissions
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.locationIcon)).performClick()
        uiDevice.wait(Until.gone(By.textContains("Allow")), 8000)

        val permissionDialog = uiDevice.wait(Until.findObject(By.textContains("Allow")), 8000)
        assert(permissionDialog != null)
        composeTestRule.onNodeWithText(context.getString(R.string.rationaleTitle)).assertIsDisplayed()
    }



    // Requires clicking on don't allow for the permissions on the emulator
    @Test
    fun locationButtonClickedDisplaysRationaleWhenDeniedAndPermissionsAgainWhenOk() {

        composeTestRule.setContent {
            FirstScreen(
                viewModel = viewModel,
                onNavigateForecastClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        // Must click don't allow for both or either permissions
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.locationIcon)).performClick()
        uiDevice.wait(Until.gone(By.textContains("Allow")), 10000)

        composeTestRule.onNodeWithText(context.getString(R.string.alertOk)).performClick()
    }


    // Requires clicking on don't allow for the permissions on the emulator
    @Test
    fun locationButtonClickedDisplaysWeatherDataSucceededWhenAllowedAndNoRationale() {

        composeTestRule.setContent {
            FirstScreen(
                viewModel = viewModel,
                onNavigateForecastClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        // Must click on allow for both permissions
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.locationIcon)).performClick()
        uiDevice.wait(Until.gone(By.textContains("Allow")), 8000)

        mockService.mockWeatherResponse = Response.success(mockWeatherData)
        viewModel.getData(0.0, 0.0)

        composeTestRule.onNodeWithText("Sunny").assertIsDisplayed()
        val rationaleNodes = composeTestRule.onAllNodes(hasText(context.getString(R.string.rationaleTitle)))
        Assert.assertEquals(0, rationaleNodes.fetchSemanticsNodes().size)
    }
}