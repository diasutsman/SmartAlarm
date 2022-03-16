package com.dias.smartalarm

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dias.smartalarm.data.Alarm
import com.dias.smartalarm.data.local.AlarmDB
import com.dias.smartalarm.data.local.AlarmDao
import com.dias.smartalarm.databinding.ActivityRepeatingAlarmBinding
import com.dias.smartalarm.fragment.TimePickerFragment
import com.dias.smartalarm.helper.TAG_TIME_PICKER
import com.dias.smartalarm.helper.timeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepeatingAlarmActivity : AppCompatActivity(), TimePickerFragment.TimeDialogListener {

    private var _binding: ActivityRepeatingAlarmBinding? = null
    private val binding get() = _binding as ActivityRepeatingAlarmBinding

    private var _alarmService: AlarmService? = null
    private val alarmService get() = _alarmService as AlarmService

    /**
     * if you are forgot,
     * dao is an interface with method that can access database with those methods
     * dao is implemented by 'kotlin-kapt' so you don't have to implement it yourself
     */

    private var _alarmDao: AlarmDao? = null
    private val alarmDao get() = _alarmDao as AlarmDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRepeatingAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get the database by passing app's context and get the dao

        _alarmDao = AlarmDB.getDatabase(applicationContext).alarmDao()

        _alarmService = AlarmService()

        initView()

    }

    private fun initView() {
        binding.apply {
            btnSetTimeRepeating.setOnClickListener {
                val timePickerFragment = TimePickerFragment()
//               will show dialog depend on tag
                timePickerFragment.show(supportFragmentManager, TAG_TIME_PICKER)
            }
            btnCancelSetRepeatingAlarm.setOnClickListener {
                finish()
            }
            btnAddSetRepeatingAlarm.setOnClickListener {
                val time = tvRepeatingTime.text.toString()
                val message = etNoteRepeating.text.toString()
                if (time != "Time") {
                    alarmService.setRepeatingAlarm(applicationContext, AlarmService.TYPE_REPEATING, time, message)
                    CoroutineScope(Dispatchers.IO).launch {
                        alarmDao.addAlarm(Alarm(
                            0,
                            "Repeating Alarm",
                            time,
                            if (message == "") "Your Repeating Alarm" else message,
                            AlarmService.TYPE_REPEATING
                        ))
                    }

                    finish()
                } else {
                    Toast.makeText(this@RepeatingAlarmActivity,
                        "Please set your time of alarm",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        binding.tvRepeatingTime.text = timeFormatter(hourOfDay, minute)
    }
}