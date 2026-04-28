package ir.danialchoopan.securediarydanial.data.repository

import ir.danialchoopan.securediarydanial.data.local.dao.DiaryDao
import ir.danialchoopan.securediarydanial.data.local.entity.DiaryEntryEntity
import ir.danialchoopan.securediarydanial.domain.model.DiaryEntry
import ir.danialchoopan.securediarydanial.domain.repository.DiaryRepository
import ir.danialchoopan.securediarydanial.domain.repository.EncryptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DiaryRepositoryImpl(
    private val diaryDao: DiaryDao,
    private val encryptionRepository: EncryptionRepository
) : DiaryRepository {

    override fun getAllEntries(): Flow<List<DiaryEntry>> {
        return diaryDao.getAllEntries().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getEntryById(id: Long): DiaryEntry? {
        return diaryDao.getEntryById(id)?.toDomain()
    }

    override suspend fun saveEntry(entry: DiaryEntry) {
        val entity = DiaryEntryEntity(
            id = entry.id,
            encryptedTitle = encryptionRepository.encryptText(entry.title),
            encryptedContent = encryptionRepository.encryptText(entry.content),
            encryptedPhotoPath = entry.photoPath?.let { encryptionRepository.encryptText(it) },
            encryptedLatitude = entry.latitude?.let { encryptionRepository.encryptText(it.toString()) },
            encryptedLongitude = entry.longitude?.let { encryptionRepository.encryptText(it.toString()) },
            timestamp = entry.timestamp
        )
        if (entry.id == 0L) {
            diaryDao.insertEntry(entity)
        } else {
            diaryDao.updateEntry(entity)
        }
    }

    override suspend fun deleteEntry(id: Long) {
        diaryDao.deleteEntryById(id)
    }

    override fun searchEntries(query: String): Flow<List<DiaryEntry>> {
        // Since data is encrypted in DB, search must happen after decryption
        // Or we use a search-friendly encryption/hashing (out of scope for now)
        // For this app, we fetch all and filter in memory as a simple implementation
        return diaryDao.getAllEntries().map { entities ->
            entities.map { it.toDomain() }.filter {
                it.title.contains(query, ignoreCase = true) || 
                it.content.contains(query, ignoreCase = true)
            }
        }
    }

    private fun DiaryEntryEntity.toDomain(): DiaryEntry {
        return DiaryEntry(
            id = id,
            title = encryptionRepository.decryptText(encryptedTitle),
            content = encryptionRepository.decryptText(encryptedContent),
            photoPath = encryptedPhotoPath?.let { encryptionRepository.decryptText(it) },
            latitude = encryptedLatitude?.let { encryptionRepository.decryptText(it).toDoubleOrNull() },
            longitude = encryptedLongitude?.let { encryptionRepository.decryptText(it).toDoubleOrNull() },
            timestamp = timestamp
        )
    }
}
