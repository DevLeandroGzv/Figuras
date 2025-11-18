package com.govele.figuras.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.govele.figuras.data.local.entity.FiguraEntity
import com.govele.figuras.data.local.dao.FiguraDao
import com.govele.figuras.utils.Convertidores

@Database(
    entities = [FiguraEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Convertidores::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun figuraDao(): FiguraDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "figuras_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }
}