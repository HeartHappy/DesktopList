package com.hearthappy.desktoplist.desktopview

import android.graphics.RectF
import android.view.View

/**
 * Created Date 2020/12/22.
 * @author ChenRui
 * ClassDescription:
 */
interface IItemViewInteractive {
    fun selectViewRect(rect: RectF?)

    fun releaseView(releaseView:View?)
}