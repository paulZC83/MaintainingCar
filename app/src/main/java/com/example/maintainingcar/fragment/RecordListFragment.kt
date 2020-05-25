package com.example.maintainingcar.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.maintainingcar.CarApplication
import com.example.maintainingcar.R
import com.example.maintainingcar.db.AppDatabase
import com.example.maintainingcar.db.CarDao
import com.example.maintainingcar.db.InExInfo
import com.example.maintainingcar.kt.dp2px
import com.yanzhenjie.recyclerview.*
import kotlinx.android.synthetic.main.fragment_list.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class RecordListFragment : Fragment(), RadioGroup.OnCheckedChangeListener {
    private val listTag = "Car_List"
    private lateinit var carDao: CarDao
    private lateinit var adapter: ListAdapter
    private var inExList = ArrayList<InExInfo>()
    private lateinit var handler :CarHandler
    private var currentCheckBox :Int = R.id.rb_All
    private val swipeMenueCreator = SwipeMenuCreator{ leftMenu: SwipeMenu, rightMenu: SwipeMenu, i: Int ->
        val deleteItem = SwipeMenuItem(activity)
        deleteItem.text = resources.getString(R.string.delete)
        deleteItem.setBackgroundColor(ContextCompat.getColor(CarApplication.context, android.R.color.holo_red_light))
        deleteItem.textSize = 18
        deleteItem.setTextColor(ContextCompat.getColor(CarApplication.context,android.R.color.white))
        deleteItem.width = CarApplication.context.dp2px(80f)
        deleteItem.height = CarApplication.context.dp2px(60f)
        rightMenu.addMenuItem(deleteItem)
    }
    private val itemMenuClickListener = OnItemMenuClickListener{ menuBridge: SwipeMenuBridge, adapterPosition:Int ->
        // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱
        menuBridge.closeMenu()

        // 左侧还是右侧菜单
        val direction = menuBridge.direction
        // 菜单item中的position
        val position = menuBridge.position
        Log.d(listTag, "direction is $direction----position is $position")

        deleteRecord(adapterPosition)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        carDao = AppDatabase.getDatabase(CarApplication.context).carDao()
        adapter = ListAdapter(CarApplication.context, ArrayList<InExInfo>())
        handler = CarHandler()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rg_view.setOnCheckedChangeListener(this)

        rvRecord.setSwipeMenuCreator(swipeMenueCreator)
        rvRecord.setOnItemMenuClickListener(itemMenuClickListener)

        val layoutManager =LinearLayoutManager(CarApplication.context)
        rvRecord.layoutManager = layoutManager
        rvRecord.adapter = adapter
        getData(R.id.rb_All)
        iv_to_top.setOnClickListener {
            rvRecord.scrollToPosition(0)
        }
    }

    private fun getData(checkedId: Int) {
        currentCheckBox = checkedId
        inExList.clear()
        thread {
            var list : List<InExInfo>?= null
            when(checkedId) {
                R.id.rb_All ->
                    list = carDao.loadAllInfo()
                R.id.rb_in ->
                    list = carDao.queryFromType(0)
                R.id.rb_ex ->
                    list = carDao.queryFromType(1)
            }

            list?.let {
                for (inExInfo in it) {
                    inExList.add(inExInfo)
                }
                val message = Message.obtain()
                message.what = 1
                handler.sendMessage(message)
            }

        }
    }

    inner class CarHandler: Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                // 刷新界面
                adapter.reFresh(inExList)
            }
        }
    }

    class ListAdapter(val context: Context, var list:List<InExInfo>):RecyclerView.Adapter<ListAdapter.MyViewHolder>(){
        private val typeArray = CarApplication.context.resources.getStringArray(R.array.array_type)
        private val inTypeArray = CarApplication.context.resources.getStringArray(R.array.in_type)
        private val exTypeArray = CarApplication.context.resources.getStringArray(R.array.ex_type)
        private val format = SimpleDateFormat("yyyy/MM/dd")
        fun reFresh(infos:List<InExInfo>){
            list = infos
            notifyDataSetChanged()
        }
        inner class MyViewHolder(view: View):RecyclerView.ViewHolder(view) {
            val image:ImageView = view.findViewById(R.id.iv_inOrEx)
            val subType:TextView = view.findViewById(R.id.tv_sub_type)
            val money:TextView = view.findViewById(R.id.tv_list_money)
            val date :TextView = view.findViewById(R.id.tv_list_date)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_list_layout, parent, false)
            return MyViewHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            var imageId = R.drawable.plus
            // 支出的场景
            if (list[position].type == 1) {
                imageId = R.drawable.minus
            }
            Glide.with(context).load(imageId).into(holder.image)

            holder.money.text = list[position].money.toString()
            var subType  = if (list[position].type == 1) {
                exTypeArray[list[position].subType]
            } else {
                inTypeArray[list[position].subType]
            }
            holder.subType.text = subType
            holder.date.text = format.format(Date(list[position].date))
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(checkedId) {
            R.id.rb_All -> {
                Log.d(listTag, "select all")
                getData(R.id.rb_All)
            }
            R.id.rb_ex -> {
                Log.d(listTag, "select ex")
                getData(R.id.rb_ex)
            }
            R.id.rb_in -> {
                Log.d(listTag, "select in")
                getData(R.id.rb_in)
            }
        }
    }

    private fun deleteRecord(position: Int){
        thread {
            carDao.deleteInExInfo(inExList[position])
            getData(currentCheckBox)
        }
    }

}
