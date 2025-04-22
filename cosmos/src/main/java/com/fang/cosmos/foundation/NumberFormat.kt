package com.fang.cosmos.foundation

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

object NumberFormat {
    operator fun invoke(
        string: String?,
        decimal: Int? = null,
        thousandSeparator: Boolean = true,
        rounding: RoundingMode? = null,
        negativePrefix: String = "-",
        positivePrefix: String = "",
        autoFillZero: Boolean = true,
    ): String? {
        val bigDecimal = string?.toBigDecimalOrNull()
        return if (bigDecimal != null && (decimal == null || decimal >= 0)) {
            val placeholder = if (autoFillZero) "0" else "#"
            val integer =
                if (bigDecimal.abs() >= BigDecimal.ONE) {
                    if (thousandSeparator) ",##$placeholder" else placeholder
                } else if (string.first() == '.') {
                    if (autoFillZero) "0" else ""
                } else {
                    placeholder
                }
            val decimalSize =
                if (decimal == null) {
                    val index = string.indexOf('.')
                    if (index != -1) string.lastIndex - index else 0
                } else {
                    decimal
                }
            val decimalPattern =
                if (decimalSize > 0) {
                    val decimalPlace = if (decimal == null) "0" else placeholder
                    ".${decimalPlace.repeat(decimalSize)}"
                } else {
                    ""
                }
            DecimalFormat("$integer$decimalPattern")
                .apply {
                    this.negativePrefix = negativePrefix
                    if (bigDecimal.signum() > 0) this.positivePrefix = positivePrefix
                    rounding?.let { this.roundingMode = it }
                }.format(bigDecimal)
        } else {
            null
        }
    }

    operator fun invoke(
        number: Number?,
        decimal: Int? = null,
        thousandSeparator: Boolean = true,
        rounding: RoundingMode? = null,
        negativePrefix: String = "-",
        positivePrefix: String = "",
        autoFillZero: Boolean = true,
    ) = invoke(
        string = number?.toString(),
        decimal = decimal,
        rounding = rounding,
        negativePrefix = negativePrefix,
        positivePrefix = positivePrefix,
        autoFillZero = autoFillZero,
        thousandSeparator = thousandSeparator,
    )
}
