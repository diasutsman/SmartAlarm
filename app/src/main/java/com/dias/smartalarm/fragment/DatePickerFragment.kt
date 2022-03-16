package com.dias.smartalarm.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var dialogListener: DateDialogListener? = null

    //    dipanggil ketika dialog dipasang
    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogListener = context as DateDialogListener
    }

//    dipanggil ketika dialog dilepas
    override fun onDetach() {
        super.onDetach()
        if (dialogListener != null) dialogListener = null
    }

//    dipanggil ketika sedang membuat dialog
//    di function ini ketika onCreateDialog dipanggil maka akan diset secara default tanggal sekarang
//    di dialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(activity as Context, this, year, month, dayOfMonth)
    }
//  dipanggil ketika tanggal di tentukan / di set
    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dialogListener?.onDialogDateSet(tag, year, month, dayOfMonth)
    }
//     buat dipanggil di activity agar mendapat nilai inputan yang dipilih
    interface DateDialogListener {
        fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int)
    }
}