package com.example.maintainingcar.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import com.example.maintainingcar.CarApplication
import com.example.maintainingcar.R
import com.example.maintainingcar.db.AppDatabase
import com.example.maintainingcar.db.CarDao
import com.example.maintainingcar.db.InExInfo
import kotlinx.android.synthetic.main.fragment_add.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class AddFragment : Fragment(), DatePickerDialog.OnDateSetListener , TimePickerDialog.OnTimeSetListener{
    private val addTag = "Car_Add"
    private var type = 0
    private var exType = 0
    private var inType = 0
    private val calendar =  Calendar.getInstance(Locale.CHINA)
    private val format = SimpleDateFormat("yyyy/MM/dd HH:mm")
    private lateinit var carDao: CarDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        carDao = AppDatabase.getDatabase(CarApplication.context).carDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val date = format.format(Date())
        tvDate.text = date.substring(0, 10)
        tvTime.text = date.substring(date.length-6, date.length)
        btSelectDate.setOnClickListener {
            showDatePickerDialog()
        }
        btSelectTime.setOnClickListener {
            showTimePickerDialog()
        }
        sp_type.onItemSelectedListener = TypeItemSelectListener()
        sp_ex_type.onItemSelectedListener = ExTypeItemSelectListener()
        sp_in_type.onItemSelectedListener = InTypeItemSelectListener()

        thread {
            val list = carDao.loadAllInfo()
            for (inExInfo in list) {
                Log.d(addTag, inExInfo.toString())
            }
        }

    }

    private fun showDatePickerDialog() {
        activity?.let {
            DatePickerDialog(
                it, this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }
    private fun showTimePickerDialog() {
        activity?.let {
            TimePickerDialog(
                it, this,
                calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), true).show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        Log.d(addTag, "year=$year-----month=$month-----day=$dayOfMonth")
        var strMonth = "${month+1}"
        if (strMonth.length == 1) {
            strMonth = "0$strMonth"
        }
        var strDay = "$dayOfMonth"
        if (strDay.length == 1) {
            strDay = "0$strDay"
        }
        tvDate.text = "$year/$strMonth/$strDay"
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        tvTime.text = "$hourOfDay:$minute"
    }

    fun insert() {
        var subType = 0
        var money = 0.0
        if (type == 0) {
            subType = inType
            money = etIn.text.toString().toDouble()
        } else {
            subType = exType
            money = etEx.text.toString().toDouble()
        }
        val format = SimpleDateFormat("yyyy/MM/dd HH:mm")
        val date = format.parse("${tvDate.text.toString()} ${tvTime.text.toString()}").time
        thread {
            var cardIndex = carDao.queryMaxCardIndex()
            Log.d(addTag, "maxIndex = $cardIndex")
            if (type == 1 && subType == 0) {
                //加油
                ++cardIndex
            }
            val inExInfo = InExInfo(type, subType, money, date, cardIndex)
            carDao.insertInExInfo(inExInfo)
        }
        resetPageData()
    }

    private fun resetPageData(){
        sp_type.setSelection(0)
        sp_ex_type.setSelection(0)
        sp_in_type.setSelection(0)
        etEx.setText("")
        etIn.setText("")
        val date = format.format(Date())
        tvDate.text = date.substring(0, 10)
        tvTime.text = date.substring(date.length-6, date.length)
    }

    inner class TypeItemSelectListener:AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
            Log.d(addTag, "nothing has be selected!")
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            Log.d(addTag, "select position is $position")
            type = position
            if (type == 0) {
                llIn.visibility = View.VISIBLE
                llEx.visibility = View.GONE
            } else {
                llIn.visibility = View.GONE
                llEx.visibility = View.VISIBLE
            }
        }
    }

    inner class ExTypeItemSelectListener:AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
            Log.d(addTag, "Ex nothing has be selected!")
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            Log.d(addTag, "Ex select position is $position")
            exType = position
        }
    }

    inner class InTypeItemSelectListener:AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
            Log.d(addTag, "In nothing has be selected!")
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            Log.d(addTag, "In select position is $position")
            inType = position
        }
    }
}

