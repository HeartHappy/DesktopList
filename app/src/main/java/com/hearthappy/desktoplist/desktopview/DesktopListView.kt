package com.hearthappy.desktoplist.desktopview

import android.app.Activity
import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.hearthappy.desktoplist.R
import com.hearthappy.desktoplist.desktopview.utils.ComputerUtils
import com.hearthappy.desktoplist.desktopview.utils.ViewOperateUtils
import org.jetbrains.annotations.NotNull
import kotlin.properties.Delegates

/**
 * Created Date 2020/12/21.
 * @author ChenRui
 * ClassDescription:
 * 1、创建ViewPager和分页Fragment
 * 2、分页Fragment加载View绑定数据
 */
class DesktopListView(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    private var defaultShowCount = 15 //每页显示个数
    private var spanCount = 3//列数
    private var iDesktopList: IDesktopList by Delegates.notNull() //返回用户的接口
    private var selectMoveViewRect: RectF? = null //选中View的Rect，用来计算分发的x、y和选中View偏移距离

    //以下三个属性主要解决dispatchTouchEvent事件分发的X、Y与选中View存在偏移问题
    private var isFirstDispatch = false  //是否位按下时的第一次分发
    private var offsetX = 0f   //第一次分发时记录偏移量X
    private var offsetY = 0f
    private var totalCount = 0//总数量
    private var totalPage = 0//总页数
    private var isMessageSend = false//消息是否已发送


    /**
     * 1、初始化数据
     * 2、初始化总页数：根据数据的总数量和每页显示数量（默认每页显示15个）
     */
    fun init(
        totalCount: Int,
        spanCount: Int,
        defaultShowCount: Int,
        @NotNull iDesktopList: IDesktopList
    ) {
        this.totalCount = totalCount
        this.spanCount = spanCount
        this.defaultShowCount = defaultShowCount
        this.iDesktopList = iDesktopList
        this.totalPage = ComputerUtils.getAllPage(totalCount, defaultShowCount)
        this.adapter = getPageAdapter()
    }

    // TODO: 2020/12/22 添加页面
    fun addPage() {

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        moveTempView(ev)
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 主页面的适配器,动态创建加载Fragment
     *
     * @return
     */
    @Suppress("DEPRECATION")
    private fun getPageAdapter(): FragmentPagerAdapter {
        val activity = context as FragmentActivity
        return object : FragmentPagerAdapter(activity.supportFragmentManager) {

            override fun getItem(position: Int): Fragment {
                //数据重置
                val listData = ComputerUtils.split(iDesktopList.dataSources(), defaultShowCount)
                //创建时拿取数据
                return FragmentContent(
                    position,
                    listData[position],
                    iDesktopList,
                    spanCount,
                    object : IItemViewInteractive {
                        override fun selectViewRect(rect: RectF?) {
                            selectMoveViewRect = rect
                        }

                        override fun releaseView(releaseView: View?) {
                            isFirstDispatch = false
                            releaseView?.let { rv ->
                                //确定位置
                                val releaseViewRect = ViewOperateUtils.findViewLocation(rv)
                                //查找当前位置的View


                                Log.d(TAG, "releaseView: $releaseViewRect")
                            }
                        }
                    })
            }

            override fun getCount(): Int {
                return totalPage
            }
        }
    }


    /**
     * 移动临时View
     */
    private fun moveTempView(ev: MotionEvent?) {
        val decorView = getDecorView()
        val moveTempView = decorView.findViewById<View>(R.id.createView)
        moveTempView?.let { mtv ->
            ev?.let {
                val layoutParams = FrameLayout.LayoutParams(moveTempView.width, moveTempView.height)
                selectMoveViewRect?.let { smv ->
                    if (!isFirstDispatch) {
                        offsetX = it.rawX - smv.left
                        offsetY = it.rawY - smv.top
                        isFirstDispatch = true
                    } else {
                        layoutParams.leftMargin = (it.rawX - offsetX).toInt()
                        layoutParams.topMargin = (it.rawY - offsetY).toInt()
                        mtv.layoutParams = layoutParams
                        isSwitchViewPager(mtv)
                    }
                }
            }
        }
    }


    /**
     * 是否切换ViewPager页数
     */
    private fun isSwitchViewPager(mtv: View) {
        if (!isMessageSend) {
            when {
                //上一页边界
                mtv.x < 0 -> {
                    setCurrentItem(currentItem - 1, true)
                    isMessageSend = true
                }
                //下一页边界
                (mtv.x + mtv.width) > width -> {
                    setCurrentItem(currentItem + 1, true)
                    isMessageSend = true
                }
                else -> iDesktopList.viewMoveBounds(
                    leftBorder = false,
                    rightBorder = false,
                    moveView = mtv
                )

            }
        } else {
            postDelayed({ isMessageSend = false }, 1500)
        }
    }


    private fun getTempView(frameLayout: FrameLayout): View? {
        return frameLayout.findViewById(R.id.createView)
    }

    private fun getDecorView(): FrameLayout {
        val activity = context as Activity
        return activity.window?.decorView as FrameLayout
    }

    companion object {
        private const val TAG = "DesktopListView"
        private val ACTION_PREV_PAGE = 1
        private val ACTION_PREV_NEXT = 2
    }
}