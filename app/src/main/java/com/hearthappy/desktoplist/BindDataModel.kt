package com.hearthappy.desktoplist

import com.hearthappy.interfaces.IBindDataModel

/**
 * Created Date 2021/1/4.
 * @author ChenRui
 * ClassDescription:
 */
@kotlinx.parcelize.Parcelize
class BindDataModel(private var url: String, private var title: String, private var appId: String = "") : IBindDataModel {

    override fun getAppUrl(): String {
        return url
    }

    override fun getAppName(): String {
        return title
    }

    override fun getAppId(): String {
        return appId
    }
}
