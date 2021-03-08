package com.hearthappy.desktoplist.weiget

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.utils.widget.ImageFilterView

/**
 * Created Date 2021/3/5.
 * @author ChenRui
 * ClassDescription:抖动ImageView
 */
class JitterImageView(context: Context, attr: AttributeSet) : ImageFilterView(context, attr) {

    private val jitterObject: ObjectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 10f, 0f, -10f, 0f)

    /**
     *
     * 开始抖动
     */
    fun start() {
        jitterObject.apply {
            interpolator = LinearInterpolator()
            duration = 700
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            start()
        }
    }

    /**
     * 停止抖动
     */
    fun end() {
        jitterObject.end()
    }

    fun enableJitter(isEnable: Boolean) {
        if (isEnable) start() else end()
    }
}