package com.hearthappy.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hearthappy.model.table.DesktopDataTable

/**
 * Created Date 2021/3/1.
 * @author ChenRui
 * ClassDescription:
 */
@Dao interface DesktopDataDao {

    //*****************************************************************
    //插入
    //*****************************************************************
    @Insert(entity = DesktopDataTable::class) fun insert(vararg desktopDataTable: DesktopDataTable)


    //*****************************************************************
    //删除
    //*****************************************************************
    @Query("DELETE FROM DesktopDataTable") fun deleteAll(): Int

    @Query("DELETE FROM DesktopDataTable WHERE orientation=:orientation AND userKey=:userKey")
    fun deleteByOrientation(orientation: Int,userKey:String): Int

    @Query("DELETE From DesktopDataTable WHERE title=:title AND orientation=:orientation AND userKey=:userKey")
    fun deleteByName(title: String, orientation: Int,userKey:String): Int


    //*****************************************************************
    //更新
    //*****************************************************************
    @Query("UPDATE DesktopDataTable SET pageNumber=:pageNumber,pageAdapterPosition=:pageAdapterPosition WHERE title=:title AND url=:url AND orientation=:orientation AND appKey=:appKey AND userKey=:userKey")
    fun update(title: String, url: String,appKey:String, pageNumber: Int, pageAdapterPosition: Int, orientation: Int,userKey:String): Int


    //*****************************************************************
    //查询
    //*****************************************************************
    @Query("SELECT * FROM DesktopDataTable WHERE title = :title AND orientation=:orientation AND userKey=:userKey")
    fun queryByName(title: String, orientation: Int,userKey:String): DesktopDataTable?

    @Query("SELECT max(pageNumber) FROM DesktopDataTable WHERE orientation=:orientation AND userKey=:userKey")
    fun queryMaxNumberOfPage(orientation: Int,userKey: String): Int

    //查询应用名称
    @Query("SELECT * FROM DesktopDataTable WHERE title IN (:usernameArray)")
    fun queryByNames(vararg usernameArray: String): List<DesktopDataTable>

    //查询页面显示数量，根据页数
    @Query("SELECT * FROM DesktopDataTable WHERE pageNumber=:pageNumber AND orientation=:orientation AND userKey=:userKey")
    fun queryPageShowNumber(pageNumber: Int, orientation: Int,userKey: String): List<DesktopDataTable>

    //根据屏幕方向查询所有
    @Query("SELECT * FROM DesktopDataTable WHERE orientation=:orientation AND userKey=:userKey")
    fun queryByOrientation(orientation: Int,userKey: String): List<DesktopDataTable>

    //查询表所有
    @Query("SELECT * FROM DesktopDataTable") fun queryAll(): List<DesktopDataTable>


}