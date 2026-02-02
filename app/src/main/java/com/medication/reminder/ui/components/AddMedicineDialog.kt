package com.medication.reminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.medication.reminder.data.entity.Medicine
import java.text.SimpleDateFormat
import java.util.*

/**
 * 添加/编辑药品对话框
 */
@Composable
fun AddMedicineDialog(
    medicine: Medicine?,
    onDismiss: () -> Unit,
    onConfirm: (Medicine) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val today = remember { System.currentTimeMillis() }
    
    var name by remember(medicine) { mutableStateOf(medicine?.name ?: "") }
    var purpose by remember(medicine) { mutableStateOf(medicine?.purpose ?: "") }
    var dosage by remember(medicine) { mutableStateOf(medicine?.dosage ?: "") }
    var timesPerDay by remember(medicine) { mutableStateOf(medicine?.timesPerDay?.toString() ?: "1") }
    var reminderTimes by remember(medicine) { mutableStateOf(medicine?.reminderTimes ?: "08:00") }
    var startDate by remember(medicine) {
        mutableStateOf(
            medicine?.startDate?.let { dateFormat.format(Date(it)) }
                ?: dateFormat.format(Date(today))
        )
    }
    var endDate by remember(medicine) {
        mutableStateOf(
            medicine?.endDate?.let { dateFormat.format(Date(it)) } ?: ""
        )
    }
    
    val scrollState = rememberScrollState()
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = if (medicine == null) "添加药品" else "编辑药品",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("药品名称") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    label = { Text("用途") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("如：降压、降糖") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("每次剂量") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("如：1片、10ml") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = timesPerDay,
                    onValueChange = { timesPerDay = it.filter { c -> c.isDigit() }.take(2) },
                    label = { Text("每日次数") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = reminderTimes,
                    onValueChange = { reminderTimes = it },
                    label = { Text("提醒时间") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("多个时间用逗号分隔，如：08:00,12:00,18:00") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("开始日期") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("yyyy-MM-dd") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("结束日期（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("留空表示长期服用") }
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val start = try {
                                dateFormat.parse(startDate)?.time ?: today
                            } catch (_: Exception) { today }
                            val end = if (endDate.isBlank()) null
                            else try {
                                dateFormat.parse(endDate)?.time
                            } catch (_: Exception) { null }
                            onConfirm(
                                Medicine(
                                    id = medicine?.id ?: 0L,
                                    name = name.trim(),
                                    purpose = purpose.trim(),
                                    dosage = dosage.trim(),
                                    timesPerDay = timesPerDay.toIntOrNull()?.coerceIn(1, 24) ?: 1,
                                    reminderTimes = reminderTimes.trim(),
                                    startDate = start,
                                    endDate = end,
                                    isActive = true
                                )
                            )
                        }
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    }
}
