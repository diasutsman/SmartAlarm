package com.dias.smartalarm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dias.smartalarm.data.Alarm

@Database(entities = [Alarm::class], version = 4)
abstract class AlarmDB : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    companion object {
        @Volatile
        var instance: AlarmDB? = null
//        val LOCK = Any()
//        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
//            instance ?: buildDatabase(context)
//        }
        @JvmStatic
        fun getDatabase(context: Context) : AlarmDB {
            if (instance == null) {
                synchronized(AlarmDB::class.java) {
                    instance = Room.databaseBuilder(context, AlarmDB::class.java, "smart-alarm.db")
                        .fallbackToDestructiveMigration()
                        // to force data to load with the UI or main thread
//                        .allowMainThreadQueries()
                        .build()
                }
            }
            return instance as AlarmDB
        }

    }
}