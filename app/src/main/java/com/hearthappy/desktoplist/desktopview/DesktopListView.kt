package com.hearthappy.desktoplist.desktopview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.RectF
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.hearthappy.desktoplist.DataModel
import com.hearthappy.desktoplist.R
import com.hearthappy.desktoplist.desktopview.utils.ComputerUtils
import com.hearthappy.desktoplist.desktopview.utils.ViewOperateUtils
import org.jetbrains.annotations.NotNull
import kotlin.math.abs
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
    private var totalCount = 0//总数量
    private var totalPage = 0//总页数
    private var iDesktopList: IDesktopList by Delegates.notNull() //返回用户的接口


    private var fromItemViewRect: RectF? = null //选中ItemView的Rect，用来计算分发的x、y和选中ImageView偏移距离
    private var fromItemView: View? = null //选中的ItemView
    private var fromPagePosition = -1 //选中当前页面position（第几页）
    private var fromAdapterPosition = -1//选中当前页面适配的position（第几个item）
    private var fromFragmentContent: FragmentContent? = null//选中ItemView来自的Fragment视图
    private var currentFragmentContent: Fragment? = null
    var firstMoveTime = 0L
    private var floatViewScrollState: Int = 0 //默认禁止状态


    private var appStyle: AppStyle by Delegates.notNull()

    //以下三个属性主要解决dispatchTouchEvent事件分发的X、Y与选中View存在偏移问题
    private var isFirstDispatch = false  //是否位按下时的第一次分发
    private var offsetX = 0f   //第一次分发时记录偏移量X
    private var offsetY = 0f
    private var touchX = 0f
    private var touchY = 0f


    //用户的数据源
    var userListData: MutableList<MutableList<DataModel>> by Delegates.notNull()
    var desktopListData: MutableList<MutableList<DataModel>> = mutableListOf()

    private var isMessageSend = false//消息是否已发送,另代表是否在边界
    private var myHandler: Handler = Handler(Handler.Callback {
        when (it.what) {
            ACTION_PREV_PAGE -> {
                setCurrentItem(currentItem - 1, true)
                isMessageSend = false
            }
            ACTION_PREV_NEXT -> {
                setCurrentItem(currentItem + 1, true)
                isMessageSend = false
            }
        }
        false
    })

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
        initProperty()
        this.totalCount = totalCount
        this.spanCount = spanCount
        this.defaultShowCount = defaultShowCount
        this.iDesktopList = iDesktopList
        this.totalPage = ComputerUtils.getAllPage(totalCount, defaultShowCount)
        initData()
        this.adapter = DesktopAdapter((context as FragmentActivity).supportFragmentManager)
    }

    fun appStyle() {
        appStyle(isCircle = false, isRoundedCorners = false, radius = 0)
    }

    fun appStyle(isCircle: Boolean) {
        appStyle(isCircle, false, 0)
    }

    fun appStyle(isRoundedCorners: Boolean, radius: Int) {
        appStyle(false, isRoundedCorners, radius)
    }

    private fun appStyle(isCircle: Boolean, isRoundedCorners: Boolean, radius: Int) {
        if (appStyle.run {
                this.isCircle = isCircle
                this.isRoundedCorners = isRoundedCorners
                this.radius = radius
                true
            }) {
            Log.d(TAG, "appStyle: invalidate")
            notifyPageStyleSetChange()
        }
    }

    /**
     * 通知页面样式发生改变
     */
    private fun notifyPageStyleSetChange() {
        this.adapter = DesktopAdapter((context as FragmentActivity).supportFragmentManager)
//        init(totalCount, spanCount, defaultShowCount, iDesktopList)
    }


    private fun initProperty() {
        offscreenPageLimit = 2
        appStyle = AppStyle()
    }

    private fun initData() {
        userListData = ComputerUtils.split(iDesktopList.dataSources(), defaultShowCount)
        //创建成员变量存储，改变数据时无需改变用户传入的数据源
        for (i in 0 until totalPage) {
            val dataModels = userListData[i]
            desktopListData.add(dataModels)
        }
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            if (it.pointerCount == 2) return false
            when (it.action) {
                MotionEvent.ACTION_MOVE -> {
                    val decorView = getDecorView()
                    val moveFloatView = getFloatView(decorView)
                    moveFloatView?.let { mfv ->
                        it.let { it2 ->
                            //移动浮动View坐标
                            coordinateFloatViewMove(mfv, it2)
                            //跨界面移动
                            if (fromPagePosition != currentItem && fromPagePosition != -1) {

                                /*Log.d(
                                    TAG,
                                    "onScroll:跨界面移动 floatView拖拽移动至第${currentItem}页,原选中ItemView的页面是：$fromPagePosition,状态${touchStateIsSwipe(
                                        it.rawX,
                                        it.rawY
                                    )}"
                                )*/
                                val targetFragment =
                                    (adapter as DesktopAdapter).getInstantFragment() as FragmentContent
                                //如果为静止状态
                                if (!touchStateIsSwipe(it.rawX, it.rawY)) {
                                    Log.d(TAG, "dispatchTouchEvent: 静止状态")
                                    //1、查询当前停留页面是否不为满Item

                                    val itemCount = targetFragment.getAdapter().itemCount
                                    if (itemCount == defaultShowCount) {
                                        Log.d(TAG, "dispatchTouchEvent: 屏幕已满")
                                        return false
                                    } else if (itemCount < defaultShowCount) {
                                        val targetIndex = getTargetIndex(mfv, targetFragment)
                                        val dataModel =desktopListData[fromPagePosition][this.fromAdapterPosition]
                                        targetFragment.getAdapter().implicitInset(dataModel, targetIndex)
                                        Log.d(TAG, "dispatchTouchEvent: 执行隐式插入position:$targetIndex")
//                                        postDelayed({ targetFragment.getAdapter().notifyDataChanged() }, 200)
                                    }
                                    //移动状态
                                } else if(floatViewScrollState== SCROLL_STATE_MOVE){
                                    val implicitPosition =targetFragment.getAdapter().getImplicitPosition()
                                    //如果有隐式插入的position，在移动状态与插入ItemView的Rect不相交，代表移除，则删除隐式插入的ItemView
                                    if (implicitPosition > -1  ) {
                                        val implicitViewHolder =targetFragment.getRecyclerView().findViewHolderForAdapterPosition(implicitPosition)
                                        implicitViewHolder?.itemView?.let {iv->
                                            val implicitViewHolderRect =ViewOperateUtils.findViewLocation(iv)
                                            if (!implicitViewHolderRect.intersect(ViewOperateUtils.findViewLocation(mfv))) {
                                                Log.d(TAG, "dispatchTouchEvent:  执行隐式删除position，当前移动位置与隐式插入View不相交:$implicitPosition")
                                                targetFragment.getAdapter().notifyItemMoved(implicitPosition,getTargetIndex(mfv, targetFragment))
                                                // TODO: 2020/12/28 存在相互替换问题
                                            }
                                        }
                                        Log.d(TAG, "dispatchTouchEvent: 滑动状态,并且存在隐式View")
//                                        postDelayed({ targetFragment.getAdapter().notifyDataChanged() }, 200)
                                    }

                                }
                                return false
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    val decorView = getDecorView()
                    val moveFloatView = getFloatView(decorView)
                    moveFloatView?.let { mfv ->
                        floatViewUp(mfv, decorView)
//                        Log.d(TAG, "dispatchTouchEvent: 处理松开事件，存在浮动View，不分发")
                        return false
                    }
                }
                else -> {
                    return super.dispatchTouchEvent(ev)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (fromPagePosition == currentItem && fromItemView != null) {
            return true
        }
        return super.onTouchEvent(ev)
    }


    /**
     * 临时View松开
     */
    private fun floatViewUp(
        mfv: View,
        decorView: FrameLayout
    ) {
        //删除临时View
        isFirstDispatch = false
        val isReplaceSucceed = findReplaceView(mfv)
//        Log.d(TAG, "floatViewUp: isReplaceSucceed:$isReplaceSucceed")
        //3、删除临时视图
        decorView.removeView(mfv)
        fromItemView?.visibility = View.VISIBLE
        Log.d(TAG, "floatViewUp--->remove float View")

        fromItemView = null
    }


    /**
     * 查找替换View位置
     */
    private fun findReplaceView(mfv: View): Boolean {
        val targetFragment = (adapter as DesktopAdapter).getInstantFragment() as FragmentContent
        val targetIndex = getTargetIndex(mfv, targetFragment)
        Log.d(TAG, "findReplaceView: $fromAdapterPosition,$targetIndex")
        //松开时的几种情况
        //1、松开时在当前页
        if (fromPagePosition == currentItem) {
            //1、如果是原位置，直接返回
            if (targetIndex == this.fromAdapterPosition) {
                targetFragment.getAdapter().notifyItemChanged(fromAdapterPosition)
//                Log.d(TAG, "findReplaceView: 当前页，原位置释放")
                return false
            }
            //1、数据源位置改变
            targetFragment.replaceLocal(
                fromAdapterPosition,
                targetIndex,
                desktopListData[fromPagePosition]
            )
            //通过以上步骤，再对数据进行重新绑定，数据源与视图重新绑定.
            if (fromAdapterPosition < targetIndex) {
                targetFragment.getAdapter().notifyItemRangeChanged(fromAdapterPosition, targetIndex)
                /*Log.d(
                    TAG,
                    "findReplaceView:fromPosition: $fromAdapterPosition,targetIndex:$targetIndex"
                )*/
            } else {
                targetFragment.getAdapter().notifyItemRangeChanged(targetIndex, fromAdapterPosition)
                /*Log.d(
                    TAG,
                    "findReplaceView: targetIndex:$targetIndex,fromPosition:$fromAdapterPosition"
                )*/
            }
            //解决notifyItemRangeChanged对数据重新绑定有时失效问题
            postDelayed({ targetFragment.getAdapter().notifyDataChanged() }, 200)

//            Log.d(TAG, "findReplaceView: 当前页，则执行替换操作")
            return true
            //2、松开时，拖拽至其他页面，并且其他页面是满的。此时targetIndex=-1，
        } else if (targetIndex == -1) {
            Toast.makeText(context, "当前屏幕没有空间", Toast.LENGTH_SHORT).show()
            return false
            //3、松开时，跨界面
        } else if (fromPagePosition != currentItem) {
//            Log.d(TAG, "findReplaceView: 跨界面拖拽")
            //如果已经存在隐式
            if(targetFragment.getAdapter().getImplicitPosition()>-1){
                targetFragment.getAdapter().resetImplicitPosition()
                targetFragment.getAdapter().notifyDataChanged()
            }else{
                val dataModel = desktopListData[fromPagePosition][this.fromAdapterPosition]
                targetFragment.getAdapter().inset(dataModel, targetIndex)
            }
            //跨界面删除，由于页面被销毁了，所以需要修改为删除数据源
            desktopListData[fromPagePosition].removeAt(this.fromAdapterPosition)
            //刷新原界面视图
            fromFragmentContent?.let { ffc ->
                if (ffc.isAdded) {
                    ffc.getAdapter().notifyDataChanged()
                }
            }
            return true
        } else {
            return false
        }
    }

    /**
     * 临时View拖拽移动
     */
    private fun coordinateFloatViewMove(mfv: View, it: MotionEvent) {
        val layoutParams = FrameLayout.LayoutParams(mfv.width, mfv.height)
        fromItemViewRect?.let { smv ->
            //第一次分发时记录按下的位置x,y与当前位置的itemView偏移距离
            if (!isFirstDispatch) {
                offsetX = it.rawX - smv.left
                offsetY = it.rawY - smv.top
                isFirstDispatch = true
            } else {
                //根据手势拖拽临时View
                layoutParams.leftMargin = (it.rawX - offsetX).toInt()
                layoutParams.topMargin = (it.rawY - offsetY).toInt()
                mfv.layoutParams = layoutParams
                isSwitchViewPager(mfv)
            }
        }
    }


    /**
     * 触摸状态是否为移动状态
     */
    private fun touchStateIsSwipe(x: Float, y: Float): Boolean {
        val secondTime = System.currentTimeMillis()
        val sign: Boolean
        if (touchX == 0f && touchY == 0f) {
            touchX = x
            touchY = y
            sign = true
        } else {
            val absX = abs(x - touchX)
            val absY = abs(y - touchY)
            touchX = x
            touchY = y
            sign = absX > 1 && absY > 1
        }
        if (sign) {
            firstMoveTime = 0L
            floatViewScrollState = SCROLL_STATE_MOVE
            Log.d(TAG, "touchStateIsSwipe: 移动中")
        } else {
            if (floatViewScrollState == SCROLL_STATE_MOVE && firstMoveTime == 0L) {
                firstMoveTime = System.currentTimeMillis()
                Log.d(TAG, "touchStateIsSwipe: 可能发生静止时间")
            } else if (secondTime - firstMoveTime >= 1000 && firstMoveTime != 0L && !isMessageSend && floatViewScrollState == SCROLL_STATE_MOVE) {
                floatViewScrollState = SCROLL_STATE_IDLE
                Log.d(
                    TAG,
                    "touchStateIsSwipe:完全静止 ${secondTime - firstMoveTime},firstMoveTime:$firstMoveTime"
                )
                return false
            }
        }
        return true
    }


    /**
     * 是否切换ViewPager页数
     */
    private fun isSwitchViewPager(mfv: View) {
        when {
            //上一页边界
            mfv.x < 0 && !isMessageSend -> {
//                Log.d(TAG, "isSwitchViewPager: 延迟发送切换上一页请求")
                myHandler.sendEmptyMessageDelayed(ACTION_PREV_PAGE, 1000)
                isMessageSend = true
            }
            //下一页边界
            (mfv.x + mfv.width) > width && !isMessageSend -> {
//                Log.d(TAG, "isSwitchViewPager: 延迟发送切换下一页请求")
                myHandler.sendEmptyMessageDelayed(ACTION_PREV_NEXT, 1000)
                isMessageSend = true
            }
            mfv.x > 0 && mfv.x + mfv.width < width && isMessageSend -> {
//                Log.d(TAG, "isSwitchViewPager: 删除延迟消息")
                myHandler.removeCallbacksAndMessages(null)
                isMessageSend = false
            }
            else -> {
                /*Log.d(
                    TAG,
                    "isSwitchViewPager: ${mfv.x},${mfv.width},$width,${(mfv.x + mfv.width) > width}"
                )*/
            }
        }
    }


    /**
     * 返回Target页面的TargetItem的Index
     */
    private fun getTargetIndex(
        mfv: View,
        fragmentContent: FragmentContent
    ): Int {
        //1、确定释放位置
        val moveViewRect = ViewOperateUtils.findViewLocation(mfv)
//        Log.d(TAG, "findReplaceView--->moveViewRect: $moveViewRect")
        val currentPageItemCount = fragmentContent.getRecyclerView().adapter?.itemCount
        currentPageItemCount?.let { cic ->
            if (cic == defaultShowCount && fromPagePosition != currentItem) {
                return -1
            }
            var localIntersect = -1//原位置相交
            var targetIntersect = -1//目标位置相交
            for (i in 0 until cic) {
                val vh = fragmentContent.getRecyclerView().findViewHolderForLayoutPosition(i)
                vh?.itemView?.let { iv ->
                    val linearLayout = iv as LinearLayout
                    val imageView = linearLayout.getChildAt(0) as ImageView
                    val targetViewRect = ViewOperateUtils.findViewLocation(imageView)
                    if (moveViewRect.intersect(targetViewRect)) {
                        if (i != fromAdapterPosition) {
                            targetIntersect = i
                            /*Log.d(
                                TAG,
                                "getTargetIndex:$i,目标相交,选中$fromAdapterPosition,${textView.text},mvr:$moveViewRect,target:$targetViewRect"
                            )*/
                        } else {
                            localIntersect = i
                            /*Log.d(
                                TAG,
                                "getTargetIndex:$i,原位置相交,选中$fromAdapterPosition,${textView.text},mvr:$moveViewRect,target:$targetViewRect"
                            )*/
                        }
                    }/* else {
                        Log.d(
                            TAG,
                            "getTargetIndex: $i,不相交,${textView.text},mvr:$moveViewRect,target:$targetViewRect"

                    })*/
                }
            }
            if (targetIntersect != -1) {
                return targetIntersect
            } else if (localIntersect != -1 && targetIntersect == -1) {
                return localIntersect
            }
            return cic
        }
        return 0
    }


    private fun getFloatView(frameLayout: FrameLayout): View? {
        return frameLayout.findViewById(R.id.createView)
    }

    private fun getDecorView(): FrameLayout {
        val activity = context as Activity
        return activity.window?.decorView as FrameLayout
    }

    /**
     * 主页面的适配器,动态创建加载Fragment
     *
     * @return
     */
    @Suppress("DEPRECATION")
    inner class DesktopAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

            //创建时拿取数据
            return FragmentContent(
                position,
                desktopListData[position],
                iDesktopList,
                spanCount,
                appStyle,
                object : IItemViewInteractive {
                    override fun selectViewRect(
                        rect: RectF?,
                        selectView: View?,
                        adapterPosition: Int,
                        fragmentContent: FragmentContent
                    ) {
                        fromPagePosition = currentItem
                        fromAdapterPosition = adapterPosition
                        fromItemViewRect = rect
                        fromFragmentContent = fragmentContent
                        /*Log.d(
                            TAG,
                            "selectItemView:current page:${currentItem},接口返回： $selectView,选中position:$adapterPosition"
                        )*/
                        this@DesktopListView.fromItemView = selectView
                    }
                })
        }

        override fun getCount(): Int {
            return totalPage
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            currentFragmentContent = `object` as Fragment
            super.setPrimaryItem(container, position, `object`)
        }

        fun getInstantFragment(): Fragment? {
            return currentFragmentContent
        }
    }


    companion object {
        private const val TAG = "DesktopListView"
        private const val ACTION_PREV_PAGE = 1
        private const val ACTION_PREV_NEXT = 2
        const val SCROLL_STATE_IDLE = 0//浮动View禁止状态
        const val SCROLL_STATE_MOVE = 1//浮动View移动状态
    }
}