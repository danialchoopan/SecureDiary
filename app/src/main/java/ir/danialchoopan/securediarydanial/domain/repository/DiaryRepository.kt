package ir.danialchoopan.securediarydanial.domain.repository

import ir.danialchoopan.securediarydanial.domain.model.DiaryEntry
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {
    fun getAllEntries(): Flow<List<DiaryEntry>>
    suspend fun getEntryById(id: Long): DiaryEntry?
    suspend fun saveEntry(entry: DiaryEntry)
    suspend fun deleteEntry(id: Long)
    fun searchEntries(query: String): Flow<List<DiaryEntry>>
}
