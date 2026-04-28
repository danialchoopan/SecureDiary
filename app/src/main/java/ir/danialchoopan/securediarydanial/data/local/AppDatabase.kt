package ir.danialchoopan.securediarydanial.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.danialchoopan.securediarydanial.data.local.dao.DiaryDao
import ir.danialchoopan.securediarydanial.data.local.entity.DiaryEntryEntity

@Database(entities = [DiaryEntryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
}
