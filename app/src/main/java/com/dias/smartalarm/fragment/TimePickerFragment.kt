package com.dias.smartalarm.fragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private var dialogListener: TimeDialogListener? = null

    //    dipanggil ketika dialog dipasang
    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogListener = context as TimeDialogListener
    }

    //    dipanggil ketika dialog dilepas
    override fun onDetach() {
        super.onDetach()
        if (dialogListener != null) dialogListener = null
    }

    //    dipanggil ketika sedang membuat dialog
//    di function ini ketika onCreateDialog dipanggil maka akan diset secara default jam dan menit sekarang
//    di dialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return TimePickerDialog(activity as Context, this, hourOfDay, minute, true)
    }

    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
        dialogListener?.onDialogTimeSet(tag, hourOfDay, minute)
    }

    interface TimeDialogListener {
        fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int)
    }
}