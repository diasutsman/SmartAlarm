package com.dias.smartalarm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dias.smartalarm.R
import com.dias.smartalarm.data.Alarm
import com.dias.smartalarm.databinding.ItemRowReminderAlarmBinding

class AlarmAdapter(val listAlarm: ArrayList<Alarm>, val context: Context) :
    RecyclerView.Adapter<AlarmAdapter.MyViewHolder>() {

//    private var _alarmDao: AlarmDao? = null
//    private val alarmDao get() = _alarmDao as AlarmDao

    inner class MyViewHolder(val binding: ItemRowReminderAlarmBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemRowReminderAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = listAlarm.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val alarm = listAlarm[position]
        holder.binding.apply {
            itemDateAlarm.text = alarm.date
            itemTimeAlarm.text = alarm.time
            itemNoteAlarm.text = alarm.message
            itemImgOneTime.setImageResource(if (alarm.date == "Repeating Alarm") R.drawable.ic_repeating else R.drawable.ic_one_time)
        }

    }

//  TODO 2 : perbaiki kode
    fun setData(list: ArrayList<Alarm>) {
        val alarmDiffUtil = AlarmDiffUtil(listAlarm, list)
        val alarmDiffUtilResult = DiffUtil.calculateDiff(alarmDiffUtil)
        listAlarm.clear()
        listAlarm.addAll(list)
        alarmDiffUtilResult.dispatchUpdatesTo(this)
//        notifyDataSetChanged()
    }

}