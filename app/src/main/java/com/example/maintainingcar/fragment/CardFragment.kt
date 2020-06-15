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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.maintainingcar.CarApplication
import com.example.maintainingcar.R
import com.example.maintainingcar.db.AppDatabase
import com.example.maintainingcar.db.CarDao
import com.example.maintainingcar.entity.CardInfo
import com.example.maintainingcar.utils.roundTo1DecimalPlaces
import com.example.maintainingcar.view.DialogHandler
import kotlinx.android.synthetic.main.fragment_card.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class CardFragment : Fragment() {
    private val cardTag = "Car_Card"
    private lateinit var carDao: CarDao
    private lateinit var adapter: CardAdapter
    private var cardInfoList = ArrayList<CardInfo>()
    lateinit var handler :CarHandler
    lateinit var dialogHandler:DialogHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        carDao = AppDatabase.getDatabase(CarApplication.context).carDao()
        adapter = CardAdapter(CarApplication.context, ArrayList<CardInfo>())
        handler = CarHandler()
        dialogHandler = DialogHandler(activity!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager =LinearLayoutManager(CarApplication.context)
        rvCard.layoutManager = layoutManager
        rvCard.adapter = adapter
        getData()
    }

    private fun getData() {
        cardInfoList.clear()
        thread {
            val list = carDao.queryGasInfo()
            for (inExInfo in list) {
                val inMoney = carDao.querySumIn(inExInfo.cardIndex).roundTo1DecimalPlaces()
                Log.d(cardTag, "inMoney = $inMoney ---- exMoney = ${inExInfo.money}")
                var imageId = R.drawable.loss_money
                if (inMoney >= inExInfo.money) {
                    imageId = R.drawable.get_money
                }
                val format = SimpleDateFormat("yyyy/MM/dd")
                val cardInfo = CardInfo(imageId, format.format(Date(inExInfo.date)), inExInfo.money, inMoney, inExInfo.cardIndex)
                cardInfoList.add(cardInfo)
            }
            val message = Message.obtain()
            message.what = 1
            handler.sendMessage(message)
        }
    }

    inner class CarHandler: Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                // 刷新界面
                adapter.reFresh(cardInfoList)
            }
        }
    }

    inner class CardAdapter(val context: Context, var list:List<CardInfo>):RecyclerView.Adapter<CardAdapter.MyViewHolder>(){
        val dao = AppDatabase.getDatabase(CarApplication.context).carDao()
        fun reFresh(infos:List<CardInfo>){
            list = infos
            notifyDataSetChanged()
        }
        inner class MyViewHolder(view: View):RecyclerView.ViewHolder(view) {
            val image:ImageView = view.findViewById(R.id.iv_money)
            val money:TextView = view.findViewById(R.id.tv_money)
            val exDate:TextView = view.findViewById(R.id.ex_date)
            val exMoney:TextView = view.findViewById(R.id.ex_money)
            val inMoney:TextView = view.findViewById(R.id.in_money)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_card_layout, parent, false)
            val viewHolder = MyViewHolder(view)
            viewHolder.itemView.setOnClickListener {
                val position = viewHolder.adapterPosition
                thread {
                    Log.d("Car_Card","list is ${list.toString()}, position is $position ")
                    val cardIndex = list[position].cardIndex
                    Log.d("Car_Card","cardIndex is $cardIndex")
                    val inList = dao.queryDetail(0, cardIndex)
                    var countIn = inList.size
                    var countDD = 0
                    var countSFC = 0
                    var moneyDD = 0.0
                    var moneySFC = 0.0
                    for (item in inList) {
                        if (item.subType == 0) {
                            moneyDD += item.money
                            countDD++
                        }
                        if (item.subType == 1) {
                            moneySFC += item.money
                            countSFC++
                        }
                    }
                    val showMoney = if (moneyDD > moneySFC)  {
                        "滴答$countDD 笔，共$moneyDD 元"
                    } else {
                        "顺风车$countSFC 笔，共$moneySFC 元"
                    }

                    var msg = "都没带人，咋整@@"
                    var titile = "不足为道"
                    if (moneyDD > 0.0 || moneySFC > 0.0){
                        msg = "总共收入$countIn 笔，总计 ${list[position].inMoney}元，其中$showMoney"
                    }
                    if (list[position].exMoney > list[position].inMoney) {
                        titile = "革命尚未成功，同志仍需努力"
                    } else {
                        titile ="成绩感人！"
                    }
                    val message = Message.obtain()
                    message.data.putString("TITLE", titile)
                    message.data.putString("MSG", msg)
                    dialogHandler.sendMessage(message)
                }
            }
            return viewHolder
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            Glide.with(context).load(list[position].imageId).into(holder.image)
            var strMoney = ""
            var money = (list[position].inMoney - list[position].exMoney).roundTo1DecimalPlaces()
            strMoney = if (money > 0) {
                "+$money"
            } else{
                "$money"
            }
            holder.money.text = strMoney
            holder.exDate.text = CarApplication.context.getString(R.string.ex_date) + list[position].exDate
            holder.exMoney.text = CarApplication.context.getString(R.string.ex) + list[position].exMoney
            holder.inMoney.text = CarApplication.context.getString(R.string.`in`) + list[position].inMoney.toString()
        }
    }

}
