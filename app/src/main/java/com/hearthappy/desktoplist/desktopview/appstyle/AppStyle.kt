package com.hearthappy.desktoplist.desktopview.appstyle

/**
 * Created Date 2020/12/30.
 * @author ChenRui
 * ClassDescription:app style密封类，指定类型
 */
sealed class AppStyle {
    object Circle : AppStyle()
    object NotStyle : AppStyle()
    data class Rounded(val radius: Int) : AppStyle()
}
