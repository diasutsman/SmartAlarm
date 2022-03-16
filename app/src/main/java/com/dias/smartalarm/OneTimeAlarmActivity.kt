package com.dias.smartalarm

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dias.smartalarm.data.Alarm
import com.dias.smartalarm.data.local.AlarmDB
import com.dias.smartalarm.data.local.AlarmDao
import com.dias.smartalarm.databinding.ActivityOneTimeAlarmBinding
import com.dias.smartalarm.fragment.DatePickerFragment
import com.dias.smartalarm.fragment.TimePickerFragment
import com.dias.smartalarm.helper.TAG_TIME_PICKER
import com.dias.smartalarm.helper.timeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OneTimeAlarmActivity : AppCompatActivity(), DatePickerFragment.DateDialogListener,
    TimePickerFragment.TimeDialogListener {

    private var _binding: ActivityOneTimeAlarmBinding? = null
    private val binding get() = _binding as ActivityOneTimeAlarmBinding

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
        _binding = ActivityOneTimeAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get the database by passing app's context and get the dao
        _alarmDao = AlarmDB.getDatabase(applicationContext).alarmDao()

        _alarmService = AlarmService()

        initView()

    }

    private fun initView() {
        binding.apply {
            btnSetDateOneTime.setOnClickListener {
                val datePickerFragment = DatePickerFragment()
//               will show dialog depend on tag
                datePickerFragment.show(supportFragmentManager, "DatePickerDialog")
            }

            btnSetTimeOneTime.setOnClickListener {
                val timePickerFragment = TimePickerFragment()
                timePickerFragment.show(supportFragmentManager, TAG_TIME_PICKER)
            }
            // cancel set one time alarm button
            btnCancelSetOneTimeAlarm.setOnClickListener {
                finish()
            }
            // add set one time alarm button
            btnAddSetOneTimeAlarm.setOnClickListener {
                val date = tvOnceDate.text.toString()
                val time = tvOnceTime.text.toString()
                val message = edtNoteOneTime.text.toString()

                if (date != "Date" && time != "Time") {

                    val alarm = Alarm(
                        0,
                        date,
                        time,
                        if (message == "") "Your One Time Alarm" else message,
                        AlarmService.TYPE_ONE_TIME
                    )

                    alarmService.setOneTimeAlarm(
                        this@OneTimeAlarmActivity,
                        AlarmService.TYPE_ONE_TIME,
                        date,
                        time,
                        if (message == "") "Your One Time Alarm" else message
                    )


                    CoroutineScope(Dispatchers.IO).launch {
                        alarmDao.addAlarm(alarm)
                    }

                    finish()
                } else {
                    Toast.makeText(this@OneTimeAlarmActivity,
                        "Please set your Time and Date",
                        Toast.LENGTH_SHORT).show()
                }


            }
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
//      mengatur tanggal supaya sama dengan format yang diinginkan
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        binding.apply {
            tvOnceDate.text =
                dateFormat.format(calendar.time) // mengambil tanggal berdasarkan tanggal yang telah ditentukan
        }
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        binding.tvOnceTime.text = timeFormatter(hourOfDay, minute)
    }
}