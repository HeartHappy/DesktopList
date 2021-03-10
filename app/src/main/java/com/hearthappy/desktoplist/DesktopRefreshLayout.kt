package com.hearthappy.desktoplist

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.abs

/**
 * Created Date 2021/3/10.
 * @author ChenRui
 * ClassDescription:解决刷新控件与桌面控件的事件冲突
 */
class DesktopRefreshLayout(context: Context, attrs: AttributeSet?) :
    SwipeRefreshLayout(context, attrs) {

    private var startY = 0f
    private var startX = 0f
    private var mIsVpDrag = false
    private var mTouchSlop = 0

    init {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    /**
     * 解决左右滑动时与下拉刷新控件的事件冲突
     * @param ev MotionEvent
     * @return Boolean
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                // 记录手指按下的位置
                startY = ev.y
                startX = ev.x
                // 初始化标记
                mIsVpDrag = false
            }
            MotionEvent.ACTION_MOVE -> {
                // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false
                if (mIsVpDrag) {
                    return false
                }

                // 获取当前手指位置
                val endY = ev.y
                val endX = ev.x
                val distanceX = abs(endX - startX)
                val distanceY = abs(endY - startY)
                // 如果X轴位移大于Y轴位移，那么将事件交给viewPager处理。
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsVpDrag = true
                    return false
                }
            }
            MotionEvent.ACTION_UP -> {
            }
            MotionEvent.ACTION_CANCEL -> {
                // 初始化标记
                mIsVpDrag = false
            }
        }
        // 如果是Y轴位移大于X轴，事件交给swipeRefreshLayout处理。
        return super.onInterceptTouchEvent(ev)
    }


    /**
     * 解决ItemView拖拽过程中与下拉刷新控件的事件冲突
     * @return Boolean 父布局的子视图是否可以向上滑动  true:子视图可以向上滑动，则优先使用子视图的事件处理  false：不支持，使用下拉刷新的事件处理
     */
    override fun canChildScrollUp(): Boolean {
        //当子视图此时是在拖拽过程中，是可以向上滑动的，返回true，使用子视图的事件处理
        val childAt = getChildAt(0)
        if (childAt is DesktopListView && childAt.isDragItemView()) {
            return true
        }
        return super.canChildScrollUp()
    }

}