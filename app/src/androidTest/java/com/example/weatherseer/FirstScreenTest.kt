package com.example.weatherseer

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.ComposeNavigator
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

    // Test notifications

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var mockService: MockServiceUI
    private lateinit var navController: NavController
    private lateinit var viewModel: WeatherViewModel
    private lateinit var mockWeatherData: WeatherMetaData
    private lateinit var uiDevice: UiDevice
    private val sampleZip = "12345"
    private val sampleAppId = ""
    private val sampleUnits = ""
    private val sampleErr = "Couldn't find what you were looking for."

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        mockService = MockServiceUI()
        navController = androidx.navigation.testing.TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        viewModel = WeatherViewModel(mockService, sampleAppId, sampleUnits, sampleErr)
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
            name = "Minneapolis",
            cod = 0
        )
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
        val toastAppeared = uiDevice.wait(Until.hasObject(By.text(context.getString(R.string.toastLength))), 3000)
        assert(toastAppeared != null)
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
        val toastAppeared = uiDevice.wait(Until.hasObject(By.text(context.getString(R.string.toastInvalid))), 3000)
        assert(toastAppeared != null)
        Assert.assertEquals(false, navigateForecastClicked)
    }



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

        composeTestRule.onNodeWithText("Minneapolis, US").assertIsDisplayed()
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
        viewModel.getData(sampleZip)

        composeTestRule.setContent {
            FirstScreen(
                viewModel = viewModel,
                onNavigateForecastClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        composeTestRule.onNodeWithText("Minneapolis, US").assertIsDisplayed()
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
        viewModel.getData(sampleZip)

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
        // Deny permissions
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.locationIcon)).performClick()
        val permissionDialog = uiDevice.wait(Until.findObject(By.textContains("Allow")), 8000)
        uiDevice.findObject(By.textStartsWith("Don")).click()
        uiDevice.findObject(By.textStartsWith("Don")).click()

        // Assert that permissions and rationale were displayed
        assert(permissionDialog != null)
        composeTestRule.onNodeWithText(context.getString(R.string.rationaleTitle)).assertIsDisplayed()
    }



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
        // Deny permissions
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.locationIcon)).performClick()
        uiDevice.findObject(By.textStartsWith("Don")).click()
        uiDevice.findObject(By.textStartsWith("Don")).click()

        // Click ok on rationale to relaunch permissions
        composeTestRule.onNodeWithText(context.getString(R.string.alertOk)).performClick()

        // Assert that permissions were displayed again
        val permissionDialog = uiDevice.wait(Until.findObject(By.textContains("Allow")), 8000)
        assert(permissionDialog != null)
    }



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
        // Allow Permissions
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.locationIcon)).performClick()
        uiDevice.findObject(By.text("While using the app")).click()
        uiDevice.findObject(By.text("Allow")).click()

        // Update the UI
        mockService.mockWeatherResponse = Response.success(mockWeatherData)
        viewModel.getData(0.0, 0.0)

        // Assert data is displayed and rationale is not/does not exist
        composeTestRule.onNodeWithText("Sunny").assertIsDisplayed()
        val rationaleNodes = composeTestRule.onAllNodes(hasText(context.getString(R.string.rationaleTitle)))
        Assert.assertEquals(0, rationaleNodes.fetchSemanticsNodes().size)
    }



    // Notification Test


    @Test
    fun isNotificationDisplayed() {

        composeTestRule.setContent {
            FirstScreen(
                viewModel = viewModel,
                onNavigateForecastClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }
        // Allow permissions
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.locationIcon)).performClick()
        uiDevice.findObject(By.text("While using the app")).click()
        uiDevice.findObject(By.text("Allow")).click()

        // Update the UI with location
        mockService.mockWeatherResponse = Response.success(mockWeatherData)
        viewModel.getData(0.0, 0.0)
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.locationIcon)).performClick()

        // Show the notification
        uiDevice.openNotification()
        Thread.sleep(2000)
        uiDevice.pressBack()

        // Assert that notification existed and not null
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifications = notificationManager.activeNotifications
        assert(notifications.isNotEmpty())
        assert(notifications.firstOrNull() != null)
    }

}


