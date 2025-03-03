package com.fang.cosmos.foundation.time.transformer

import android.content.Context
import android.os.Looper
import android.text.format.DateFormat
import com.fang.cosmos.definition.CosmosDef
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

interface TimeConverter {
    companion object {
        /**
         * 將 [timeMillis] format 成具有 [pattern] date string
         */
        fun format(
            timeMillis: Long?,
            pattern: String = TimePattern.default,
            locale: Locale = Locale.getDefault(),
            timeZone: TimeZone = TimeZone.getDefault(),
        ) = runCatching {
            getDateFormatter(
                pattern = pattern,
                locale = locale,
                timeZone = timeZone,
            ).format(timeMillis)
        }.getOrNull()

        /**
         * 將 [date] format 成具有 [pattern] date string
         */
        fun format(
            date: Date?,
            pattern: String = TimePattern.default,
            locale: Locale = Locale.getDefault(),
            timeZone: TimeZone = TimeZone.getDefault(),
        ) = format(
            timeMillis = date?.time,
            pattern = pattern,
            locale = locale,
            timeZone = timeZone,
        )

        /**
         * 將 [calendar] format 成具有 [pattern] date string
         */
        fun format(
            calendar: Calendar?,
            pattern: String = TimePattern.default,
            locale: Locale = Locale.getDefault(),
            timeZone: TimeZone = TimeZone.getDefault(),
        ) = format(
            date = calendar?.time,
            pattern = pattern,
            locale = locale,
            timeZone = timeZone,
        )

        /**
         * 將具有 [pattern] 的 [timeString] parse 成 [Date]
         */
        fun parse(
            pattern: String,
            timeString: String?,
            locale: Locale = Locale.getDefault(),
            timeZone: TimeZone = TimeZone.getDefault(),
        ) = runCatching {
            getDateFormatter(
                pattern = pattern,
                locale = locale,
                timeZone = timeZone,
            ).parse(requireNotNull(timeString))
        }.getOrNull()

        /**
         * 將具有 [inputPattern] 的 [timeString] transform 成具有 [outputPattern] date string
         */
        fun transform(
            inputPattern: String,
            timeString: String,
            outputPattern: String = TimePattern.default,
            inputLocale: Locale = Locale.getDefault(),
            inputTimeZone: TimeZone = TimeZone.getDefault(),
            outputLocale: Locale = Locale.getDefault(),
            outputTimeZone: TimeZone = TimeZone.getDefault(),
        ) = format(
            date =
                parse(
                    pattern = inputPattern,
                    timeString = timeString,
                    locale = inputLocale,
                    timeZone = inputTimeZone,
                ),
            pattern = outputPattern,
            locale = outputLocale,
            timeZone = outputTimeZone,
        )

        /**
         * 取得 thread-safe 的 [SimpleDateFormat]
         */
        private fun getDateFormatter(
            pattern: String = TimePattern.SYSTEM_Y_M_D,
            locale: Locale = Locale.getDefault(),
            timeZone: TimeZone = TimeZone.getDefault(),
            context: Context = CosmosDef.Context,
        ): SimpleDateFormat {
            val newPattern =
                if (pattern == TimePattern.SYSTEM_Y_M_D) {
                    kotlin
                        .runCatching {
                            context.resources.configuration.setLocale(locale)
                            val systemSdf = DateFormat.getDateFormat(context) as? SimpleDateFormat
                            systemSdf?.toLocalizedPattern()
                        }.getOrNull() ?: TimePattern.default
                } else {
                    pattern
                }
            val formatter =
                SimpleDateFormat(newPattern, locale).apply { this.timeZone = timeZone }
            val onMainThread = Looper.myLooper() == Looper.getMainLooper()
            return if (!onMainThread) {
                formatter.clone() as SimpleDateFormat
            } else {
                formatter
            }
        }
    }
}
