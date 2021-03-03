package com.hearthappy.desktoplist.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hearthappy.desktoplist.model.table.DesktopDataTable

/**
 * Created Date 2021/3/1.
 * @author ChenRui
 * ClassDescription:
 */
@Dao interface DesktopDataDao {
    @Insert(entity = DesktopDataTable::class) fun insert(vararg desktopDataTable: DesktopDataTable)

    @Query("DELETE FROM DesktopDataTable") fun deleteAll(): Int

    @Query("DELETE From DesktopDataTable WHERE title=:title") fun deleteByName(title: String): Int

    @Query("UPDATE DesktopDataTable SET pageNumber=:pageNumber,pageAdapterPosition=:pageAdapterPosition WHERE title=:title AND url=:url")
    fun update(title: String, url: String, pageNumber: Int, pageAdapterPosition: Int): Int

    @Query("SELECT * FROM DesktopDataTable WHERE title = :title")
    fun queryByName(title: String): DesktopDataTable?

    @Query("SELECT max(pageNumber) FROM DesktopDataTable") fun queryMaxNumberOfPage(): Int

    @Query("SELECT * FROM DesktopDataTable WHERE title IN (:usernameArray)")
    fun queryByNames(vararg usernameArray: String): List<DesktopDataTable>

    @Query("SELECT * FROM DesktopDataTable WHERE pageNumber=:pageNumber")
    fun queryPageShowNumber(pageNumber: Int): List<DesktopDataTable>

    @Query("SELECT * FROM DesktopDataTable") fun queryAll(): List<DesktopDataTable>


}