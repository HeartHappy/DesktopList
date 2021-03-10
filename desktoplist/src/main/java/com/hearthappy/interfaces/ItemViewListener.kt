package com.hearthappy.interfaces

import com.hearthappy.desktoplist.databinding.ItemAppListBinding

/**
 * Created Date 2021/2/24.
 * @author ChenRui
 * ClassDescription:
 */
interface ItemViewListener {

//    fun onSelectedView()

//    fun onSelectedMoveView()

//    fun onReleaseView()

    fun onBindView(position: Int, list: List<IBindDataModel>, viewBinding: ItemAppListBinding)
}