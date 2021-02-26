package com.hearthappy.desktoplist.desktopview

import android.os.Parcelable
import android.view.View
import com.hearthappy.desktoplist.interfaces.IBindDataModel

/**
 * Created Date 2020/12/22.
 * @author ChenRui
 * ClassDescription:ItemView交互操作接口
 */
interface IItemViewInteractive : Parcelable {

    /**
     * 选中View位置
     */
    fun selectViewRect(selectView: View?, adapterPosition: Int, fragmentContent: FragmentContent)

    /**
     * itemView点击监听
     * @param position Int
     * @param list List<IBindDataModel>
     */
    fun onClick(position: Int, list: List<IBindDataModel>)

    /**
     * 选中View在当前界面移动
     */
    fun onMove()


}