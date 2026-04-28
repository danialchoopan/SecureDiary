package ir.danialchoopan.securediarydanial.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val encryptedTitle: String,
    val encryptedContent: String,
    val encryptedPhotoPath: String? = null,
    val encryptedLatitude: String? = null,
    val encryptedLongitude: String? = null,
    val timestamp: Long
)
