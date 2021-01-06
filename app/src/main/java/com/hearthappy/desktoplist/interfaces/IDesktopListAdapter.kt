package com.hearthappy.desktoplist.interfaces

import android.content.Context
import android.os.Parcelable
import com.hearthappy.desktoplist.appstyle.AppStyle
import com.hearthappy.desktoplist.desktopview.DesktopListAdapter

/**
 * Created Date 2021/1/4.
 * @author ChenRui
 * ClassDescription:
 */
interface IDesktopListAdapter : Parcelable {

    /**
     *
     * @return Int 返回ItemView布局id
     */
    fun onAdapterResId(): Int

    /**
     * 绑定ViewHolder的回调
     * @param context Context?
     * @param holder ViewHolder
     * @param position Int 下标
     * @param list List<IBindDataModel> 数据集合
     * @param appStyle AppStyle 图标显示的样式
     */
    fun onBindMyViewHolder(
        context: Context?,
        holder: DesktopListAdapter.ViewHolder,
        position: Int,
        list: List<IBindDataModel>,
        appStyle: AppStyle
    )

    /**
     *
     * @return Int 返回布局的高度
     */
    fun onItemViewHeight(): Int
}