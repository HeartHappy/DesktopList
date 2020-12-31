package com.hearthappy.desktoplist.desktopview

import android.view.View

/**
 * Created Date 2020/12/21.
 * @author ChenRui
 * ClassDescription:
 */
interface IDesktopList {

    /**
     * 原数据源
     */
    fun dataSources(): MutableList<Any>

    /**
     * 初始化试图的ResId
     */
    fun adapterResId(): Int


    /**
     * 绑定ViewHolder
     */
    /*fun onBindViewHolder(
        holder: DesktopListAdapter<Any>.ViewHolder, position: Int, listData: MutableList<Any>
    )*/

    /**
     * 手势触摸View移动时是否越界，true代表越界，下面参数代表是否左侧越界或右侧越界
     */
    fun viewMoveBounds(leftBorder: Boolean, rightBorder: Boolean, moveView: View)
    fun <T> onBindViewHolder(
        holder: DesktopListAdapter<T>.ViewHolder, position: Int, listData: MutableList<T>
    )

}