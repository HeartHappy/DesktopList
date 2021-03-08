package com.hearthappy.desktoplist

import com.hearthappy.interfaces.IBindDataModel
import kotlinx.android.parcel.Parcelize

/**
 * Created Date 2021/1/4.
 * @author ChenRui
 * ClassDescription:
 */
@Parcelize
class BindDataModel(private var url: String, private var title: String) : IBindDataModel {

    override fun getAppUrl(): String {
        return url
    }

    override fun getAppName(): String {
        return title
    }
}
