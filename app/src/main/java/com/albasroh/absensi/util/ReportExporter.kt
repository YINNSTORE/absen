package com.albasroh.absensi.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import com.albasroh.absensi.data.local.entity.Attendance
import com.albasroh.absensi.data.local.entity.Student
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportExporter {
    private fun stamp(): String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

    fun exportExcel(context: Context, attendance: List<Attendance>, students: List<Student>): String {
        val names = students.associateBy { it.id }
        val rows = attendance.joinToString("") { a ->
            val student = names[a.studentId]
            "<Row><Cell><Data ss:Type=\"String\">${esc(student?.nama ?: "-")}</Data></Cell>" +
                "<Cell><Data ss:Type=\"String\">${esc(student?.nis ?: "-")}</Data></Cell>" +
                "<Cell><Data ss:Type=\"String\">${esc(student?.kelas ?: "-")}</Data></Cell>" +
                "<Cell><Data ss:Type=\"String\">${esc(a.tanggal)}</Data></Cell>" +
                "<Cell><Data ss:Type=\"String\">${esc(a.waktu)}</Data></Cell>" +
                "<Cell><Data ss:Type=\"String\">${esc(a.status)}</Data></Cell>" +
                "<Cell><Data ss:Type=\"String\">${esc(a.method)}</Data></Cell></Row>"
        }
        val xml = """
            <?xml version="1.0"?>
            <?mso-application progid="Excel.Sheet"?>
            <Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet"
             xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet">
              <Worksheet ss:Name="Absensi">
                <Table>
                  <Row>
                    <Cell><Data ss:Type="String">Nama Murid</Data></Cell>
                    <Cell><Data ss:Type="String">NIS</Data></Cell>
                    <Cell><Data ss:Type="String">Kelas</Data></Cell>
                    <Cell><Data ss:Type="String">Tanggal</Data></Cell>
                    <Cell><Data ss:Type="String">Waktu</Data></Cell>
                    <Cell><Data ss:Type="String">Status</Data></Cell>
                    <Cell><Data ss:Type="String">Metode</Data></Cell>
                  </Row>$rows
                </Table>
              </Worksheet>
            </Workbook>
        """.trimIndent()
        return save(context, "Absensi_${stamp()}.xls", "application/vnd.ms-excel", xml.toByteArray())
    }

    fun exportPdf(context: Context, attendance: List<Attendance>, students: List<Student>): String {
        val names = students.associateBy { it.id }
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 11f }
        val title = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 20f; isFakeBoldText = true }
        val header = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 10f; isFakeBoldText = true }
        var pageNumber = 1
        var page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas = page.canvas
        var y = 42f
        canvas.drawText("MTs-Al Basroh", 36f, y, title)
        y += 20f
        canvas.drawText("Laporan Absensi • ${Clock.date()}", 36f, y, paint)
        y += 30f
        canvas.drawText("Nama", 36f, y, header)
        canvas.drawText("Kelas", 180f, y, header)
        canvas.drawText("Tanggal", 250f, y, header)
        canvas.drawText("Waktu", 330f, y, header)
        canvas.drawText("Status", 390f, y, header)
        y += 18f

        attendance.forEach { a ->
            if (y > 800f) {
                document.finishPage(page)
                pageNumber++
                page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                canvas = page.canvas
                y = 42f
            }
            val student = names[a.studentId]
            canvas.drawText((student?.nama ?: "-").take(22), 36f, y, paint)
            canvas.drawText((student?.kelas ?: "-").take(10), 180f, y, paint)
            canvas.drawText(a.tanggal, 250f, y, paint)
            canvas.drawText(a.waktu, 330f, y, paint)
            canvas.drawText(a.status, 390f, y, paint)
            y += 18f
        }
        document.finishPage(page)
        val bytes = java.io.ByteArrayOutputStream().use { out -> document.writeTo(out); out.toByteArray() }
        document.close()
        return save(context, "Absensi_${stamp()}.pdf", "application/pdf", bytes)
    }

    private fun save(context: Context, name: String, mime: String, data: ByteArray): String {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: error("Penyimpanan tidak tersedia")
            val file = java.io.File(dir, name)
            file.outputStream().use { it.write(data) }
            return file.absolutePath
        }

        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, name)
            put(MediaStore.Downloads.MIME_TYPE, mime)
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Absensi-MTs-Al-Basroh")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: error("Tidak dapat membuat file laporan")
        try {
            context.contentResolver.openOutputStream(uri)?.use { it.write(data) }
                ?: error("Tidak dapat membuka file laporan")
            val ready = ContentValues().apply { put(MediaStore.Downloads.IS_PENDING, 0) }
            context.contentResolver.update(uri, ready, null, null)
        } catch (e: Exception) {
            context.contentResolver.delete(uri, null, null)
            throw e
        }
        return name
    }

    private fun esc(value: String): String = value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")
}
