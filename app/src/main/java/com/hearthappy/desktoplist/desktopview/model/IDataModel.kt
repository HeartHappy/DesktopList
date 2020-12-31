package com.hearthappy.desktoplist.desktopview.model

/**
 * Created Date 2020/12/29.
 * @author ChenRui
 * ClassDescription:
 */
interface IDataModel<DB : List<*>> {
    fun onCreateDataModel(): DB
}