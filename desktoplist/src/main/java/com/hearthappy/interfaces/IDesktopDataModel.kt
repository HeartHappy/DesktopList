package com.hearthappy.interfaces

/**
 * Created Date 2020/12/21.
 * @author ChenRui
 * ClassDescription:桌面的数据源
 */
interface IDesktopDataModel<out DB : IBindDataModel> {

    /**
     * @return List<DB> 返回数据的集合
     */
    fun dataSources(): List<DB>

}