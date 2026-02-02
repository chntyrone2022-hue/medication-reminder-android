package com.medication.reminder.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.medication.reminder.data.repository.MedicineRepository
import com.medication.reminder.ui.screens.CalendarScreen
import com.medication.reminder.ui.screens.MedicineListScreen
import com.medication.reminder.ui.viewmodel.MedicineViewModel
import com.medication.reminder.ui.viewmodel.MedicineViewModelFactory

/**
 * 应用主导航
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationApp(
    repository: MedicineRepository,
    viewModel: MedicineViewModel = viewModel(factory = MedicineViewModelFactory(repository))
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("用药管家") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Medication, contentDescription = "药品") },
                    label = { Text("药品") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "日历") },
                    label = { Text("日历") }
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> MedicineListScreen(
                modifier = Modifier.padding(paddingValues),
                viewModel = viewModel
            )
            1 -> CalendarScreen(
                modifier = Modifier.padding(paddingValues),
                viewModel = viewModel
            )
        }
    }
}
