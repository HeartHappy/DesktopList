package com.hearthappy.desktoplist.desktopview.appstyle


/**
 * Created Date 2020/12/25.
 * @author ChenRui
 * ClassDescription:
 */
class AppStyle : IAppStyle {
    var appStyleType=IAppStyle.APP_STYLE_CIRCLE
    var radius=0
    override fun appStyleType(): Int {
        return appStyleType
    }

    override fun radius(): Int {
        return radius
    }
}