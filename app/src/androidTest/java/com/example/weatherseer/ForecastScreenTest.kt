package com.example.weatherseer

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class ForecastScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var mockService: MockServiceUI
    private lateinit var viewModel: WeatherViewModel
    private lateinit var mockForecastData: ForecastMetaData
    private lateinit var sampleZip: String
    private val sampleAppId = ""
    private val sampleUnits = ""
    private val sampleErr = ""

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        mockService = MockServiceUI()
        viewModel = WeatherViewModel(mockService, sampleAppId, sampleUnits, sampleErr)
        mockForecastData = ForecastMetaData(
            city = City(
                0, "Saint Paul",
                coord = Coord(0.0, 0.0),
                country = "US",
                population = 0,
                timezone = 0
            ),
            list = listOf(
                ForecastList(
                    weather = arrayListOf(Weather(
                        icon = "01d",
                        description = "sunny",
                        id = 0,
                        main = ""
                    )),
                    temp = Temp(day = 0.0, night = 0.0),
                    feelsLike = FeelsLike(day = 0.0),
                    humidity = 0
                )
            )
        )
        sampleZip = "12345"
    }



    @Test
    fun forecastScreenTitleDisplayed() {

        composeTestRule.setContent {
            ForecastScreen(
                viewModel = viewModel,
                zip = sampleZip,
                onNavigateBackClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.forecastTitle)).assertExists()
    }



    @Test
    fun forecastBackButtonDisplayedAndNavigatesBack() {

        var navigateBackClicked = false

        composeTestRule.setContent {
                ForecastScreen(
                    viewModel = viewModel,
                    zip = sampleZip,
                    onNavigateBackClicked = { navigateBackClicked = true },
                    lat = 0.0,
                    lon = 0.0,
                    startLocationUpdates = {}
                )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.back)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.back)).performClick()
        Assert.assertEquals(true, navigateBackClicked)
    }



    // Successful Request Tests


    @Test
    fun forecastDataSucceededAlertNotDisplayed() {

        mockService.mockForecastResponse = Response.success(mockForecastData)
        viewModel.getForecastData(sampleZip)

        composeTestRule.setContent {
            ForecastScreen(
                viewModel = viewModel,
                zip = sampleZip,
                onNavigateBackClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        val alertTitleNodes = composeTestRule.onAllNodes(hasText(context.getString(R.string.alertTitle)))
        val alertMessageNodes = composeTestRule.onAllNodes(hasText(context.getString(R.string.alertMessage)))
        val alertOkNodes = composeTestRule.onAllNodes(hasText(context.getString(R.string.alertOk)))

        Assert.assertEquals(0, alertTitleNodes.fetchSemanticsNodes().size)
        Assert.assertEquals(0, alertMessageNodes.fetchSemanticsNodes().size)
        Assert.assertEquals(0, alertOkNodes.fetchSemanticsNodes().size)
    }



    @Test
    fun noLocationForecastDataSucceededAndDisplaysCity() {

        mockService.mockForecastResponse = Response.success(mockForecastData)
        viewModel.getForecastData(sampleZip)

        composeTestRule.setContent {

                ForecastScreen(
                    viewModel = viewModel,
                    zip = sampleZip,
                    onNavigateBackClicked = {},
                    lat = 0.0,
                    lon = 0.0,
                    startLocationUpdates = {}
                )
        }

        composeTestRule.onNodeWithText("Saint Paul, US").assertIsDisplayed()
    }



    @Test
    fun hasLocationForecastDataSucceededAndDisplaysCity() {

        mockService.mockForecastResponse = Response.success(mockForecastData)
        viewModel.getForecastData(0.0, 0.0)

        composeTestRule.setContent {

            ForecastScreen(
                viewModel = viewModel,
                zip = sampleZip,
                onNavigateBackClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        composeTestRule.onNodeWithText("Saint Paul, US").assertIsDisplayed()
    }



    @Test
    fun noLocationForecastDataSucceededAndDisplayedInColumn() {

        mockService.mockForecastResponse = Response.success(mockForecastData)
        viewModel.getForecastData(sampleZip)

        composeTestRule.setContent {
                ForecastScreen(
                    viewModel = viewModel,
                    zip = sampleZip,
                    onNavigateBackClicked = {},
                    lat = 0.0,
                    lon = 0.0,
                    startLocationUpdates = {}
                )
        }

        composeTestRule.onNodeWithContentDescription(context.getString(R.string.sunny)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.sunny)).assertIsDisplayed()
        composeTestRule.onNodeWithText("0°").assertIsDisplayed()
        composeTestRule.onNodeWithText("0% Humidity ").assertIsDisplayed()
    }



    @Test
    fun hasLocationForecastDataSucceededAndDisplayedInColumn() {

        mockService.mockForecastResponse = Response.success(mockForecastData)
        viewModel.getForecastData(0.0, 0.0)

        composeTestRule.setContent {
            ForecastScreen(
                viewModel = viewModel,
                zip = "",
                onNavigateBackClicked = {},
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        composeTestRule.onNodeWithContentDescription(context.getString(R.string.sunny)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.sunny)).assertIsDisplayed()
        composeTestRule.onNodeWithText("0°").assertIsDisplayed()
        composeTestRule.onNodeWithText("0% Humidity ").assertIsDisplayed()
    }



    // Failed Request Tests


    @Test
    fun noLocationForecastDataFailedAlertDisplayedAndNavigatesBack() {

        var navigateBackClicked = false

        mockService.mockForecastResponse = Response.error(404, "".toResponseBody(null))
        viewModel.getForecastData(sampleZip)

        composeTestRule.setContent {
            ForecastScreen(
                viewModel = viewModel,
                zip = sampleZip,
                onNavigateBackClicked = { navigateBackClicked = true },
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        val alertTitleNodes = composeTestRule.onAllNodes(hasText(context.getString(R.string.alertTitle)))
        val alertMessageNodes = composeTestRule.onAllNodes(hasText(context.getString(R.string.alertMessage)))
        val alertOkNodes = composeTestRule.onAllNodes(hasText(context.getString(R.string.alertOk)))

        Assert.assertEquals(1, alertTitleNodes.fetchSemanticsNodes().size)
        Assert.assertEquals(1, alertMessageNodes.fetchSemanticsNodes().size)
        Assert.assertEquals(1, alertOkNodes.fetchSemanticsNodes().size)

        composeTestRule.onNodeWithText(context.getString(R.string.alertOk)).performClick()
        Assert.assertEquals(true, navigateBackClicked)
    }



    @Test
    fun hasLocationForecastDataFailedAlertDisplayedAndNavigatesBack() {

        var navigateBackClicked = false

        mockService.mockForecastResponse = Response.error(404, "".toResponseBody(null))
        viewModel.getForecastData(0.0, 0.0)

        composeTestRule.setContent {
            ForecastScreen(
                viewModel = viewModel,
                zip = sampleZip,
                onNavigateBackClicked = { navigateBackClicked = true },
                lat = 0.0,
                lon = 0.0,
                startLocationUpdates = {}
            )
        }

        val alertTitleNodes = composeTestRule.onAllNodes(hasText(context.getString(R.string.alertTitle)))
        val alertMessageNodes = composeTestRule.onAllNodes(hasText(context.getString(R.string.alertMessage)))
        val alertOkNodes = composeTestRule.onAllNodes(hasText(context.getString(R.string.alertOk)))

        Assert.assertEquals(1, alertTitleNodes.fetchSemanticsNodes().size)
        Assert.assertEquals(1, alertMessageNodes.fetchSemanticsNodes().size)
        Assert.assertEquals(1, alertOkNodes.fetchSemanticsNodes().size)

        composeTestRule.onNodeWithText(context.getString(R.string.alertOk)).performClick()
        Assert.assertEquals(true, navigateBackClicked)
    }

}