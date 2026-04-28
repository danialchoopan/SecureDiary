package ir.danialchoopan.securediarydanial.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.danialchoopan.securediarydanial.domain.model.DiaryEntry
import ir.danialchoopan.securediarydanial.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DiaryViewModel(private val diaryRepository: DiaryRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val entries: StateFlow<List<DiaryEntry>> = _searchQuery
        .debounce(300L)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                diaryRepository.getAllEntries()
            } else {
                diaryRepository.searchEntries(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun getEntryById(id: Long): Flow<DiaryEntry?> = flow {
        emit(diaryRepository.getEntryById(id))
    }

    fun saveEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            diaryRepository.saveEntry(entry)
        }
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            diaryRepository.deleteEntry(id)
        }
    }
}
