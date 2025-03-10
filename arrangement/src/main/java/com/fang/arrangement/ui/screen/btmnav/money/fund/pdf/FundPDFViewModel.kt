package com.fang.arrangement.ui.screen.btmnav.money.fund.pdf

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.arrangement.definition.sheet.sheetFund
import com.fang.arrangement.ui.screen.btmnav.money.fund.MFund
import com.fang.arrangement.ui.screen.btmnav.money.fund.YearMonthFund
import com.fang.arrangement.ui.screen.btmnav.money.fund.YearMonthFund.DayFund
import com.fang.arrangement.ui.screen.btmnav.money.fund.totalFund
import com.fang.arrangement.ui.screen.btmnav.statistic.pdf.PdfBundle
import com.fang.arrangement.ui.shared.dsl.YMDDayOfWeek
import com.fang.cosmos.definition.workstate.WorkState
import com.fang.cosmos.definition.workstate.WorkStateImpl
import com.fang.cosmos.foundation.NumberFormat
import com.fang.cosmos.foundation.isMulti
import com.fang.cosmos.foundation.time.calendar.ChineseDayOfWeek
import com.fang.cosmos.foundation.time.calendar.dayOfMonth
import com.fang.cosmos.foundation.time.calendar.month
import com.fang.cosmos.foundation.time.calendar.today
import com.fang.cosmos.foundation.time.calendar.year
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.OutputStream

