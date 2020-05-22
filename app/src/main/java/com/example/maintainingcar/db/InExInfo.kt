package com.example.maintainingcar.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
 * type:收入/支出
 * subType:收入分类/支出分类
 * money:金额
 * date:记入时间
 * cardIndex:加油支出时+1
 */
@Entity
data class InExInfo(var type:Int, var subType:Int, var money:Double, var date:Long, var cardIndex:Int) {
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0

    override fun toString(): String {
        return "InExInfo(type=$type, subType=$subType, money=$money, date=$date, cardIndex=$cardIndex, id=$id)"
    }


}