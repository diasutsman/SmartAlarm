package com.dias.smartalarm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dias.smartalarm.adapter.AlarmAdapter
import com.dias.smartalarm.data.Alarm
import com.dias.smartalarm.data.local.AlarmDB
import com.dias.smartalarm.data.local.AlarmDao
import com.dias.smartalarm.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private var _alarmService: AlarmService? = null
    private val alarmService get() = _alarmService as AlarmService

    /**
     * In case if you forgot,
     * dao is an interface with method that can access database with those methods
     * dao is implemented by 'kotlin-kapt' so you don't have to implement it yourself
     */

    private var _alarmDao: AlarmDao? = null
    private val alarmDao get() = _alarmDao as AlarmDao

    private var alarmAdapter: AlarmAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // get the database by passing app's context and get the dao
        _alarmDao = AlarmDB.getDatabase(applicationContext).alarmDao()
        _alarmService = AlarmService()
        alarmAdapter = AlarmAdapter(arrayListOf(), this@MainActivity)
        initView()
        setupRv()
    }

    private fun initView() {
        binding.apply {
            cvSetOneTimeAlarm.setOnClickListener {
                startActivity(Intent(applicationContext, OneTimeAlarmActivity::class.java))
            }
            cvSetRepeatingAlarm.setOnClickListener {
                startActivity(Intent(this@MainActivity, RepeatingAlarmActivity::class.java))
            }

        }

    }

    private fun setupRv() {
        // use observe with LiveData
        alarmDao.getAllAlarms().observe(this) { alarm ->
            val alarm2 = alarm as ArrayList<Alarm>
            alarm2.reverse()
            alarmAdapter?.setData(alarm2)
        }
//        CoroutineScope(Dispatchers.IO).launch {
//            // access room database and retrieve some data
//            val alarms = alarmDao.getAllAlarms() as ArrayList<Alarm>
//            withContext(Dispatchers.Main) {
//                // update UI with data retrieved
//                // if you wanted to remove warning in AlarmAdapter.kt by removing the invoke of notifyDataSetChanged()
////                binding.rvReminderAlarm.adapter = AlarmAdapter(alarms, this@MainActivity)
//                alarms.reverse()
//                alarmAdapter?.setData(alarms)
//            }
////            Log.i("onResume()", "onResume(): ${}")
//        }
        binding.rvReminderAlarm.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = alarmAdapter
            swipeToDelete(this)
        }
    }

    override fun onResume() {
        super.onResume()
        setupRv()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun swipeToDelete(recylerView: RecyclerView) {
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

//            TODO 3: hapus notifyItemRemoved
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedAlarm = alarmAdapter?.listAlarm?.get(viewHolder.adapterPosition) as Alarm
                // interacting with database should in main thread, so use Coroutine like this
                CoroutineScope(Dispatchers.IO).launch {
                    alarmDao.deleteAlarm(deletedAlarm)
                }
                alarmService.cancelAlarm(applicationContext, deletedAlarm.type as Int)
            }

        }).attachToRecyclerView(recylerView)
    }
}