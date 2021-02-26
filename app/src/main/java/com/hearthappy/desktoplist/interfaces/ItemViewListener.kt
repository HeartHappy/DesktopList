package com.hearthappy.desktoplist.interfaces

/**
 * Created Date 2021/2/24.
 * @author ChenRui
 * ClassDescription:
 */
interface ItemViewListener {
    fun onClick(currentPagePosition: Int, list: List<IBindDataModel>)
}