package com.eundmswlji.tacoling.util

import java.text.DecimalFormat

object Ext {

    fun Int.wonFormat(): String = DecimalFormat("#,###원").format(this)

}