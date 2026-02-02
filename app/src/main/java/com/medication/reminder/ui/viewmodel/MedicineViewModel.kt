package com.medication.reminder.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.medication.reminder.data.entity.Medicine
import com.medication.reminder.data.entity.MedicationRecord
import com.medication.reminder.data.repository.MedicineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 药品ViewModel
 */
class MedicineViewModel(private val repository: MedicineRepository) : ViewModel() {
    
    val medicines: StateFlow<List<Medicine>> = repository.getAllActiveMedicines()
        .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())
    
    private val _selectedDate = MutableStateFlow(getTodayDate())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()
    
    val recordsForSelectedDate: StateFlow<List<MedicationRecord>> = _selectedDate
        .flatMapLatest { date -> repository.getRecordsByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    private val _monthStart = MutableStateFlow(getCurrentMonthStart())
    private val _monthEnd = MutableStateFlow(getCurrentMonthEnd())
    val monthStart: StateFlow<String> = _monthStart.asStateFlow()
    val monthRecords: StateFlow<List<MedicationRecord>> = combine(_monthStart, _monthEnd) { s, e -> s to e }
        .flatMapLatest { (s, e) -> repository.getRecordsByDateRange(s, e) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    private val _uiState = MutableStateFlow<MedicineUiState>(MedicineUiState())
    val uiState: StateFlow<MedicineUiState> = _uiState.asStateFlow()
    
    fun selectDate(date: String) {
        _selectedDate.value = date
    }
    
    fun goToPreviousMonth() {
        val cal = Calendar.getInstance(Locale.getDefault())
        cal.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(_monthStart.value)!!
        cal.add(Calendar.MONTH, -1)
        _monthStart.value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        _monthEnd.value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }
    
    fun goToNextMonth() {
        val cal = Calendar.getInstance(Locale.getDefault())
        cal.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(_monthStart.value)!!
        cal.add(Calendar.MONTH, 1)
        _monthStart.value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        _monthEnd.value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }
    
    fun goToCurrentMonth() {
        _monthStart.value = getCurrentMonthStart()
        _monthEnd.value = getCurrentMonthEnd()
    }
    
    private fun getCurrentMonthStart(): String {
        val cal = Calendar.getInstance(Locale.getDefault())
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }
    
    private fun getCurrentMonthEnd(): String {
        val cal = Calendar.getInstance(Locale.getDefault())
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }
    
    fun loadMedicine(id: Long) {
        viewModelScope.launch {
            val medicine = repository.getMedicineById(id)
            _uiState.value = _uiState.value.copy(editingMedicine = medicine)
        }
    }
    
    fun saveMedicine(medicine: Medicine) {
        viewModelScope.launch {
            try {
                val saved = if (medicine.id == 0L) {
                    val id = repository.insertMedicine(medicine)
                    medicine.copy(id = id)
                } else {
                    repository.updateMedicine(medicine)
                    medicine
                }
                _uiState.value = _uiState.value.copy(
                    editingMedicine = null,
                    showAddDialog = false,
                    lastSavedMedicine = saved
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun clearLastSavedMedicine() {
        _uiState.value = _uiState.value.copy(lastSavedMedicine = null)
    }
    
    fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch {
            repository.deleteMedicine(medicine)
        }
    }
    
    fun updateRecordTakenStatus(recordId: Long, isTaken: Boolean) {
        viewModelScope.launch {
            repository.updateRecordTakenStatus(recordId, isTaken)
        }
    }
    
    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(
            showAddDialog = true,
            editingMedicine = null
        )
    }
    
    fun showEditDialog(medicine: Medicine) {
        _uiState.value = _uiState.value.copy(
            showAddDialog = true,
            editingMedicine = medicine
        )
    }
    
    fun hideAddDialog() {
        _uiState.value = _uiState.value.copy(
            showAddDialog = false,
            editingMedicine = null,
            error = null
        )
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}

data class MedicineUiState(
    val showAddDialog: Boolean = false,
    val editingMedicine: Medicine? = null,
    val error: String? = null,
    val lastSavedMedicine: Medicine? = null  // 刚保存的药品，用于设置提醒后清空
)

class MedicineViewModelFactory(private val repository: MedicineRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MedicineViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
