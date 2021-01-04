package com.hearthappy.desktoplist.desktopview.interfaces

import android.content.Context
import com.hearthappy.desktoplist.desktopview.DesktopListAdapter
import com.hearthappy.desktoplist.desktopview.appstyle.AppStyle

/**
 * Created Date 2021/1/4.
 * @author ChenRui
 * ClassDescription:
 */
interface IDesktopListAdapter {

    //创建ViewHolder的布局id
    fun onAdapterResId(): Int

    //绑定ViewHolder的回调
    fun onBindMyViewHolder(
        context: Context?,
        holder: DesktopListAdapter.ViewHolder,
        position: Int,
        list: List<IBindDataModel>,
        appStyle: AppStyle
    )
}