package com.eundmswlji.tacoling

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
