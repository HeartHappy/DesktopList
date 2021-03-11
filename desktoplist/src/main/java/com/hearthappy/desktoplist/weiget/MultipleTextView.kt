package com.hearthappy.desktoplist.weiget

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import androidx.core.animation.addListener

/**
 * Created Date 2021/3/11.
 * @author ChenRui
 * ClassDescription:
 */
class MultipleTextView(context: Context, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    private var isShowMainText = true
    private lateinit var mainText: String
    private lateinit var animator: ObjectAnimator
    private lateinit var subText: String


    /**
     * 设置子标题
     * @param text String  子标题文本
     * @param enableAnimator Boolean  是否启用动画
     * @param delay Long 延迟时长
     */
    fun setSubText(text: String, enableAnimator: Boolean = true, delay: Long = 3000) {
        this.subText = text
        mainText = this.text.toString()
        if (enableAnimator) {
            switchTextAnimator(subText, delay)
        } else {
            switchText(text, delay)
        }
    }


    /**
     * 直接切换文本，没有动画
     * @param text String
     */
    private fun switchText(text: String, delay: Long) {
        postDelayed({
            this@MultipleTextView.text = text
            if (isShowMainText) {
                switchText(mainText, delay)
            } else {
                switchText(subText, delay)
            }
            isShowMainText = !isShowMainText
        }, delay)
    }


    /**
     * 切换文本，带动画
     * @param text String
     */
    private fun switchTextAnimator(text: String, delay: Long) {
        animator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
        animator.apply {
            startDelay = delay
            addListener(onEnd = {
                showTextAnimator(text, delay)
            })
            start()
        }
    }

    private fun showTextAnimator(text: String, delay: Long) {
        this@MultipleTextView.text = text
        animator = ObjectAnimator.ofFloat(this@MultipleTextView, "alpha", 0f, 1f)
        animator.apply {
            addListener(onEnd = {
                if (isShowMainText) {
                    switchTextAnimator(mainText, delay)
                } else {
                    switchTextAnimator(subText, delay)
                }
                isShowMainText = !isShowMainText
            })
            duration = 1000
            start()
        }
    }


    companion object {
        private const val TAG = "MultipleTextView"
    }
}