package com.fang.arrangement.ui.screen.btmnav.statistic.pdf

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.arrangement.definition.sheet.sheetAttendance
import com.fang.arrangement.definition.sheet.sheetEmployee
import com.fang.arrangement.definition.sheet.sheetSite
import com.fang.arrangement.foundation.DASH
import com.fang.arrangement.ui.shared.dsl.AttendanceNumFormat
import com.fang.arrangement.ui.shared.dsl.YMDDayOfWeek
import com.fang.cosmos.definition.workstate.WorkState
import com.fang.cosmos.definition.workstate.WorkStateImpl
import com.fang.cosmos.foundation.NumberFormat
import com.fang.cosmos.foundation.time.calendar.ChineseDayOfWeek
import com.fang.cosmos.foundation.time.calendar.today
import com.fang.cosmos.foundation.time.calendar.year
import com.fang.cosmos.foundation.time.transformer.TimeConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.OutputStream
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
internal class PDFViewModel(
    sheetRepository: SheetRepository,
) : ViewModel(), WorkState by WorkStateImpl() {
    private companion object {
        val BLUE = Color.parseColor("#0270ed")
        const val WIDTH = 595
        const val HEIGHT = 842
        const val LEFT_MARGIN = 20f
        const val TOP_MARGIN = 32f
        const val DEFAULT_X = -1f
    }

    private data class RequestTrigger(
        val startMillis: Long,
        val endMillis: Long,
        val includeRemark: Boolean,
    )

    private data class Site(val id: Long, val name: String?)

    private data class Employee(
        val id: Long,
        val name: String?,
        val salary: Int?,
        val factor: Double,
    )

    private data class AttAll(
        val dayMillis: Long,
        val attendances: List<Att>,
    )

    private data class Att(
        val site: Site,
        val fulls: List<Employee>,
        val halfs: List<Employee>,
        val remark: String?,
    ) {
        val total get() = fulls.size + halfs.size * 0.5
    }

    private data class TargetAttendance(val name: String?, val att: Double, val salary: BigDecimal?)

    private data class Draw(
        val text: String,
        val paint: Paint,
        val x: Float = DEFAULT_X,
    ) {
        val textHeight get() = with(paint.fontMetrics) { descent - ascent }
    }

    private val String?.orDeleted get() = this ?: "已刪除"

    private val requestTrigger = MutableStateFlow<RequestTrigger?>(null)
    private val _request = MutableStateFlow<PDFRequest?>(null)
    val request = _request.asStateFlow()
    private val _pdfBundle = MutableStateFlow<PdfBundle?>(null)
    val pdfBundle = _pdfBundle.asStateFlow()

    init {
        viewModelScope.launch {
            requestTrigger.filterNotNull().flatMapLatest { request ->
                sheetRepository.workSheets
                    .mapLatest { workSheets ->
                        val sitesDeferred =
                            async(Dispatchers.Default) { workSheets?.sheetSite()?.values }
                        val employeesDeferred =
                            async(Dispatchers.Default) { workSheets?.sheetEmployee()?.values }
                        val sites = sitesDeferred.await()
                        val employees = employeesDeferred.await()
                        workSheets?.sheetAttendance()?.values?.filter {
                            it.id in request.startMillis..request.endMillis
                        }?.sortedByDescending { it.id }?.let { attendanceAlls ->
                            val attAlls =
                                attendanceAlls.map { attAll ->
                                    AttAll(
                                        dayMillis = attAll.id,
                                        attendances =
                                            attAll.attendances.map { att ->
                                                Att(
                                                    site =
                                                        sites?.find { it.id == att.siteId }?.let {
                                                            Site(it.id, it.name)
                                                        } ?: Site(att.siteId, null),
                                                    fulls =
                                                        att.fulls.map { id ->
                                                            employees?.find { it.id == id }?.let {
                                                                val s =
                                                                    it.salaries.find { s ->
                                                                        attAll.id >= s.millis
                                                                    }?.salary
                                                                Employee(it.id, it.name, s, 1.0)
                                                            } ?: Employee(id, null, null, 1.0)
                                                        },
                                                    halfs =
                                                        att.halfs.map { id ->
                                                            employees?.find { it.id == id }?.let {
                                                                val s =
                                                                    it.salaries.find { s ->
                                                                        attAll.id >= s.millis
                                                                    }?.salary
                                                                Employee(it.id, it.name, s, 0.5)
                                                            } ?: Employee(id, null, null, 0.5)
                                                        },
                                                    remark = att.remark.takeIf { request.includeRemark },
                                                )
                                            },
                                    )
                                }
                            val flattenAtts = attAlls.flatMap { it.attendances }
                            val pdfSitesDeferred =
                                async(Dispatchers.Default) {
                                    flattenAtts.groupBy { it.site.id }
                                        .mapNotNull { (_, atts) ->
                                            val site = atts.randomOrNull()?.site
                                            atts.sumOf { it.total }.takeIf { it > 0.0 }
                                                ?.let { att ->
                                                    val salary =
                                                        atts.sumOf { a ->
                                                            (a.fulls + a.halfs).sumOf {
                                                                BigDecimal.valueOf(
                                                                    (
                                                                        it.salary?.toDouble()
                                                                            ?: 0.0
                                                                    ) * it.factor,
                                                                )
                                                            }
                                                        }.takeIf { it != BigDecimal.ZERO }
                                                    TargetAttendance(
                                                        name = site?.name,
                                                        att = att,
                                                        salary = salary,
                                                    )
                                                }
                                        }
                                }
                            val pdfEmployeesDeferred =
                                async(Dispatchers.Default) {
                                    flattenAtts.flatMap { att ->
                                        att.fulls + att.halfs
                                    }.groupBy { it.id }.mapNotNull { (_, employees) ->
                                        employees.sumOf { it.factor }.takeIf { it > 0 }
                                            ?.let { att ->
                                                TargetAttendance(
                                                    name = employees.randomOrNull()?.name,
                                                    att = att,
                                                    salary =
                                                        employees.sumOf {
                                                            BigDecimal.valueOf(
                                                                (
                                                                    it.salary?.toDouble()
                                                                        ?: 0.0
                                                                ) * it.factor,
                                                            )
                                                        }.takeIf { it != BigDecimal.ZERO },
                                                )
                                            }
                                    }
                                }

                            val pdfSites = pdfSitesDeferred.await()
                            val pdfEmployees = pdfEmployeesDeferred.await()

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
                                                draws.take(i).sumOf {
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
                            if (attAlls.isEmpty()) {
                                draw(
                                    Draw(
                                        text = "(無資料)",
                                        paint = paint(20f),
                                    ),
                                )
                            } else {
                                val contentPaint = paint(16f)
                                val targets = { title: String, targets: List<TargetAttendance> ->
                                    draw(
                                        Draw(
                                            text = title,
                                            paint =
                                                paint(20f).typeface(Typeface.DEFAULT_BOLD),
                                        ),
                                    )
                                    targets.forEach { t ->
                                        val name = t.name.orDeleted
                                        val maxName =
                                            targets.maxBy { (it.name.orDeleted).length }.name.orDeleted
                                        val maxAtt =
                                            targets.maxBy { AttendanceNumFormat(it.att).length }.att
                                        draws(
                                            listOf(
                                                Draw(
                                                    text = name,
                                                    paint = contentPaint,
                                                ),
                                                Draw(
                                                    text = "(${AttendanceNumFormat(t.att)})",
                                                    paint = contentPaint,
                                                    x = contentPaint.measureText("$maxName "),
                                                ),
                                                Draw(
                                                    text = "$${
                                                        NumberFormat(
                                                            t.salary,
                                                            decimalCount = 0,
                                                            invalidText = DASH,
                                                        )
                                                    }",
                                                    paint = contentPaint,
                                                    x =
                                                        contentPaint.measureText(
                                                            "$maxName (${
                                                                AttendanceNumFormat(
                                                                    maxAtt,
                                                                )
                                                            }) ",
                                                        ),
                                                ),
                                            ),
                                        )
                                    }
                                }
                                val salary =
                                    pdfEmployees.sumOf {
                                        it.salary ?: BigDecimal.ZERO
                                    }.takeIf { it != BigDecimal.ZERO }
                                val allAtt =
                                    AttendanceNumFormat(
                                        number = pdfEmployees.sumOf { it.att }.takeIf { it > 0.0 },
                                        invalidText = DASH,
                                    )
                                val allSalary =
                                    NumberFormat(salary, decimalCount = 0, invalidText = DASH)
                                draw(
                                    Draw(
                                        text = "工數總計 $allAtt ／薪資總計 $$allSalary",
                                        paint = paint(20f).typeface(Typeface.DEFAULT_BOLD),
                                    ),
                                )
                                newLine(20f)
                                targets("員工", pdfEmployees)
                                newLine(20f)
                                targets("工地", pdfSites)
                                newLine(20f)
                                draws(
                                    listOfNotNull(
                                        Draw(
                                            text = "日明細",
                                            paint =
                                                paint(20f)
                                                    .typeface(Typeface.DEFAULT_BOLD),
                                        ),
                                        Draw(
                                            text = "    **藍字半工 ",
                                            paint = paint(14f, BLUE),
                                        ),
                                        Draw(
                                            text = "    **灰字備註",
                                            paint = paint(14f, Color.GRAY),
                                        ).takeIf { request.includeRemark },
                                    ),
                                )
                                newLine(2f)
                                attAlls.forEachIndexed { i, attAll ->
                                    val year = today(attAll.dayMillis).year
                                    if (i == 0 || year != today(attAlls[i - 1].dayMillis).year) {
                                        draw(
                                            Draw(
                                                text = "${year}年",
                                                paint =
                                                    paint(18f, Color.DKGRAY)
                                                        .typeface(Typeface.DEFAULT_BOLD),
                                            ),
                                        )
                                    }
                                    val mmdd =
                                        TimeConverter.format(
                                            timeMillis = attAll.dayMillis,
                                            pattern = "MM/dd",
                                        )
                                    val all =
                                        AttendanceNumFormat(attAll.attendances.sumOf { it.total })
                                    val date = "$mmdd${ChineseDayOfWeek(attAll.dayMillis)}"
                                    val w = contentPaint.measureText("09/09六 (12.5) ")
                                    attAll.attendances.forEachIndexed { j, att ->
                                        val siteName =
                                            "${att.site.name.orDeleted} (${AttendanceNumFormat(att.total)}) "
                                        val sitePaint = paint(16f).typeface(Typeface.DEFAULT_BOLD)
                                        draws(
                                            listOfNotNull(
                                                Draw(
                                                    text = "$date ($all)",
                                                    paint = contentPaint,
                                                ).takeIf { j == 0 },
                                                Draw(
                                                    text = siteName,
                                                    paint = sitePaint,
                                                    x = w,
                                                ),
                                                Draw(
                                                    text = att.remark.orEmpty().replace("\n", "・"),
                                                    paint = paint(14f, Color.GRAY),
                                                    x = w + sitePaint.measureText(siteName),
                                                ).takeIf { att.remark != null },
                                            ),
                                        )
                                        val fulls =
                                            att.fulls.joinToString("／") { it.name.orDeleted }
                                                .map { it.toString() to true }
                                        val halfs =
                                            att.halfs.joinToString("／") { it.name.orDeleted }
                                                .map { it.toString() to false }
                                        val separator =
                                            if (att.fulls.isNotEmpty() && att.halfs.isNotEmpty()) {
                                                listOf("／" to true)
                                            } else {
                                                emptyList()
                                            }
                                        (fulls + separator + halfs).chunked(27)
                                            .forEach { item ->
                                                val full =
                                                    item.joinToString("") { p ->
                                                        p.first.takeIf { p.second }.orEmpty()
                                                    }
                                                val half =
                                                    item.joinToString("") { p ->
                                                        p.first.takeIf { !p.second }.orEmpty()
                                                    }
                                                val fullW =
                                                    contentPaint.measureText(full)
                                                        .takeIf { full.isNotEmpty() } ?: 0f
                                                draws(
                                                    listOfNotNull(
                                                        Draw(
                                                            text = full,
                                                            paint = contentPaint,
                                                            x = w,
                                                        ).takeIf { full.isNotEmpty() },
                                                        Draw(
                                                            text = half,
                                                            paint = paint(16f, BLUE),
                                                            x = w + fullW,
                                                        ).takeIf { half.isNotEmpty() },
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
                    .flowOn(Dispatchers.IO)
            }
                .filterNotNull()
                .flowOn(Dispatchers.Default)
                .collectLatest {
                    _pdfBundle.value = it
                }
        }
    }

    fun startDownload(
        startMillis: Long,
        endMillis: Long,
        includeRemark: Boolean,
    ) {
        loading()
        requestTrigger.value =
            RequestTrigger(
                startMillis = startMillis,
                endMillis = endMillis,
                includeRemark = includeRemark,
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

    fun toggleIncludeRemark() {
        update { it.copy(includeRemark = !it.includeRemark) }
    }

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
