package com.eundmswlji.tacoling

import android.view.Menu
import com.eundmswlji.tacoling.data.model.ShopItem
import com.eundmswlji.tacoling.data.model.ShopX
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
       assertEquals( Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1,0)
    }
}
