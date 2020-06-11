package com.example.maintainingcar.db

import androidx.room.*

@Dao
interface CarDao {
    @Insert
    fun insertInExInfo(inExInfo: InExInfo):Long

    @Update
    fun updateInExInfo(newInfo: InExInfo)

    @Delete
    fun deleteInExInfo(inExInfo: InExInfo)

    @Query("select * from InExInfo order by date Desc")
    fun loadAllInfo():List<InExInfo>

    @Query("select * from InExInfo where type = :type order by date Desc")
    fun queryFromType(type:Int):List<InExInfo>

    @Query("select max(cardIndex) from InExInfo")
    fun queryMaxCardIndex():Int

    // type : 0:收入  1：支出
    @Query("select * from InExInfo  where type = 1 and subType = 0 order by cardIndex DESC")
    fun queryGasInfo():List<InExInfo>

    @Query("select sum(money) from InExInfo where type = 0 and cardIndex = :cardIndex")
    fun querySumIn(cardIndex:Int):Double

    @Query("select sum(money) from InExInfo  where type = :type and subType = :subType ")
    fun queryInfo(type:Int, subType:Int):Double

    @Query("select sum(money) from InExInfo  where type = :type")
    fun queryInfoFromType(type:Int):Double

    @Query("select * from InExInfo  where type = :type and cardIndex = :cardIndex order by id Desc")
    fun queryDetail(type:Int,  cardIndex:Int):List<InExInfo>


}