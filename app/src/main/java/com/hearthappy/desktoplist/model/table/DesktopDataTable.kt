package com.hearthappy.desktoplist.model.table

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hearthappy.desktoplist.interfaces.IBindDataModel
import kotlinx.android.parcel.Parcelize

/**
 * Created Date 2021/3/1.
 * @author ChenRui
 * ClassDescription:
 */
@Entity(tableName = "DesktopDataTable")
@Parcelize
class DesktopDataTable(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val pageNumber: Int
) : IBindDataModel {
    override fun getAppUrl(): String {
        return url
    }

    override fun getAppName(): String {
        return title
    }
}
