package com.example.maintainingcar.utils

import java.math.BigDecimal

fun Double.roundTo1DecimalPlaces() =
    BigDecimal(this).setScale(1, BigDecimal.ROUND_HALF_UP).toDouble()
