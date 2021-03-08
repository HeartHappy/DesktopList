package com.hearthappy.interfaces

import com.hearthappy.interfaces.IBindDataModel

/**
 * Created Date 2021/2/24.
 * @author ChenRui
 * ClassDescription:
 */
interface ItemViewListener {
    fun onClick(position: Int, list: List<IBindDataModel>)
}