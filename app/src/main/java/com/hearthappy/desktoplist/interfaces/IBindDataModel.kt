package com.hearthappy.desktoplist.interfaces

import android.os.Parcelable
import java.io.Serializable

/**
 * Created Date 2020/12/21.
 * @author ChenRui
 * ClassDescription:自定义数据结构与接口实现数据绑定
 */
interface IBindDataModel: Parcelable {

    fun getAppUrl(): String

    fun getAppName(): String
}