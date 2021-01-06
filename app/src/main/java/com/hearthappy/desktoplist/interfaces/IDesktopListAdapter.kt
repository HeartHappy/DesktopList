package com.hearthappy.desktoplist.interfaces

import android.content.Context
import android.os.Parcelable
import com.hearthappy.desktoplist.appstyle.AppStyle
import com.hearthappy.desktoplist.desktopview.DesktopListAdapter
import java.io.Serializable

/**
 * Created Date 2021/1/4.
 * @author ChenRui
 * ClassDescription:
 */
interface IDesktopListAdapter :Parcelable {

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