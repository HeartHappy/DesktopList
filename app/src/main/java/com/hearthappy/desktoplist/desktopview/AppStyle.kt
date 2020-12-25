package com.hearthappy.desktoplist.desktopview

import com.hearthappy.desktoplist.desktopview.utils.Preconditions


/**
 * Created Date 2020/12/25.
 * @author ChenRui
 * ClassDescription:
 */
class AppStyle {
    var isCircle = false
    var isRoundedCorners = false
    var radius = 0


    constructor()

    constructor(isCircle: Boolean) {
        this.isCircle = isCircle
    }

    constructor(isRoundedCorners: Boolean, radius: Int) {
        this.isRoundedCorners = isRoundedCorners
        Preconditions.checkArgument(
            radius > 0,
            "roundingRadius must be greater than 0."
        )
        this.radius = radius
    }
}