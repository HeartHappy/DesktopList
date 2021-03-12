//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package com.hearthappy.desktoplist.weiget

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher
import java.util.*

class VerticalTextView(context: Context?, attrs: AttributeSet?) :
    TextSwitcher(context, attrs), ViewSwitcher.ViewFactory {
    private var mTextSize = 16.0f
    private var mPadding = 5
    private var textColor: Int
    private var itemClickListener: OnItemClickListener? = null
    private var currentId: Int
    private lateinit var textList: ArrayList<String>
    private var myHandler: Handler? = null

    fun setText(textSize: Float, padding: Int, textColor: Int) {
        mTextSize = textSize
        mPadding = padding
        this.textColor = textColor
    }

    constructor(context: Context?) : this(context, null as AttributeSet?)

    fun setAnimTime(animDuration: Long) {
        val `in`: Animation = TranslateAnimation(
            0.0f, 0.0f,
            height.toFloat(), 0.0f
        )
        `in`.duration = animDuration
        `in`.interpolator = AccelerateInterpolator()
        val out: Animation = TranslateAnimation(0.0f, 0.0f, 0.0f, (-height).toFloat())
        out.duration = animDuration
        out.interpolator = AccelerateInterpolator()
        this.inAnimation = `in`
        this.outAnimation = out
    }

    fun setTextStillTime(time: Long) {
        myHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> {
                        if (textList.size > 0) {
                            currentId++
                            this@VerticalTextView.setText(textList[currentId % textList.size] as CharSequence?)
                        }
                        handler?.sendEmptyMessageDelayed(0, time)
                    }
                    1 -> handler?.removeMessages(0)
                }
            }
        }
    }

    fun setTextList(titles: ArrayList<String>) {
        if (!::textList.isInitialized) {
            textList = ArrayList<String>()
        } else {
            textList.clear()
        }
        textList.addAll(titles)
        currentId = -1
    }

    fun startAutoScroll() {
        handler?.sendEmptyMessage(0)
    }

    fun stopAutoScroll() {
        handler?.sendEmptyMessage(1)
    }

    override fun makeView(): View {
        val t = TextView(context)
        t.gravity = 19
        t.maxLines = 1
        t.setPadding(mPadding, mPadding, mPadding, mPadding)
        t.setTextColor(textColor)
        t.textSize = mTextSize
        t.isClickable = true
        t.setOnClickListener {
            if (itemClickListener != null && textList.size > 0 && currentId != -1) {
                itemClickListener?.onItemClick(currentId % textList.size)
            }
        }
        return t
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(var1: Int)
    }

    companion object {
        private const val FLAG_START_AUTO_SCROLL = 0
        private const val FLAG_STOP_AUTO_SCROLL = 1
    }

    init {
        textColor = -16777216
        currentId = -1
    }
}