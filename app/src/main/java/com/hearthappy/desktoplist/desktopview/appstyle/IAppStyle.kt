package com.hearthappy.desktoplist.desktopview.appstyle

/**
 * Created Date 2020/12/29.
 * @author ChenRui
 * ClassDescription:
 */
interface IAppStyle {

    fun appStyleType(): Int

    fun radius(): Int

    companion object{
        const val APP_STYLE_NO = 0
        const val APP_STYLE_CIRCLE = 1
        const val APP_STYLE_ROUNDED = 2
    }
}