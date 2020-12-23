package com.hearthappy.desktoplist.desktopview

import android.view.View
import com.hearthappy.desktoplist.DataModel

/**
 * Created Date 2020/12/21.
 * @author ChenRui
 * ClassDescription:
 */
interface IDesktopList {

    /**
     * 原数据源
     */
    fun dataSources(): MutableList<DataModel>

    /**
     * 初始化试图的ResId
     */
    fun adapterResId(): Int


    /**
     * 绑定ViewHolder
     */
    fun onBindViewHolder(
        holder: DesktopListAdapter.ViewHolder,
        position: Int,
        listData: MutableList<DataModel>
    )

    /**
     * 手势触摸View移动时是否越界，true代表越界，下面参数代表是否左侧越界或右侧越界
     */
    fun viewMoveBounds(leftBorder: Boolean, rightBorder: Boolean,moveView:View)
}