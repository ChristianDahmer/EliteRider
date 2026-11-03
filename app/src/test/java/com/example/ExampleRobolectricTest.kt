package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CoastalAirport
import com.example.data.model.CoastalFlightPhase
import com.example.data.model.LuxuryAmenity
import com.example.data.model.VehicleCategory
import com.example.localization.AppLanguage
import com.example.localization.LocaleManager
import com.example.viewmodel.BookingFormState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("LuxeDrive VIP", appName)
  }

  @Test
  fun `test localization manager across multiple languages`() {
    val ptTitle = LocaleManager.getString("app_title", AppLanguage.PORTUGUESE)
    val enTitle = LocaleManager.getString("app_title", AppLanguage.ENGLISH)
    val esTitle = LocaleManager.getString("app_title", AppLanguage.SPANISH)
    val frTitle = LocaleManager.getString("app_title", AppLanguage.FRENCH)

    assertEquals("LuxeDrive VIP", ptTitle)
    assertEquals("LuxeDrive VIP", enTitle)
    assertEquals("LuxeDrive VIP", esTitle)
    assertEquals("LuxeDrive VIP", frTitle)

    val ptBooking = LocaleManager.getString("confirm_booking", AppLanguage.PORTUGUESE)
    val enBooking = LocaleManager.getString("confirm_booking", AppLanguage.ENGLISH)
    assertTrue(ptBooking.contains("Confirmar"))
    assertTrue(enBooking.contains("Confirm"))
  }

  @Test
  fun `test vehicle category base rates`() {
    assertTrue(VehicleCategory.ARMORED_VIP.basePrice > VehicleCategory.FIRST_CLASS_SEDAN.basePrice)
    assertTrue(VehicleCategory.ARMORED_VIP.isArmoredDefault)
  }

  @Test
  fun `test coastal airport models and runway specs`() {
    val santos = CoastalAirport.GUARUJA_SANTOS
    val ubatuba = CoastalAirport.UBATUBA
    val angra = CoastalAirport.ANGRA_PARATY
    val caboFrio = CoastalAirport.CABO_FRIO

    assertEquals("SSZ", santos.code)
    assertEquals("SBDN", santos.icao)
    assertTrue(santos.airportName.contains("Guarujá"))

    assertEquals("UBT", ubatuba.code)
    assertEquals("SDUB", ubatuba.icao)

    assertEquals("JDR", angra.code)
    assertEquals("CFB", caboFrio.code)
  }
}
