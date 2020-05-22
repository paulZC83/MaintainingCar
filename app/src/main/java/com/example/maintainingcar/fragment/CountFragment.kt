package com.example.maintainingcar.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import com.example.maintainingcar.CarApplication
import com.example.maintainingcar.R
import com.example.maintainingcar.db.AppDatabase
import com.example.maintainingcar.db.CarDao
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.android.synthetic.main.fragment_count.*
import kotlin.concurrent.thread

class CountFragment : Fragment(), CompoundButton.OnCheckedChangeListener {
    private val countTag = "Car_Count"
    private lateinit var carDao: CarDao
    private var jyChecked = false
    private var tcChecked = false
    private var glChecked = false
    private var byChecked = false
    private var wxChecked = false
    private var ddChecked = false
    private var sfcChecked = false
    private var allInChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        carDao = AppDatabase.getDatabase(CarApplication.context).carDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_count, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pieChart.setNoDataText("您还没有生成饼图数据！")
        btGenerate.setOnClickListener {
            generate()
        }
        cb_jy.setOnCheckedChangeListener(this)
        cb_tc.setOnCheckedChangeListener(this)
        cb_gl.setOnCheckedChangeListener(this)
        cb_by.setOnCheckedChangeListener(this)
        cb_wx.setOnCheckedChangeListener(this)
        cb_dd.setOnCheckedChangeListener(this)
        cb_sfc.setOnCheckedChangeListener(this)
        cb_all_in.setOnCheckedChangeListener(this)
    }

    private fun generate(){
        var jyMoney = 0.0
        var tcMoney = 0.0
        var glMoney = 0.0
        var byMoney = 0.0
        var wxMoney = 0.0
        var ddMoney = 0.0
        var sfcMoney = 0.0
        var allInMoney = 0.0

        var jyPercent = 0f
        var tcPercent = 0f
        var glPercent = 0f
        var byPercent = 0f
        var wxPercent = 0f
        var ddPercent = 0f
        var sfcPercent = 0f
        var allInPercent = 0f

        thread {
            if (jyChecked) {
                jyMoney = carDao.queryInfo(1, 0)
            }
            if (tcChecked) {
                tcMoney = carDao.queryInfo(1, 1)
            }
            if (glChecked) {
                glMoney = carDao.queryInfo(1, 2)
            }
            if (byChecked) {
                byMoney = carDao.queryInfo(1, 3)
            }
            if (wxChecked) {
                wxMoney = carDao.queryInfo(1, 4)
            }
            if (ddChecked) {
                ddMoney = carDao.queryInfo(0, 0)
            }
            if (sfcChecked) {
                sfcMoney = carDao.queryInfo(0, 1)
            }
            if (allInChecked) {
                allInMoney = carDao.queryInfoFromType(0)
            }

            val count = jyMoney.plus(tcMoney).plus(glMoney).plus(byMoney).plus(wxMoney).plus(ddMoney).plus(sfcMoney).plus(allInMoney)
            if (jyMoney > 0) {
                jyPercent = String.format("%.3f", jyMoney.div(count.toFloat())).toFloat()
            }
            if (tcMoney > 0) {
                tcPercent = String.format("%.3f", tcMoney.div(count.toFloat())).toFloat()
            }
            if (glMoney > 0) {
                glPercent = String.format("%.3f", glMoney.div(count.toFloat())).toFloat()
            }
            if (byMoney > 0) {
                byPercent = String.format("%.3f", byMoney.div(count.toFloat())).toFloat()
            }
            if (wxMoney > 0) {
                wxPercent = String.format("%.3f", wxMoney.div(count.toFloat())).toFloat()
            }
            if (ddMoney > 0) {
                ddPercent = String.format("%.3f", ddMoney.div(count.toFloat())).toFloat()
            }
            if (sfcMoney > 0) {
                sfcPercent = String.format("%.3f", sfcMoney.div(count.toFloat())).toFloat()
            }
            if (allInMoney > 0) {
                allInPercent = String.format("%.3f", allInMoney.div(count.toFloat())).toFloat()
            }
            val pieEntry1 = PieEntry(jyPercent.times(100),"加油")
            val pieEntry2 = PieEntry(tcPercent.times(100),"停车")
            val pieEntry3 = PieEntry(glPercent.times(100),"过路")
            val pieEntry4 = PieEntry(byPercent.times(100),"保养")
            val pieEntry5 = PieEntry(wxPercent.times(100),"维修")
            val pieEntry6 = PieEntry(ddPercent.times(100),"滴答")
            val pieEntry7 = PieEntry(sfcPercent.times(100),"顺风车")
            val pieEntry8 = PieEntry(allInPercent.times(100),"收入")

            val list = ArrayList<PieEntry>()
            if (jyMoney > 0) {
                list.add(pieEntry1)
            }
            if (tcMoney > 0) {
                list.add(pieEntry2)
            }
            if (glMoney > 0) {
                list.add(pieEntry3)
            }
            if (byMoney > 0) {
                list.add(pieEntry4)
            }
            if (wxMoney > 0) {
                list.add(pieEntry5)
            }
            if (ddMoney > 0) {
                list.add(pieEntry6)
            }
            if (sfcMoney > 0) {
                list.add(pieEntry7)
            }
            if (allInMoney > 0) {
                list.add(pieEntry8)
            }
//            val list = mutableListOf(pieEntry1, pieEntry2, pieEntry3,pieEntry4,pieEntry5,pieEntry6,pieEntry7)

            val pieDataSet = PieDataSet(list, "")

            //一般有多少项数据，就配置多少个颜色的，少的话会复用最后一个颜色，多的话无大碍
            pieDataSet.colors = ArrayList<Int>()
            if (jyMoney > 0) {
                pieDataSet.colors.add(Color.parseColor("#60acfc"))
            }
            if (tcMoney > 0) {
                pieDataSet.colors.add(Color.parseColor("#feb64d"))
            }
            if (glMoney > 0) {
                pieDataSet.colors.add(Color.parseColor("#C8DC0A"))
            }
            if (byMoney > 0) {
                pieDataSet.colors.add(Color.parseColor("#15E4D1"))
            }
            if (wxMoney > 0) {
                pieDataSet.colors.add(Color.parseColor("#B30FCF"))
            }
            if (ddMoney > 0) {
                pieDataSet.colors.add(Color.parseColor("#ff7c7c"))
            }
            if (sfcMoney > 0) {
                pieDataSet.colors.add(Color.parseColor("#9287e7"))
            }
            if (allInMoney > 0) {
                pieDataSet.colors.add(Color.parseColor("#9287e7"))
            }
//
//            pieDataSet.colors = mutableListOf(
//                Color.parseColor("#feb64d"),
//                Color.parseColor("#ff7c7c"),
//                Color.parseColor("#9287e7"),
//                Color.parseColor("#60acfc"),
//                Color.parseColor("#C8DC0A"),
//                Color.parseColor("#15E4D1"),
//                Color.parseColor("#B30FCF"))

            val pieData = PieData(pieDataSet!!)

            pieChart.data = pieData
            //显示值格式化，这里使用Api,添加百分号
            pieData.setValueFormatter(PercentFormatter())
            //设置值得颜色
            pieData.setValueTextColor(Color.parseColor("#FFFFFF"))
            //设置值得大小
            pieData.setValueTextSize(10f)

            val description= Description()

            description.text=""
            //把右下边的Description label 去掉，同学也可以设置成饼图说明
            pieChart.description=description

            //去掉中心圆，此时中心圆半透明
            pieChart.holeRadius=0f
            //去掉半透明
            pieChart.setTransparentCircleAlpha(0)

            pieChart.setDrawEntryLabels(true)

            pieChart.invalidate()
        }

    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        buttonView?.let {
            when(buttonView.id) {
                R.id.cb_jy ->
                    jyChecked = if (isChecked) {
                        Log.d(countTag, "加油checked")
                        true
                    } else {
                        Log.d(countTag, "加油unChecked")
                        false
                    }
                R.id.cb_tc ->
                    tcChecked = if (isChecked) {
                        Log.d(countTag, "停车checked")
                        true
                    } else {
                        Log.d(countTag, "停车unChecked")
                        false
                    }
                R.id.cb_gl ->
                    glChecked = if (isChecked) {
                        Log.d(countTag, "过路checked")
                        true
                    } else {
                        Log.d(countTag, "过路unChecked")
                        false
                    }
                R.id.cb_by ->
                    byChecked = if (isChecked) {
                        Log.d(countTag, "保养checked")
                        true
                    } else {
                        Log.d(countTag, "保养unChecked")
                        false
                    }
                R.id.cb_wx ->
                    wxChecked = if (isChecked) {
                        Log.d(countTag, "维修checked")
                        true
                    } else {
                        Log.d(countTag, "维修unChecked")
                        false
                    }
                R.id.cb_dd ->
                    ddChecked = if (isChecked) {
                        Log.d(countTag, "滴答checked")
                        true
                    } else {
                        Log.d(countTag, "滴答unChecked")
                        false
                    }
                R.id.cb_sfc ->
                    sfcChecked = if (isChecked) {
                        Log.d(countTag, "顺风车checked")
                        true
                    } else {
                        Log.d(countTag, "顺风车unChecked")
                        false
                    }
                R.id.cb_all_in ->
                    allInChecked = if (isChecked) {
                        Log.d(countTag, "全部收入checked")
                        cb_dd.isChecked = false
                        cb_sfc.isChecked = false
                        cb_dd.isEnabled = false
                        cb_sfc.isEnabled = false
                        true
                    } else {
                        Log.d(countTag, "全部收入unChecked")
                        cb_dd.isEnabled = true
                        cb_sfc.isEnabled = true
                        false
                    }
                else ->
                    Log.d(countTag, "into else branch")
            }
        }
    }
}
