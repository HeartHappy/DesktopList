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
        selectView: View?,
        adapterPosition: Int,
        fragmentContent: FragmentContent
    )
}