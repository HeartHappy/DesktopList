package com.hearthappy.model.table

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hearthappy.interfaces.IBindDataModel
import kotlinx.android.parcel.Parcelize

/**
 * Created Date 2021/3/1.
 * @author ChenRui
 * ClassDescription:pageNumber存储的是页面的索引（0~3页，说明有4页）
 */
@Entity(tableName = "DesktopDataTable") @Parcelize
class DesktopDataTable(@PrimaryKey(autoGenerate = true) val id: Long = 0, val title: String, val url: String, val pageNumber: Int, val pageAdapterPosition: Int, val orientation: Int) :
    IBindDataModel {
    override fun getAppUrl(): String {
        return url
    }

    override fun getAppName(): String {
        return title
    }
}
