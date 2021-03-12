package com.hearthappy.desktoplist.weiget

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import androidx.core.animation.addListener
import java.lang.ref.WeakReference

/**
 * Created Date 2021/3/11.
 * @author ChenRui
 * ClassDescription:
 */
class MultipleTextView(context: Context, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    private var isShowMainText = true
    private lateinit var mainText: String
    private lateinit var subText: String
    private lateinit var myHandler: Handler
    private var mDelay: Long = -1
    private var transY = -1
    private var hideAnimator: WeakReference<ObjectAnimator>? = null
    private var showAnimator: WeakReference<ObjectAnimator>? = null


    fun enableTextSwitch(isEnableSwitch: Boolean, position: Int) {
        if (transY == -1) {
            postDelayed({ enableTextSwitch(isEnableSwitch, position) }, 200)
            return
        }
        if (::subText.isInitialized && ::mainText.isInitialized && mDelay != (-1).toLong() && ::myHandler.isInitialized) {
            if (isEnableSwitch) {
                Log.d(TAG, "enableTextSwitch: 启用：$position")
                clearAnimator()
                createAnimator()
                switchTextAnimator()
            } else {
                Log.d(TAG, "enableTextSwitch: 禁用：$position")
                clearAnimator()
                myHandler.removeCallbacksAndMessages(null)
                System.gc()
            }
        }
    }

    /**
     * 创建动画
     */
    private fun createAnimator() {
        hideAnimator = WeakReference(ObjectAnimator.ofFloat(this, "translationY", 0f, -transY.toFloat()))
        showAnimator = WeakReference(ObjectAnimator.ofFloat(this, "translationY", transY.toFloat(), 0f))
    }


    /**
     * 清理动画
     */
    private fun clearAnimator() {
        hideAnimator?.get()?.end()
        showAnimator?.get()?.end()
        hideAnimator?.clear()
        showAnimator?.clear()
        hideAnimator = null
        showAnimator = null
    }

    /**
     * 设置子标题
     * @param text String  子标题文本
     * @param delay Long 延迟时长
     */
    fun setSubText(text: String, delay: Long = 3000) {
        this.subText = text
        this.mDelay = delay
        mainText = this.text.toString()
        myHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (msg.what == 1) {
                    val enableSucceed = startHideAnimator()
                    if(!enableSucceed){
                        Log.d(TAG, "handleMessage: 没有启动成功，延迟再启动")
                        myHandler.sendEmptyMessageDelayed(1,200)
                    }
                }
            }
        }
    }

    /**
     * 开始向上滚动主标题动画
     */
    private fun startHideAnimator(): Boolean {
        hideAnimator?.get()?.apply {
            addListener(onEnd = {
                showTextAnimator(getShowText())
            })
            start()
            return true
        }
        return false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        transY = height
    }


    /**
     * 切换文本，带动画
     */
    private fun switchTextAnimator() {
        myHandler.removeCallbacksAndMessages(null)
        myHandler.sendEmptyMessageDelayed(1, mDelay)
    }

    private fun getShowText(): String {
        return if (isShowMainText) {
            isShowMainText = false
            mainText
        } else {
            isShowMainText = true
            subText
        }
    }

    private fun showTextAnimator(text: String) {
        this@MultipleTextView.text = text
        showAnimator?.get()?.apply {
            addListener(onEnd = {
                switchTextAnimator()
            })
            duration = 1000
            start()
        }
    }


    companion object {
        private const val TAG = "MultipleTextView"
    }
}