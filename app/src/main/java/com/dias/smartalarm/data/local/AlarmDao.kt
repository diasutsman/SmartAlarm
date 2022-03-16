package com.dias.smartalarm.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dias.smartalarm.data.Alarm

@Dao
interface AlarmDao {
    // to get all alarm from alarms
    @Query("SELECT * FROM alarms")
    fun getAllAlarms(): LiveData<List<Alarm>>

    // to add alarm to alarms
    @Insert
    fun addAlarm(alarm: Alarm)

    //to delete alarm from alarms
    @Delete
    fun deleteAlarm(alarm: Alarm)
}