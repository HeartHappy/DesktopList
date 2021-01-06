package com.hearthappy.desktoplist.desktopview

import android.os.Parcelable
import android.view.View
import java.io.Serializable

/**
 * Created Date 2020/12/22.
 * @author ChenRui
 * ClassDescription:ItemView交互操作接口
 */
interface IItemViewInteractive :Parcelable {

    /**
     * 选中View位置
     */
    fun selectViewRect(
        selectView: View?, adapterPosition: Int, fragmentContent: FragmentContent
    )
}