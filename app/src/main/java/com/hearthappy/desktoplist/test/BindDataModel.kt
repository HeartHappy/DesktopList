package com.hearthappy.desktoplist.test

import com.hearthappy.desktoplist.desktopview.interfaces.IBindDataModel

/**
 * Created Date 2021/1/4.
 * @author ChenRui
 * ClassDescription:
 */
class BindDataModel(var url: String, var title: String) : IBindDataModel {
    override fun getAppUrl(): String {
        return url
    }

    override fun getAppName(): String {
        return title
    }
}
