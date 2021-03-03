package com.hearthappy.desktoplist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.hearthappy.desktoplist.interfaces.IBindDataModel

/**
 * Created Date 2021/3/2.
 * @author ChenRui
 * ClassDescription:
 */
class DesktopListViewModel(app:Application):AndroidViewModel(app) {
    //用户的数据源
    private lateinit var userListData: MutableList<MutableList<IBindDataModel>>
    private var desktopListData: MutableList<MutableList<IBindDataModel>> = mutableListOf()
}