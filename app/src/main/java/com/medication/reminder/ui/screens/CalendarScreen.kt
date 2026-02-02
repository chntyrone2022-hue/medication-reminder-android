package com.medication.reminder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.medication.reminder.data.entity.MedicationRecord
import com.medication.reminder.ui.viewmodel.MedicineViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日历看板界面
 */
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    viewModel: MedicineViewModel
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val records by viewModel.recordsForSelectedDate.collectAsState()
    val monthStart by viewModel.monthStart.collectAsState()
    val monthRecords by viewModel.monthRecords.collectAsState()
    val medicines by viewModel.medicines.collectAsState()
    
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val monthTitle = remember(monthStart) {
        val parts = monthStart.split("-")
        if (parts.size >= 2) "${parts[0]}年${parts[1].toIntOrNull() ?: 0}月" else monthStart
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // 月份导航
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.goToPreviousMonth() }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "上月")
            }
            Text(
                text = monthTitle,
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = { viewModel.goToNextMonth() }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "下月")
            }
        }
        
        // 星期标题
        val weekHeaders = listOf("日", "一", "二", "三", "四", "五", "六")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekHeaders.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // 日历格子：根据当月第一天是星期几算出前面空白，再填日期
        val daySummaries = remember(monthRecords) {
            monthRecords.groupBy { it.date }.mapValues { (_, list) ->
                val total = list.size
                val taken = list.count { it.isTaken }
                DaySummary(total = total, taken = taken)
            }
        }
        
        val cal = remember { Calendar.getInstance(Locale.getDefault()) }
        val firstDayOfMonth = remember(monthStart) {
            cal.time = dateFormat.parse(monthStart)!!
            cal.get(Calendar.DAY_OF_WEEK) - 1 // 0=周日
        }
        val daysInMonth = remember(monthStart) {
            cal.time = dateFormat.parse(monthStart)!!
            cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        val monthStartStr = monthStart
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 280.dp)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(firstDayOfMonth) { _ ->
                Box(Modifier.size(36.dp))
            }
            items(daysInMonth) { dayIndex ->
                val day = dayIndex + 1
                val dateStr = run {
                    cal.time = dateFormat.parse(monthStartStr)!!
                    cal.set(Calendar.DAY_OF_MONTH, day)
                    dateFormat.format(cal.time)
                }
                val summary = daySummaries[dateStr]
                val isSelected = dateStr == selectedDate
                val isToday = dateStr == dateFormat.format(Date())
                
                DayCell(
                    day = day,
                    summary = summary,
                    isSelected = isSelected,
                    isToday = isToday,
                    onClick = { viewModel.selectDate(dateStr) }
                )
            }
        }
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        // 选中日期的用药记录
        Text(
            text = "当日用药 · $selectedDate",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "当日无用药计划",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(records, key = { it.id }) { record ->
                    val medicine = medicines.find { it.id == record.medicineId }
                    RecordItem(
                        record = record,
                        medicineName = medicine?.name ?: "未知药品",
                        dosage = medicine?.dosage ?: "",
                        onToggleTaken = { viewModel.updateRecordTakenStatus(record.id, !record.isTaken) }
                    )
                }
            }
        }
    }
}

private data class DaySummary(val total: Int, val taken: Int)

@Composable
private fun DayCell(
    day: Int,
    summary: DaySummary?,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isToday -> MaterialTheme.colorScheme.secondaryContainer
        else -> androidx.compose.ui.graphics.Color.Transparent
    }
    val contentColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        isToday -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Column(
        modifier = Modifier
            .size(36.dp)
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor
        )
        summary?.let { s ->
            if (s.total > 0) {
                val color = if (s.taken == s.total) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
                Text(
                    text = "${s.taken}/${s.total}",
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun RecordItem(
    record: MedicationRecord,
    medicineName: String,
    dosage: String,
    onToggleTaken: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (record.isTaken) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleTaken) {
                Icon(
                    imageVector = if (record.isTaken) Icons.Default.CheckCircle else Icons.Outlined.Cancel,
                    contentDescription = if (record.isTaken) "已服用" else "未服用",
                    tint = if (record.isTaken) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicineName,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "${record.reminderTime} · $dosage",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = if (record.isTaken) "已服用" else "未服用",
                style = MaterialTheme.typography.labelSmall,
                color = if (record.isTaken) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}