@OptIn(ExperimentalCoroutinesApi::class)
internal class FundPDFViewModel(
    sheetRepository: SheetRepository,
) : ViewModel(),
    WorkState by WorkStateImpl() {
    private companion object {
        const val WIDTH = 595
        const val HEIGHT = 842
        const val LEFT_MARGIN = 20f
        const val TOP_MARGIN = 32f
        const val DEFAULT_X = -1f
    }

    private data class RequestTrigger(
        val startMillis: Long,
        val endMillis: Long,
    )

    private data class Mediator(
        val year: String,
        val totalFund: String,
    )

    private data class Draw(
        val text: String,
        val paint: Paint,
        val x: Float = DEFAULT_X,
    ) {
        val textHeight get() = with(paint.fontMetrics) { descent - ascent }
    }

    private val requestTrigger = MutableStateFlow<RequestTrigger?>(null)
    private val _request = MutableStateFlow<PDFRequest?>(null)
    val request = _request.asStateFlow()
    private val _pdfBundle = MutableStateFlow<PdfBundle?>(null)
    val pdfBundle = _pdfBundle.asStateFlow()

    init {
        viewModelScope.launch {
            requestTrigger
                .flatMapLatest { request ->
                    if (request == null) {
                        emptyFlow()
                    } else {
                        sheetRepository.workSheets
                            .mapLatest { workSheets ->
                                workSheets
                                    ?.sheetFund()
                                    ?.values
                                    ?.filter {
                                        it.millis in request.startMillis..request.endMillis
                                    }?.sortedByDescending { it.id }
                                    ?.let { funds ->
                                        val ymFund =
                                            funds
                                                .map {
                                                    MFund(
                                                        selected = false,
                                                        id = it.id,
                                                        fund = it.fund,
                                                        millis = it.millis,
                                                        remark = it.remark,
                                                    )
                                                }.sortedWith(
                                                    compareByDescending<MFund> { it.millis }
                                                        .thenByDescending { it.id },
                                                ).groupBy {
                                                    with(today(it.millis)) { year to month }
                                                }.map { (pair, yFunds) ->
                                                    val (year, month) = pair
                                                    val dayFunds =
                                                        yFunds
                                                            .groupBy {
                                                                today(it.millis).dayOfMonth
                                                            }.map { (day, funds) ->
                                                                DayFund(day = day, funds = funds)
                                                            }
                                                    YearMonthFund(year = year, month = month, dayFunds = dayFunds)
                                                }
                                        val pdfDocument = PdfDocument()
                                        var pdfPage = createNewPage(pdfDocument)
                                        var y = TOP_MARGIN
                                        val draws = { draws: List<Draw> ->
                                            val maxLineHeight = draws.maxOfOrNull { it.textHeight } ?: 0f
                                            if (y + maxLineHeight > HEIGHT - TOP_MARGIN) {
                                                pdfDocument.finishPage(pdfPage)
                                                pdfPage = createNewPage(pdfDocument)
                                                y = TOP_MARGIN
                                            }
                                            draws.forEachIndexed { i, draw ->
                                                pdfPage.canvas.drawText(
                                                    draw.text,
                                                    LEFT_MARGIN +
                                                        if (draw.x == DEFAULT_X) {
                                                            draws
                                                                .take(i)
                                                                .sumOf {
                                                                    it.paint.measureText(it.text).toDouble()
                                                                }.toFloat()
                                                        } else {
                                                            draw.x
                                                        },
                                                    y,
                                                    draw.paint,
                                                )
                                            }
                                            y += maxLineHeight
                                        }
                                        val draw = { draw: Draw -> draws(listOf(draw)) }
                                        val newLine = { textSize: Float ->
                                            draw(
                                                Draw(
                                                    text = "\n",
                                                    paint = paint(textSize, Color.TRANSPARENT),
                                                ),
                                            )
                                        }
                                        val start = YMDDayOfWeek(request.startMillis)
                                        val end = YMDDayOfWeek(request.endMillis)
                                        draw(
                                            Draw(
                                                text = "$start - $end",
                                                paint = paint(24f).typeface(Typeface.DEFAULT_BOLD),
                                            ),
                                        )
                                        newLine(20f)
                                        if (ymFund.isEmpty()) {
                                            draw(
                                                Draw(
                                                    text = "(無資料)",
                                                    paint = paint(20f),
                                                ),
                                            )
                                        } else {
                                            val contentPaint = paint(16f)
                                            val targets = { title: String, mediators: List<Mediator>? ->
                                                draw(
                                                    Draw(
                                                        text = title,
                                                        paint =
                                                            paint(20f).typeface(Typeface.DEFAULT_BOLD),
                                                    ),
                                                )
                                                mediators?.forEach { m ->
                                                    draws(
                                                        listOf(
                                                            Draw(
                                                                text = m.year,
                                                                paint = contentPaint,
                                                            ),
                                                            Draw(
                                                                text = m.totalFund,
                                                                paint = contentPaint,
                                                                x = contentPaint.measureText("${m.year} "),
                                                            ),
                                                        ),
                                                    )
                                                }
                                            }
                                            val mediators =
                                                ymFund
                                                    .groupBy { it.year }
                                                    .map { (k, v) ->
                                                        Mediator(k.toString(), v.totalFund)
                                                    }.takeIf { it.isMulti }
                                            targets("總計 ${ymFund.totalFund}", mediators)
                                            newLine(20f)
                                            ymFund.forEachIndexed { i, attAll ->
                                                val year = attAll.year
                                                val month = attAll.month
                                                val pre = "0".takeIf { month < 9 }.orEmpty()
                                                if (i == 0 || year != ymFund[i - 1].year || month != ymFund[i - 1].month) {
                                                    newLine(16f)
                                                    draw(
                                                        Draw(
                                                            text = "$year/$pre${month + 1}  ${attAll.totalFundDisplay}",
                                                            paint =
                                                                paint(20f, Color.BLACK)
                                                                    .typeface(Typeface.DEFAULT_BOLD),
                                                        ),
                                                    )
                                                }
                                                attAll.dayFunds.forEach { att ->
                                                    val dayPre = "0".takeIf { att.day < 10 }.orEmpty()
                                                    val date = "$pre${month + 1}/$dayPre${att.day}"
                                                    val chDate = ChineseDayOfWeek(att.funds.first().millis)
                                                    att.funds.forEachIndexed { j, item ->
                                                        val prettyFund =
                                                            NumberFormat(
                                                                number = item.fund,
                                                                decimalCount = 0,
                                                            )
                                                        draws(
                                                            listOf(
                                                                Draw(
                                                                    text = "$date ($chDate)   ",
                                                                    paint =
                                                                        if (j == 0) {
                                                                            contentPaint
                                                                        } else {
                                                                            paint(16f, Color.TRANSPARENT)
                                                                        },
                                                                ),
                                                                Draw(
                                                                    text = "$$prettyFund  ${item.remark.orEmpty().replace("\n", "・")}",
                                                                    paint = contentPaint,
                                                                ),
                                                            ),
                                                        )
                                                    }
                                                }
                                                newLine(4f)
                                            }
                                        }
                                        pdfDocument.finishPage(pdfPage)
                                        PdfBundle(pdfDocument, request.startMillis, request.endMillis)
                                    }
                            }
                    }
                }.flowOn(Dispatchers.IO)
                .collectLatest {
                    _pdfBundle.value = it
                }
        }
    }

    fun startDownload(
        startMillis: Long,
        endMillis: Long,
    ) {
        loading()
        requestTrigger.value =
            RequestTrigger(
                startMillis = startMillis,
                endMillis = endMillis,
            )
        clearRequest()
    }

    fun finishDownload(
        out: OutputStream,
        pdf: PdfDocument,
    ) {
        runBlocking(Dispatchers.IO) {
            runCatching {
                pdf.writeTo(out)
                pdf.close()
                _pdfBundle.value = null
                requestTrigger.value = null
                noLoading()
            }
        }
    }

    fun showRequest() {
        _request.value = PDFRequest.default
    }

    fun editStartMillis(millis: Long?) = update { it.copy(startMillis = millis) }

    fun editEndMillis(millis: Long?) = update { it.copy(endMillis = millis) }

    fun clearRequest() {
        _request.value = null
    }

    private fun update(function: (PDFRequest) -> PDFRequest?) {
        _request.update { it?.let(function) }
    }

    private fun paint(
        textSize: Float,
        color: Int = Color.BLACK,
        block: Paint.() -> Unit = {},
    ) = Paint().apply {
        this.textSize = textSize
        this.color = color
        block(this)
    }

    private fun Paint.typeface(typeface: Typeface) =
        apply {
            this.typeface = typeface
        }

    private fun createNewPage(pdfDocument: PdfDocument) =
        pdfDocument.startPage(
            PdfDocument.PageInfo.Builder(WIDTH, HEIGHT, 1).create(),
        )
}
