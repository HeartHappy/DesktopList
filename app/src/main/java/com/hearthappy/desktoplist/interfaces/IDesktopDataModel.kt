package com.hearthappy.desktoplist.interfaces

/**
 * Created Date 2020/12/21.
 * @author ChenRui
 * ClassDescription:桌面的数据源
 */
interface IDesktopDataModel<out DB : IBindDataModel> {

    /**
     * 原数据源
     */
    fun dataSources(): List<DB>

    /**
     * 数据源的数量
     * @return Int
     */
    fun dataSize(): Int

}