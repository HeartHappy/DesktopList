package com.hearthappy.desktoplist.desktopview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.RectF
import android.os.Build
import android.os.Handler
import android.os.Parcel
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.IntRange
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.hearthappy.desktoplist.MyApplication
import com.hearthappy.desktoplist.R
import com.hearthappy.desktoplist.appstyle.AppStyle
import com.hearthappy.desktoplist.interfaces.IBindDataModel
import com.hearthappy.desktoplist.interfaces.IDesktopDataModel
import com.hearthappy.desktoplist.interfaces.ItemViewListener
import com.hearthappy.desktoplist.model.dao.DesktopDataDao
import com.hearthappy.desktoplist.model.table.DesktopDataTable
import com.hearthappy.desktoplist.transformpage.PagerTransformer
import com.hearthappy.desktoplist.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.annotations.NotNull
import kotlin.math.abs

/**
 * Created Date 2020/12/21.
 * @author ChenRui
 * ClassDescription:
 * 1、创建ViewPager和分页Fragment
 * 2、分页Fragment加载View绑定数据
 */
class DesktopListView(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {


    private var singlePageShowCount = 0 //每页显示个数
    private var spanCount = 3 //列数
    private var totalPage = 0 //总页数
    private var verticalSpacing = 0f //用于RecyclerView ItemView中上下间距 top、bottom


    private var fromItemViewRect: RectF? = null //选中ItemView的Rect，用来计算分发的x、y和选中ImageView偏移距离

    private var fromPagePosition = -1 //选中当前页面position（第几页）
    private var fromAdapterPosition = -1 //选中当前页面适配的position（第几个item）
    private var fromFragmentContent: FragmentContent? = null //选中ItemView来自的Fragment视图
    private var currentFragmentContent: FragmentContent? = null
    private var firstMoveTime = 0L
    private var floatViewScrollState = FloatViewScrollState.SCROLL_STATE_IDLE

    private var itemViewListener: ItemViewListener? = null
    private var appStyle: AppStyle = AppStyle.NotStyle

    //以下三个属性主要解决dispatchTouchEvent事件分发的X、Y与选中View存在偏移问题
    private var isFirstDispatch = false  //是否位按下时的第一次分发
    private var offsetX = 0f   //第一次分发时记录偏移量X
    private var offsetY = 0f
    private var touchX = 0f
    private var touchY = 0f

    private lateinit var orientation: Orientation

    //用户的数据源
    private var desktopListData: MutableList<MutableList<IBindDataModel>> = mutableListOf()

    private var isMessageSend = false //消息是否已发送,另代表是否在边界
    private var myHandler: Handler = Handler {
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
    }

    private lateinit var iDesktopDataModel: IDesktopDataModel<IBindDataModel>


    /**
     * 划分集合根据索引为条件
     * @receiver Iterable<T>
     * @param predicate Function2<Int, T, Boolean>
     * @return Pair<List<T>, List<T>>
     */
    private inline fun <T> Iterable<T>.partitionByIndex(predicate: (Int) -> Boolean): Pair<List<T>, List<T>> {
        val first = ArrayList<T>()
        val second = ArrayList<T>()
        forEachIndexed { index, element ->
            if (predicate(index)) {
                first.add(element)
            } else {
                second.add(element)
            }
        }
        return Pair(first, second)
    }

    /**
     * 请求FragmentContent操作
     * @param block [@kotlin.ExtensionFunctionType] Function1<FragmentContent, Unit>
     */
    private inline fun requestFromFragmentContent(block: (FragmentContent) -> Unit) {
        fromFragmentContent?.let { fc ->
            block(fc)
        }
    }

    private inline fun requestTargetFragmentContent(block: (FragmentContent) -> Unit) {
        val targetFragment = (adapter as DesktopAdapter).getInstantFragment() as FragmentContent
        block(targetFragment)
    }

    private inline fun requestByFragmentContent(fragmentContent: FragmentContent?, block: (FragmentContent) -> Unit) {
        fragmentContent?.let {
            block(it)
        }
    }

    /**
     * 请求DesktopListAdapter操作
     * @param block [@kotlin.ExtensionFunctionType] Function1<DesktopListAdapter, Unit>
     */
    private inline fun requestDesktopListAdapter(block: (DesktopListAdapter) -> Unit) {
        fromFragmentContent?.let { fc ->
            fc.getAdapter()?.let { adapter ->
                block(adapter)
            }
        }
    }


    /**
     * 请求DesktopListRecyclerView操作
     * @param block [@kotlin.ExtensionFunctionType] Function1<RecyclerView, Unit>
     */
    private inline fun requestDesktopListRecyclerView(fragmentContent: FragmentContent?, block: (RecyclerView) -> Unit) {
        fragmentContent?.let { fc ->
            fc.getRecyclerView()?.let {
                block(it)
            }
        }
    }

    private fun requestUpdaterViewPagerAdapter() {
        GlobalScope.launch(Dispatchers.Main) {
            adapter = DesktopAdapter((context as FragmentActivity).supportFragmentManager)
        }
    }

    private inline fun execute(updatePageAdapter: Boolean, crossinline block: (desktopDataDao: DesktopDataDao) -> Unit) {
        GlobalScope.launch {
            val desktopDataDao = getApplication().database.desktopDataDao()
            block(desktopDataDao)
            if (updatePageAdapter) {
                Log.d(TAG, "execute: 初始化适配器")
                requestUpdaterViewPagerAdapter()
            }
        }
    }

    /**
     * 尾递归查找TargetIndex
     * @param itemCount Int
     * @param targetIndex Int
     * @param recyclerView RecyclerView
     * @param moveViewRect RectF
     * @return Int
     */
    private tailrec fun findIntersectViewPosition(itemCount: Int, targetIndex: Int = 0, recyclerView: RecyclerView, moveViewRect: RectF): Int {
        var tempTargetIndex = targetIndex
        Log.d(TAG, "findIntersectViewPosition: $tempTargetIndex")
        val vh = recyclerView.findViewHolderForAdapterPosition(targetIndex)
        val itemView = vh?.itemView
        if (itemView != null) {
            val linearLayout = itemView as LinearLayout
            val imageView = linearLayout.getChildAt(0) as ImageView
            val targetViewRect = ViewOperateUtils.findViewLocation(imageView)
            return if (moveViewRect.intersect(targetViewRect) || moveViewRect.contains(targetViewRect)) {
                targetIndex
            } else {
                tempTargetIndex++
                if (tempTargetIndex < itemCount) {
                    findIntersectViewPosition(itemCount, tempTargetIndex, recyclerView, moveViewRect)
                } else {
                    itemCount
                }
            }
        }
        return 0
    }

    /**
     * 支持列数发生改变后，系统重新布局（调试时使用）
     * 支持横竖屏切换后，系统重新布局（主要针对平板设备使用）
     *
     * 1、初始化数据
     * 2、初始化总页数：根据数据的总数量和每页显示数量（默认每页显示15个）
     */
    fun init(@NotNull iDesktopList: IDesktopDataModel<IBindDataModel>, orientation: Orientation = Orientation.PORTRAIT, @IntRange(from = 3, to = 8) spanCount: Int = 4) {
        if (height <= 0 || !checkScreenChange()) {
            postDelayed({ init(iDesktopList, orientation, spanCount) }, 200)
            return
        }

        initializePageProperty(spanCount, iDesktopList)
        if (!checkSpanCountChangeByOrientation(orientation, spanCount)) {
            initPageData()
        }
    }


    /**
     * 初始化页面属性
     * @param spanCount Int
     * @param iDesktopList IDesktopDataModel<IBindDataModel>
     */
    private fun initializePageProperty(spanCount: Int, iDesktopList: IDesktopDataModel<IBindDataModel>) {
        this.spanCount = spanCount
        this.iDesktopDataModel = iDesktopList
        //        this.iDesktopListAdapter = iDesktopListAdapter
        val itemHeightSpan = resources.getDimensionPixelOffset(R.dimen.dp_105)
        val precisionRowsCount = ComputerUtils.getFloatLimit(height * 1f / itemHeightSpan).toFloat()
        val rowsCount = height / itemHeightSpan
        singlePageShowCount = rowsCount * spanCount
        verticalSpacing = (itemHeightSpan * (precisionRowsCount % rowsCount) / rowsCount / 2)
        Log.d(TAG, "initializePageProperty: 每页显示:$singlePageShowCount,行数:$rowsCount,height:$height,itemHeight:${resources.getDimensionPixelOffset(R.dimen.dp_105)},精确行数:$precisionRowsCount,垂直偏移:$verticalSpacing")
    }


    private fun initPageData() {
        val remoteDataSources = iDesktopDataModel.dataSources()
        execute(true) { dao ->
            val localDataSource = dao.queryByOrientation(orientation.value())
            Log.d(TAG, "initPageData  localDataSource count: ${localDataSource.size},remoteDataSources count:${remoteDataSources.size}")
            if (localDataSource.isEmpty()) {
                Log.d(TAG, "initPageData:使用网络数据： 本地数据为空，并初次保存至本地")
                conversionRemoteData(remoteDataSources)
                executeFirstInset(dao)
                //如果本地与网络数据源相同
            } else {
                val localAndRemoteMap = localAndRemoteMap(localDataSource, remoteDataSources)
                var queryMaxNumberOfPage = dao.queryMaxNumberOfPage(orientation.value())
                totalPage = ++queryMaxNumberOfPage
                if (localDataSource.size == remoteDataSources.size && localAndRemoteMap.first == localAndRemoteMap.second) {
                    Log.d(TAG, "initPageData: 使用本地数据库数据：本地存储数据与网络请求数据相同")
                    conversionLocalData(localDataSource)
                    //如果本地与网络不相同，并且本地已经有数据，需要同步更新
                } else if (localAndRemoteMap.first != localAndRemoteMap.second) {
                    //网络数据增加了
                    val conversionDifferentData = conversionDifferentData(localDataSource, remoteDataSources, localAndRemoteMap.first, localAndRemoteMap.second)
                    if (conversionDifferentData.first) {
                        insetLocalDataSource(dao, conversionDifferentData.second)
                        conversionLocalData(dao.queryByOrientation(orientation.value()))
                    } else {
                        executeSyncDelete(dao, conversionDifferentData.second)
                        conversionLocalData(dao.queryByOrientation(orientation.value()))
                    }
                }
            }
        }
    }


    /**
     * 检查当前屏幕下数量是否发生改变
     * @param orientation Orientation 屏幕方向
     * @param spanCount Int 该屏幕方向下显示的列数
     */
    private fun checkSpanCountChangeByOrientation(orientation: Orientation, spanCount: Int): Boolean {
        this.orientation = orientation
        return when (orientation) {
            Orientation.PORTRAIT -> {
                spanCountIsChange(spanCount, orientation, KEY_PORTRAIT_SPAN_COUNT, KEY_PORTRAIT_SINGLE_SHOW_COUNT)
            }
            Orientation.LANDSCAPE -> {
                spanCountIsChange(spanCount, orientation, KEY_LANDSCAPE_SPAN_COUNT, KEY_LANDSCAPE_SINGLE_SHOW_COUNT)
            }
        }
    }


    // TODO: 2021/3/5 首次进入后初始化了，通过关闭程序后，在此进入会存在死循环，一直未改变 。
    //  复现：竖屏显示后，退出程序，在竖屏进入死循环
    /**
     * 检查屏幕用户设置是否发生变化
     * @return Boolean true:代表宽高都已经改变了  false：还未执行onSize,代表还未改变宽高
     */
    private fun checkScreenChange(): Boolean {
        context.getSharedPreferences(SP_FILENAME) { sp ->

            val currentScreenOrientation = sp toInt KEY_CURRENT_SCREEN_ORIENTATION
            //首次进入已经改变
            if (currentScreenOrientation == 0) {
                sp.editApplySaveByName { it.putInt(KEY_CURRENT_SCREEN_ORIENTATION, width * height) }
                Log.d(TAG, "checkScreenChange: 初始化屏幕宽高")
                return true
            }
            //
            //如果存储的宽*高/除以现阶段的宽不等于高，说明已经改变
            if (currentScreenOrientation / width != height) {
                sp.editApplySaveByName { it.putInt(KEY_CURRENT_SCREEN_ORIENTATION, width * height) }
                Log.d(TAG, "checkScreenChange: 屏幕宽高已改变")
                return true
            }
            Log.d(TAG, "checkScreenChange: 屏幕宽高还未改变")
            return false
        }
        return false
    }

    /**
     * 列数是否在当前屏幕方向下发生改变，如果改变则删除原缓存数据，进行重布局
     * @param spanCount Int 更改列数
     * @param orientation Orientation 屏幕方向
     * @param keySpanCount String 存储列数的Key
     * @param keySinglePageShowCount String 存储每页显示数量的Key
     */
    private fun spanCountIsChange(spanCount: Int, orientation: Orientation, keySpanCount: String, keySinglePageShowCount: String): Boolean {
        context.getSharedPreferences(SP_FILENAME) { sp ->
            val storageSpanCount = sp toInt keySpanCount
            val storageSinglePageShowCount = sp toInt keySinglePageShowCount
            val isChange = storageSpanCount != 0 && storageSpanCount != spanCount || storageSinglePageShowCount != singlePageShowCount
            Log.d(TAG, "spanCountIsChange: $orientation,是否更改:$isChange,存储列数：$storageSpanCount,更改为：$spanCount,存储显示个数:$storageSinglePageShowCount,更改为：$singlePageShowCount")
            sp.editApplySaveByName {
                it.putInt(keySpanCount, spanCount)
                it.putInt(keySinglePageShowCount, singlePageShowCount)
            }
            if (isChange) {
                executeDeleteByOrientation(orientation)
                return true
            }
        }
        return false
    }

    fun setAppStyle(appStyleType: AppStyle): DesktopListView {
        this.appStyle = appStyleType
        return this
    }

    /**
     * Parameters only allowed to be Windmill, FloatUp, Translate
     *
     * @param animSpecies Animation Species
     */
    fun setTransformAnimation(animSpecies: PagerTransformer.AnimSpecies): DesktopListView {
        //        Log.d(TAG, "transformAnimation: ${animSpecies.type()}")
        setPageTransformer(true, PagerTransformer(animSpecies))
        return this
    }


    /**
     * 通知页面样式发生改变
     */
    fun notifyChangeStyle() {
        requestUpdaterViewPagerAdapter()
    }

    /**
     * 通知刷新当前页面
     */
    fun notifyUpdateCurrentPage() {
        requestTargetFragmentContent { it.getAdapter()?.notifyDataChanged() }
    }

    fun setDesktopAdapterListener(itemViewListener: ItemViewListener) {
        this.itemViewListener = itemViewListener
    }

    /**
     *  通知抖动
     */
    private fun notifyJitter() {
        val supportFragmentManager = (context as FragmentActivity).supportFragmentManager
        val fragments = supportFragmentManager.fragments
        fragments.forEach {
            if (it is FragmentContent) {
                it.getAdapter()?.notifyDataChanged()
            }
        }
    }


    /**
     * 网络数据转换
     * @param dataSources List<IBindDataModel>
     */
    private fun conversionRemoteData(dataSources: List<IBindDataModel>) {
        //将集合按照数量分块，最后一块较小
        val block = dataSources.chunked(singlePageShowCount) { chunk -> desktopListData.add(chunk.toMutableList()) }
        //计算总页数
        totalPage = block.size
        Log.d(TAG, "dataConversion: 网络数据转换成每页数据，并初次写入本地,页数：$totalPage,每页显示个数:$singlePageShowCount")
        desktopListData.forEachIndexed { index, mutableList ->
            Log.d(TAG, "conversionRemoteData: 当前页数:$index,每页显示数量：${mutableList.size}")
            mutableList.forEachIndexed { i, iBindDataModel ->
                Log.d(TAG, "conversionRemoteData: $i,${iBindDataModel.getAppName()}")
            }
        }
    }


    /**
     * 本地数据转换
     * @param queryDesktopData List<DesktopDataTable>
     */
    private fun conversionLocalData(queryDesktopData: List<DesktopDataTable>) {
        val groupBySort = queryDesktopData.groupBy { it.pageNumber }.toSortedMap()
        totalPage = groupBySort.size
        groupBySort.forEach { group ->
            //得到划分的页面数据
            val dividePageData = group.value.sortedBy { it.pageAdapterPosition }
            desktopListData.add(dividePageData.toMutableList())
            Log.d(TAG, "conversionLocalData:当前页数: ${group.key}")
            dividePageData.forEach { Log.d(TAG, "groupBySort conversionLocalData: ${it.title},page:${it.pageNumber},position:${it.pageAdapterPosition},or:${it.orientation}") }
        }
    }


    /**
     * 转换提取出不同数据
     * @param localList List<IBindDataModel>
     * @param remoteList List<IBindDataModel>
     * @return Pair(true:插入、false:删除,查询出不同的数据)
     */
    private fun conversionDifferentData(localList: List<IBindDataModel>, remoteList: List<IBindDataModel>, localMap: List<String>, remoteMap: List<String>): Pair<Boolean, List<IBindDataModel>> {
        val differentDataModel = mutableListOf<IBindDataModel>()
        val isInset = remoteMap.size > localMap.size
        Log.d(TAG, "conversionDifferentData: 是否插入否则删除：$isInset")
        if (isInset) {
            val differentMap = remoteMap.minus(localMap)
            differentMap.forEach { different ->
                Log.d(TAG, "conversionDifferentData:本地需要插入：$different")
                remoteList.find { it.getAppName() == different }?.let { differentDataModel.add(it) }
            }
        } else {
            val differentMap = localMap.minus(remoteMap)
            differentMap.forEach { different ->
                Log.d(TAG, "conversionDifferentData: 本地需要删除：$different")
                localList.find { it.getAppName() == different }?.let { differentDataModel.add(it) }
            }
        }
        return Pair(isInset, differentDataModel)
    }


    /**
     * 插入本地数据库
     * @param dao DesktopDataDao
     * @param insetDataList List<IBindDataModel>
     */
    private fun insetLocalDataSource(dao: DesktopDataDao, insetDataList: List<IBindDataModel>) {
        //获取最后一页的显示数量，是否允许增加
        var lastPageIndex = totalPage - 1
        val lastPageShowNumber = dao.queryPageShowNumber(lastPageIndex, orientation.value()).size

        //最有一页最多插入条目
        val lastPageMaxInsetNumber = singlePageShowCount - lastPageShowNumber
        //划分当前页最多插入数量和插入其他页面的所有数据
        val partitionByIndex = insetDataList.partitionByIndex { it < lastPageMaxInsetNumber }
        //将插入其他页的分块
        val chunked = partitionByIndex.second.chunked(singlePageShowCount)
        //插入每页的数据
        val insertCollectionPerPage = mutableListOf<List<IBindDataModel>>()
        if (partitionByIndex.first.isNotEmpty()) {
            insertCollectionPerPage.add(partitionByIndex.first)
        } else {
            ++lastPageIndex
        }
        chunked.forEach { insertCollectionPerPage.add(it) }
        Log.d(TAG, "insetLocalDataSource: 需要插入数据库：${insetDataList.size}条,分：${insertCollectionPerPage.size}页插入")
        insertCollectionPerPage.forEachIndexed { index, list ->
            //有可能直接从新页面开始
            val pageIndex = index + lastPageIndex
            val pageAdapterIndex = dao.queryPageShowNumber(pageIndex, orientation.value()).size
            Log.d(TAG, "insetLocalDataSource: 插入索引为第：$pageIndex 页，开始位置索引:$pageAdapterIndex，该页插入:${list.size}条")
            executeSyncInset(dao, list, pageIndex, pageAdapterIndex)
        }
    }

    /**
     * 首次将网络数据写入本地数据库
     * @param dao DesktopDataDao
     */
    private fun executeFirstInset(dao: DesktopDataDao) {
        Log.d(TAG, "saveLocalDataSource: inset all data")
        for (i in 0 until totalPage) {
            val numberPerPage = desktopListData[i].size
            for (j in 0 until numberPerPage) {
                dao.insert(DesktopDataTable(title = desktopListData[i][j].getAppName(), url = desktopListData[i][j].getAppUrl(), pageNumber = i, pageAdapterPosition = j, orientation = this.orientation.value()))
            }
        }
    }


    /**
     * 更新本地数据库
     */
    private fun executeSyncUpdate() {
        execute(false) { dao ->
            //            Log.d(TAG, "executeSyncUpdate: 更新数据改变")
            for (i in 0 until totalPage) {
                val numberPerPage = desktopListData[i].size
                //                Log.d(TAG, "executeSyncUpdate number per Page----->: $numberPerPage")
                for (j in 0 until numberPerPage) {
                    //                    Log.d(TAG, "executeSyncUpdate: ${desktopListData[i][j].getAppName()},page:$i,position:$j")
                    dao.update(title = desktopListData[i][j].getAppName(), url = desktopListData[i][j].getAppUrl(), pageNumber = i, pageAdapterPosition = j, orientation.value())
                }
            }
        }
    }


    /**
     * 执行同步插入
     * @param dao DesktopDataDao
     * @param insetDataList List<IBindDataModel>  插入页面数据
     * @param insetPageIndex Int  插入哪一页
     * @param insetPageAdapterIndex Int 插入页面第几个位置
     */
    private fun executeSyncInset(dao: DesktopDataDao, insetDataList: List<IBindDataModel>, insetPageIndex: Int, insetPageAdapterIndex: Int) {
        ++totalPage
        var insetIndex = insetPageAdapterIndex
        insetDataList.forEachIndexed { _, insetData ->
            Log.d(TAG, "executeSyncInset: sync inset:${insetData.getAppName()}")
            dao.insert(DesktopDataTable(title = insetData.getAppName(), url = insetData.getAppUrl(), pageNumber = insetPageIndex, pageAdapterPosition = insetIndex, orientation = orientation.value()))
            ++insetIndex
        }
    }

    /**
     * 执行同步删除
     * @param deleteDataList List<IBindDataModel>
     */
    private fun executeSyncDelete(dao: DesktopDataDao, deleteDataList: List<IBindDataModel>) {
        deleteDataList.forEachIndexed { _, deleteData ->
            val deleteByName = dao.deleteByName(deleteData.getAppName(), orientation.value())
            Log.d(TAG, "deleteLocalDataSource: 同步删除:${deleteData.getAppName()},是否成功:$deleteByName")
        }
    }

    /**
     * 按照屏幕方向删除数据，需要重新布局和初始化
     * @param orientation Orientation
     */
    private fun executeDeleteByOrientation(orientation: Orientation) {
        Log.d(TAG, "executeDeleteAll: $orientation")
        execute(false) { dao ->
            //            val queryByOrientation = dao.queryByOrientation(orientation = orientation.value())
            //            val deleteByOrientation2 = dao.deleteByOrientation2(queryByOrientation.toTypedArray())
            val deleteByOrientation = dao.deleteByOrientation(orientation = orientation.value())
            desktopListData.clear()
            Log.d(TAG, "executeDeleteByOrientation: $deleteByOrientation")
            initPageData()
        }
    }


    /**
     * 集合根据appName排序后，在进行map映射新的集合
     * @param localList List<IBindDataModel>
     * @param remoteList List<IBindDataModel>
     * @return Pair<List<String>, List<String>>
     */
    private fun localAndRemoteMap(localList: List<IBindDataModel>, remoteList: List<IBindDataModel>): Pair<List<String>, List<String>> {
        val localListSort = localList.toMutableList()
        val remoteListSort = remoteList.toMutableList()
        val localMap = localListSort.map { it.getAppName() }
        val remoteMap = remoteListSort.map { it.getAppName() }
        return Pair(localMap, remoteMap)
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        try {
            ev?.let {
                if (it.pointerCount == 2) return false
                when (it.action) {
                    MotionEvent.ACTION_MOVE -> {
                        val moveFloatView = getFloatView()
                        moveFloatView?.let { mfv ->
                            it.let { e ->
                                //移动浮动View坐标
                                dragToMoveTheSuspensionView(mfv, e)
                                //跨界面移动
                                if (fromPagePosition != currentItem && fromPagePosition != -1) {
                                    //                                  Log.d(TAG, "onScroll:跨界面移动 floatView拖拽移动至第${currentItem}页,原选中ItemView的页面是：$fromPagePosition,状态${touchStateIsSwipe(it.rawX, it.rawY)}")
                                    onMoveCrossPage(it, mfv)
                                    return false
                                } else {
                                    onMoveFromPage(it, mfv)
                                }
                            }
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        return if (isExistFloatView()) {
                            onReleaseSelectItemView()
                            false
                        } else {
                            //                            Log.d(TAG, "dispatchTouchEvent: not floatView up")
                            super.dispatchTouchEvent(ev)
                        }
                    }
                    else -> {
                        return super.dispatchTouchEvent(ev)
                    }
                }
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return false
        }
        return super.dispatchTouchEvent(ev)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        try {
            if (fromPagePosition == currentItem && isExistFloatView()) {
                return true
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return true
        }
        return super.onTouchEvent(ev)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(TAG, "onSizeChanged: w:$w,h:$h,oldW:$oldw,oldH:$oldh")
    }


    /**
     * 创建浮动View
     */
    private fun createFloatView(selectView: View?) {
        val frameLayout = getDecorView()
        //将原ImageView属性copy
        val linearLayout = selectView as LinearLayout
        val imageView = linearLayout.getChildAt(0) as ImageView

        fromItemViewRect = ViewOperateUtils.findViewLocation(imageView)
        fromItemViewRect?.let {
            val tempImageView = ImageFilterView(context)
            val imageViewLayoutParams = FrameLayout.LayoutParams(imageView.width, imageView.height)
            setFloatViewStyle(tempImageView)
            tempImageView.apply {
                //切换样式
                setImageDrawable(imageView.drawable)
                scaleType = imageView.scaleType
                id = R.id.floatView
                //设置View位置
                imageViewLayoutParams.apply {
                    setMargins(it.left.toInt(), it.top.toInt(), 0, 0)
                    layoutParams = imageViewLayoutParams
                    //添加DecorView到视图
                    frameLayout.addView(tempImageView, this)
                }
                animate().scaleX(1.4f).scaleY(1.4f).start()
            }
        }
    }


    /**
     * 设置浮动View样式
     * @param tempImageView ImageFilterView
     */
    private fun setFloatViewStyle(tempImageView: ImageFilterView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tempImageView.round = when (appStyle) {
                is AppStyle.Circle -> context?.let { (it.resources.getDimensionPixelSize(R.dimen.dp_52) / 2).toFloat() } ?: let { 0f }
                is AppStyle.Rounded -> (appStyle as AppStyle.Rounded).radius.toFloat()
                is AppStyle.NotStyle -> 0f
            }
        }
    }


    /**
     * 点击ItemView
     * @param position Int
     * @param list List<IBindDataModel>
     */
    private fun onClickItemView(position: Int, list: List<IBindDataModel>) {
        itemViewListener?.onClick(position, list)
    }

    /**
     * 选中ItemView
     * @param selectView View?
     * @param adapterPosition Int
     * @param fragmentContent FragmentContent
     */
    private fun onSelectItemView(selectView: View?, adapterPosition: Int, fragmentContent: FragmentContent) {
        fromPagePosition = currentItem
        fromFragmentContent = fragmentContent
        createFloatView(selectView)
        requestFromFragmentContent {
            it.getAdapter()?.hideFromPosition(adapterPosition)
            fromAdapterPosition = adapterPosition
        }
        notifyJitter()
    }

    /**
     *
     * 处理来自页面移动
     */
    private fun onMoveFromPage(motionEvent: MotionEvent, mfv: View) {
        //        Log.d(TAG, "handlerFromPageMove:fromPagePosition:$fromPagePosition")
        //        if (fromPageIsCreateAlterDestroy(fromPagePosition) && destroyFragmentPosition != -1 && fromPagePosition == destroyFragmentPosition) {
        requestFromFragmentContent { ffc ->
            ffc.getAdapter()?.let { adapter ->
                //如果为静止状态
                if (!touchStateIsSwipe(motionEvent.rawX, motionEvent.rawY)) {
                    val targetIndex = getTargetIndexEfficient(mfv, ffc)
                    Log.d(TAG, "handlerFromPageMove: $targetIndex")
                    //移动交换位置
                    adapter.checkFromPageIsMove(ffc, mfv, targetIndex)
                }
            } ?: let {
                fromFragmentContent = currentFragmentContent
            }
        }
        //        }
    }


    /**
     * 处理跨界面移动：
     * @param it MotionEvent
     * @param mfv View
     */
    private fun onMoveCrossPage(it: MotionEvent, mfv: View) {
        requestTargetFragmentContent { tfc ->
            //如果为静止状态
            if (!touchStateIsSwipe(it.rawX, it.rawY)) {
                val targetIndex = getTargetIndexEfficient(mfv, tfc)

                val adapter = tfc.getAdapter()
                adapter?.run {
                    Log.d(TAG, "handlerCrossPageMove: $targetIndex,$itemCount")
                    //静止状态下，存在隐式View
                    if (isImplicitInset()) {
                        //移动交换位置
                        checkCrossPageIsMove(tfc, mfv, targetIndex)
                        //静止状态下，不存在，则插入
                    } else if (!isImplicitInset() && itemCount < singlePageShowCount && targetIndex != itemCount) {
                        //                    Log.d(TAG, "dispatchTouchEvent:插入目标位置：${targetIndex}")
                        implicitInset(targetIndex, desktopListData[fromPagePosition][fromAdapterPosition])
                    }
                }
            }
        }
    }


    /**
     * 释放选中View
     */
    private fun onReleaseSelectItemView() {
        val floatView = getFloatView()
        floatView?.let { mfv ->
            isFirstDispatch = false
            onReleaseHandler(mfv)
            //3、删除临时视图
            removeFloatView(mfv)

            //刷新原界面视图
            requestFromFragmentContent { ffc ->
                Log.d(TAG, "onReleaseSelectItemView: request update view")
                if (ffc.isAdded) {
                    postDelayed({
                        Log.d(TAG, "onReleaseSelectItemView: request update view succeed")
                        requestDesktopListAdapter {
                            Log.d(TAG, "onReleaseSelectItemView: showFromPosition")
                            it.showFromPosition()
                            Log.d(TAG, "onReleaseSelectItemView: notifyDataChanged")
                            it.notifyDataChanged()
                        }
                        fromAdapterPosition = -1
                    }, 200)
                } else {
                    Log.d(TAG, "onReleaseSelectItemView: request update view failed ,not add fragment to activity")
                }
            }
            Log.d(TAG, "onReleaseSelectItemView--->remove float View")
            executeSyncUpdate()
        }
        notifyJitter()
    }


    /**
     * 释放处理
     */
    private fun onReleaseHandler(mfv: View) {
        requestTargetFragmentContent { tfc ->
            val targetIndex = getTargetIndexEfficient(mfv, tfc)
            //            Log.d(TAG, "findReplaceView: $fromAdapterPosition,$targetIndex")
            tfc.getAdapter()?.let {
                when {
                    //1、如果是当前页面
                    fromPagePosition == currentItem -> {
                        //1.1、如果是原位置，则直接返回
                        onReleasedHandlerInCurrentPage(targetIndex, it, tfc)
                    }
                    //2、松开时，拖拽至其他页面，并且其他页面是满的。此时targetIndex=-1
                    targetIndex == -1 -> {
                        Toast.makeText(context, "当前屏幕没有空间", Toast.LENGTH_SHORT).show()
                        requestFromFragmentContent { ffc -> ffc.getAdapter()?.notifyDataChanged() }
                    }
                    //3、松开时，跨界面
                    fromPagePosition != currentItem -> {
                        onReleasedHandlerInCrossPage(it, tfc, targetIndex)
                    }
                }
            }
        }
    }


    /**
     * 检查来自页面是否需要移动新列表ItemView位置
     * @receiver DesktopListAdapter
     * @param fc FragmentContent
     * @param mfv View
     * @param targetIndex Int
     */
    private fun DesktopListAdapter.checkFromPageIsMove(fc: FragmentContent, mfv: View, targetIndex: Int) {
        Log.d(TAG, "checkNeedMoveByDesktopPage--> destroyPageAdapterSelPosition:$fromPosition,fromAdapterPosition:${fromAdapterPosition},targetPosition:$targetIndex")
        if (targetIndex == -1) return
        val targetViewHolderRect = fc.getRecyclerView()?.findViewHolderForAdapterPosition(targetIndex)
        targetViewHolderRect?.itemView?.let {
            if (ViewOperateUtils.findViewLocation(mfv).intersect(ViewOperateUtils.findViewLocation(it.findViewById(R.id.appIcon)))) {
                moveFromPosition(fromPosition, targetIndex)
            }
        }
    }

    /**
     * 跨界面移动：
     * 检查是否需要移动隐式ItemView位置
     */
    private fun DesktopListAdapter.checkCrossPageIsMove(targetFragment: FragmentContent, mfv: View, targetIndex: Int) {
        if (getImplicitPosition() == -1) return
        val implicitViewHolder = targetFragment.getRecyclerView()?.findViewHolderForAdapterPosition(getImplicitPosition())
        implicitViewHolder?.itemView?.let { iv ->
            val implicitImageViewRect = ViewOperateUtils.findViewLocation(iv.findViewById(R.id.appIcon))
            val intersect = implicitImageViewRect.intersect(ViewOperateUtils.findViewLocation(mfv))
            val contains = implicitImageViewRect.contains(ViewOperateUtils.findViewLocation(mfv))
            if (!intersect && !contains && getImplicitPosition() != targetIndex) {
                //                Log.d(TAG, "dispatchTouchEvent:当前移动位置与隐式插入View不相交,原位置：${getImplicitPosition()},移动目标位置：${targetIndex}")
                moveCrossPosition(getImplicitPosition(), if (targetIndex != itemCount) targetIndex else targetIndex - 1)
            }
        }
    }

    /**
     * remove floatView
     * @param mfv View
     */
    private fun removeFloatView(mfv: View) {
        getDecorView().removeView(mfv)
    }

    /**
     * floating View released in cross page
     * @param it DesktopListAdapter
     * @param targetFragment FragmentContent
     * @param targetIndex Int
     */
    private fun onReleasedHandlerInCrossPage(it: DesktopListAdapter, targetFragment: FragmentContent, targetIndex: Int) {
        Log.d(TAG, "floatViewReleasedInCrossPage")
        //如果已经存在隐式
        if (it.isImplicitInset()) {
            if (it.getImplicitPositionIsChange()) {
                moveUpChangeDataSource(targetFragment, it.getImplicitPositionInFirstInset(), targetIndex, currentItem)
            } else {
                it.notifyDataChanged()
            }
            it.resetImplicitPosition()
        } else {
            val dataModel = desktopListData[fromPagePosition][fromAdapterPosition]
            it.inset(targetIndex, dataModel)
        }
        //跨界面删除，由于页面被销毁了，所以需要修改为删除数据源
        desktopListData[fromPagePosition].removeAt(fromAdapterPosition)
    }

    /**
     * The floating view is released in the current interface
     * @param targetIndex Int
     * @param it DesktopListAdapter
     * @param targetFragment FragmentContent
     * @return Boolean
     */
    private fun onReleasedHandlerInCurrentPage(targetIndex: Int, it: DesktopListAdapter, targetFragment: FragmentContent): Boolean {
        if (targetIndex == fromAdapterPosition) {
            it.notifyItemChanged(fromAdapterPosition)
            Log.d(TAG, "findReplaceView: 当前页，原位置释放")
            return true
        }
        Log.d(TAG, "findReplaceView: 当前页，则执行替换操作")
        //1.2、如果不是原位置，则数据源位置改变
        moveUpChangeDataSource(targetFragment, fromAdapterPosition, targetIndex, fromPagePosition)
        return false
    }

    /**
     * 移动后改变数据源并进行重新绑定
     */
    private fun moveUpChangeDataSource(targetFragment: FragmentContent, fromIndex: Int, targetIndex: Int, fromPagePosition: Int) {
        try {
            //通过以上步骤，再对数据进行重新绑定，数据源与视图重新绑定.
            targetFragment.getAdapter()?.let {
                it.moveDataModel(fromIndex, targetIndex, desktopListData[fromPagePosition])
                postDelayed({
                    Log.d(TAG, "moveChangeDataSource: 刷新当前界面视图")
                    it.notifyDataChanged()
                }, 200)
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            Log.e(TAG, "moveChangeDataSource:fromIndex: $fromIndex,targetIndex:$targetIndex")
        }
    }

    /**
     * 拖拽移动悬浮View
     */
    private fun dragToMoveTheSuspensionView(mfv: View, it: MotionEvent) {
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
            floatViewScrollState = FloatViewScrollState.SCROLL_STATE_MOVE
            //            Log.d(TAG, "touchStateIsSwipe: 移动中")
        } else {
            if (floatViewScrollState == FloatViewScrollState.SCROLL_STATE_MOVE && firstMoveTime == 0L) {
                firstMoveTime = System.currentTimeMillis()
                //                Log.d(TAG, "touchStateIsSwipe: 可能发生静止时间")
            } else if (secondTime - firstMoveTime >= 200 && firstMoveTime != 0L && !isMessageSend && floatViewScrollState == FloatViewScrollState.SCROLL_STATE_MOVE) {
                floatViewScrollState = FloatViewScrollState.SCROLL_STATE_IDLE
                Log.d(TAG, "touchStateIsSwipe:完全静止 ${secondTime - firstMoveTime},firstMoveTime:$firstMoveTime")
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
                /*Log.d(TAG,"isSwitchViewPager: ${mfv.x},${mfv.width},$width,${(mfv.x + mfv.width) > width}")*/
            }
        }
    }

    /**
     * 返回Target页面的TargetItem的Index
     */
    /* private fun getTargetIndex(mfv: View, fragmentContent: FragmentContent): Int {
         //1、确定释放位置
         val moveViewRect = ViewOperateUtils.findViewLocation(mfv)
         //        Log.d(TAG, "findReplaceView--->moveViewRect: $moveViewRect")
         requestByFragmentContent(fragmentContent) { fc ->
             fc.getAdapter()?.itemCount?.let { cic ->
                 if (cic == singlePageShowCount && fromPagePosition != currentItem) {
                     return -1
                 }
                 var localIntersect = -1 //原位置相交
                 var targetIntersect = -1 //目标位置相交
                 for (i in 0 until cic) {
                     val vh = fc.getRecyclerView()?.findViewHolderForLayoutPosition(i)
                     vh?.itemView?.let { iv ->
                         val linearLayout = iv as LinearLayout
                         val imageView = linearLayout.getChildAt(0) as ImageView
                         val targetViewRect = ViewOperateUtils.findViewLocation(imageView)
                         if (moveViewRect.intersect(targetViewRect)) {
                             if (i != fromAdapterPosition) {
                                 targetIntersect = i
                                 //                                Log.d(TAG, "getTargetIndex:$i,目标相交,选中$fromAdapterPosition,mvr:$moveViewRect,target:$targetViewRect")
                             } else {
                                 localIntersect = i
                                 //                                Log.d(TAG, "getTargetIndex:$i,原位置相交,选中$fromAdapterPosition,mvr:$moveViewRect,target:$targetViewRect")
                             }
                         }
                     }
                 }
                 if (targetIntersect != -1) {
                     return targetIntersect
                 } else if (localIntersect != -1 && targetIntersect == -1) {
                     return localIntersect
                 }
                 return cic
             }
         }
         return 0
     }*/


    /**
     *
     * 优化获取TargetIndex，采用尾递归查找
     * @param mfv View
     * @param fragmentContent FragmentContent
     * @return Int targetIndex:1、 -1:   2、itemCount:添加至最后一个  3、0~itemCount:交换位置
     */
    private fun getTargetIndexEfficient(mfv: View, fragmentContent: FragmentContent): Int {
        //1、确定释放位置
        val moveViewRect = ViewOperateUtils.findViewLocation(mfv)
        //        Log.d(TAG, "findReplaceView--->moveViewRect: $moveViewRect")
        requestByFragmentContent(fragmentContent) { fc ->
            fc.getAdapter()?.let { adapter ->
                adapter.itemCount.let { ic ->
                    if (ic == singlePageShowCount && fromPagePosition != currentItem && !adapter.isImplicitInset()) {
                        return -1
                    }
                    requestDesktopListRecyclerView(fc) { rv ->
                        val targetIndex = findIntersectViewPosition(itemCount = ic, recyclerView = rv, moveViewRect = moveViewRect)
                        Log.d(TAG, "getTargetIndexEfficient:targetIndex: $targetIndex,itemCount:$ic")
                        if (targetIndex == ic) {
                            return ic
                        } else if (targetIndex != -1) {
                            return targetIndex
                        }
                    }
                }
            }
        }
        return 0
    }


    private fun getFloatView(): View? {
        return getDecorView().findViewById(R.id.floatView)
    }

    /**
     * 是否存在FloatView
     * @return Boolean
     */
    internal fun isExistFloatView(): Boolean {
        getFloatView()?.let {
            return true
        } ?: let {
            return false
        }
    }

    internal fun getAppStyle(): AppStyle {
        return appStyle
    }


    private fun getDecorView(): FrameLayout {
        val activity = context as Activity
        return activity.window?.decorView as FrameLayout
    }

    private fun getApplication(): MyApplication {
        return context.applicationContext as MyApplication
    }


    /**
     * 主页面的适配器,动态创建加载Fragment
     *
     * @return
     */
    @Suppress("DEPRECATION")
    internal inner class DesktopAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            var fromPageAdapterPosition = -1
            if (position == fromPagePosition && fromAdapterPosition != -1 && isExistFloatView()) {
                fromPageAdapterPosition = fromAdapterPosition
                //                Log.d(TAG, "getItem:1 创建:${fromPageIsDestroy(position)},position:$position,fromPageAdapterPosition:$fromPageAdapterPosition")
            }
            //创建时拿取数据
            return FragmentContent.newInstance(position, fromPageAdapterPosition, desktopListData[position], spanCount, verticalSpacing, appStyle, object : IItemViewInteractive {

                override fun selectViewRect(selectView: View?, adapterPosition: Int, fragmentContent: FragmentContent) {
                    onSelectItemView(selectView, adapterPosition, fragmentContent)
                    //                    Log.d(TAG, "selectItemView:current page:${currentItem},interface result： $selectView,select position:$adapterPosition")
                }

                override fun onClick(position: Int, list: List<IBindDataModel>) {
                    onClickItemView(position, list)
                }

                override fun onMove(fromPosition: Int, toPosition: Int) {
                }

                override fun describeContents(): Int {
                    return 0
                }

                override fun writeToParcel(dest: Parcel?, flags: Int) {
                }
            }, object : ILifeCycle {

                override fun onCreate(position: Int) {
                }

                override fun onCreateView(position: Int) {
                }

                override fun onViewCreated(position: Int) {
                }

                override fun onResume(position: Int) {

                }

                override fun onDestroyView(position: Int) {
                    //                    Log.d(ILifeCycle_TAG, "onDestroyView: $position")
                }

                override fun onDestroy(position: Int) {
                }

                override fun onUserVisibleHint(visibleToUser: Boolean, position: Int) {
                }


                override fun describeContents(): Int = 0

                override fun writeToParcel(dest: Parcel?, flags: Int) {}
            })
        }

        override fun getCount(): Int {
            return totalPage
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            currentFragmentContent = `object` as FragmentContent
            super.setPrimaryItem(container, position, `object`)
        }

        fun getInstantFragment(): Fragment? {
            return currentFragmentContent
        }
    }


    enum class FloatViewScrollState {
        SCROLL_STATE_IDLE, //floatView 静止状态，
        SCROLL_STATE_MOVE, //floatView 滑动状态
    }

    enum class Orientation : IOrientationType {
        LANDSCAPE {
            override fun value(): Int = 1
        },
        PORTRAIT {
            override fun value(): Int = 2
        }
    }


    companion object {
        private const val TAG = "DesktopListView"
        //        private const val ILifeCycle_TAG = "ILifeCycle"

        private const val ACTION_PREV_PAGE = 1
        private const val ACTION_PREV_NEXT = 2
    }
}

