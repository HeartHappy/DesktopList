package com.hearthappy.desktoplist.desktopview.appstyle


/**
 * Created Date 2020/12/25.
 * @author ChenRui
 * ClassDescription:应用图标显示样式
 */
class AppStyle : IAppStyle {
    var appStyleType = IAppStyle.APP_STYLE_NO
    var radius = 0
    override fun appStyleType(): Int {
        return appStyleType
    }

    override fun radius(): Int {
        return radius
    }
}