package ir.danialchoopan.securediarydanial.di

import android.content.Context
import androidx.room.Room
import ir.danialchoopan.securediarydanial.data.local.AppDatabase
import ir.danialchoopan.securediarydanial.data.repository.DiaryRepositoryImpl
import ir.danialchoopan.securediarydanial.data.repository.EncryptionRepositoryImpl
import ir.danialchoopan.securediarydanial.data.repository.SettingsRepositoryImpl
import ir.danialchoopan.securediarydanial.domain.repository.DiaryRepository
import ir.danialchoopan.securediarydanial.domain.repository.EncryptionRepository
import ir.danialchoopan.securediarydanial.domain.repository.SettingsRepository

interface AppContainer {
    val diaryRepository: DiaryRepository
    val encryptionRepository: EncryptionRepository
    val settingsRepository: SettingsRepository
}

class AppContainerImpl(private val context: Context) : AppContainer {

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(context, AppDatabase::class.java, "secure_diary.db").build()
    }

    override val encryptionRepository: EncryptionRepository by lazy {
        EncryptionRepositoryImpl(context)
    }

    override val diaryRepository: DiaryRepository by lazy {
        DiaryRepositoryImpl(database.diaryDao(), encryptionRepository)
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(context)
    }
}
