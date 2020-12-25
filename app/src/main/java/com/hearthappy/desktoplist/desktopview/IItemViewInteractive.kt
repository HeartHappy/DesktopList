package com.hearthappy.desktoplist.desktopview

import android.graphics.RectF
import android.view.View

/**
 * Created Date 2020/12/22.
 * @author ChenRui
 * ClassDescription:
 */
interface IItemViewInteractive {

    /**
     * 选中View位置
     */
    fun selectViewRect(
        rect: RectF?,
        selectView: View?,
        adapterPosition: Int,
        fragmentContent: FragmentContent
    )

    /**
     * 当前页面View移动时
     */
    fun moveView(fromPosition: Int, targetPosition: Int): Boolean

}