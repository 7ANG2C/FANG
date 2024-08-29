package com.fang.cosmos.foundation.number

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

object NumberFormat {
    /**
     * 格式化數字格式
     * @param string 當可以轉換成 [BigDecimal] 才進行 format ，否則回傳 invalidText
     * @param decimalCount 小數點保留位數，若 < 0 拋出 [IllegalArgumentException]
     * @param autoFillZero 是否自動補 0，當 true，數字 =0 時，自動補 0
     * @param thousandSeparator 有否有千位分隔符
     */
    operator fun invoke(
        string: String?,
        decimalCount: Int? = null,
        roundingMode: RoundingMode? = null,
        negativePrefix: String = "-",
        positivePrefix: String = "",
        invalidText: String = "",
        autoFillZero: Boolean = true,
        thousandSeparator: Boolean = true,
    ): String {
        require(decimalCount == null || decimalCount >= 0)
        val bigDecimal = string?.toBigDecimalOrNull()
        return if (bigDecimal != null) {
            var dot = ""
            val decimalSize =
                if (decimalCount == null) {
                    val index = string.indexOf('.')
                    if (index != -1) {
                        if (index == string.lastIndex) dot = "."
                        string.lastIndex - index
                    } else {
                        0
                    }
                } else {
                    decimalCount
                }
            val placeholder = if (autoFillZero) "0" else "#"
            val integer =
                if (thousandSeparator) {
                    ",##$placeholder"
                } else {
                    placeholder
                }.takeIf { autoFillZero || bigDecimal.abs() > BigDecimal.ONE }.orEmpty()
            val decimal =
                if (decimalSize > 0) {
                    ".${placeholder.repeat(decimalSize)}"
                } else {
                    ""
                }
            DecimalFormat("$integer$decimal").apply {
                this.negativePrefix = negativePrefix
                if (bigDecimal.signum() > 0) this.positivePrefix = positivePrefix
                roundingMode?.let { this.roundingMode = it }
            }.format(bigDecimal) + dot
        } else {
            invalidText
        }
    }

    operator fun invoke(
        number: Number?,
        decimalCount: Int? = null,
        roundingMode: RoundingMode? = null,
        negativePrefix: String = "-",
        positivePrefix: String = "",
        invalidText: String = "",
        autoFillZero: Boolean = true,
        thousandSeparator: Boolean = true,
    ) = invoke(
        string = number?.toString(),
        decimalCount = decimalCount,
        roundingMode = roundingMode,
        negativePrefix = negativePrefix,
        positivePrefix = positivePrefix,
        invalidText = invalidText,
        autoFillZero = autoFillZero,
        thousandSeparator = thousandSeparator,
    )
}
