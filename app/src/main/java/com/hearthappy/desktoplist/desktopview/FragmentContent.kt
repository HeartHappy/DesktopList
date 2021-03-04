package com.hearthappy.desktoplist.desktopview

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.hearthappy.desktoplist.R
import com.hearthappy.desktoplist.appstyle.AppStyle
import com.hearthappy.desktoplist.interfaces.IBindDataModel
import kotlinx.android.synthetic.main.fragment_tab_main.*
import java.util.*
import kotlin.properties.Delegates


/**
 * Created Date 2020/12/21.
 * @author ChenRui
 * ClassDescription:动态创建的分页Fragment
 */
class FragmentContent : Fragment() {
    var position by Delegates.notNull<Int>()
    private var destroyPageAdapterSelPosition by Delegates.notNull<Int>() //默认：-1,如果遇到销毁页面，则是原选中适配器position,
    private var spanCount: Int by Delegates.notNull()
    private var verticalSpacing: Float = 0f
    private var appStyle: AppStyle by Delegates.notNull()
    private var iItemViewInteractive: IItemViewInteractive by Delegates.notNull()
    private lateinit var iLifeCycle: ILifeCycle
    private var desktopListAdapter: DesktopListAdapter? = null
    private var listData: MutableList<IBindDataModel> by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        arguments?.let {
            position = it.getInt(POSITION)
            destroyPageAdapterSelPosition = it.getInt(DESTROY_PAGE_ADAPTER_SEL_POSITION)
            spanCount = it.getInt(SPAN_COUNT)
            verticalSpacing = it.getFloat(VERTICAL_SPACING)
            appStyle = it.getSerializable(APP_STYLE) as AppStyle
            iItemViewInteractive = it.getParcelable<IItemViewInteractive>(I_ITEM_VIEW_INTERACTIVE) as IItemViewInteractive
            iLifeCycle = it.getParcelable<ILifeCycle>(I_Life_Cycle) as ILifeCycle
            listData = it.getParcelableArrayList<IBindDataModel>(MUTABLE_LIST) as ArrayList<IBindDataModel>
            iLifeCycle.onCreate(position)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        iLifeCycle.onCreateView(position)
        return inflater.inflate(R.layout.fragment_tab_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvDesktopList.layoutManager = GridLayoutManager(context, spanCount)

        desktopListAdapter = DesktopListAdapter(context, listData, iItemViewInteractive, rvDesktopList.parent).apply {
            appStyle = this@FragmentContent.appStyle
            this.fromPosition = this@FragmentContent.destroyPageAdapterSelPosition
            //                Log.d(TAG, "getValue: 初始化：$position,$destroyPageAdapterSelPosition")
        }

        //涉及数据绑定View的交给用户自定义
        rvDesktopList.adapter = desktopListAdapter
        rvDesktopList.itemAnimator?.changeDuration = 0
        rvDesktopList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                if (parent.getChildLayoutPosition(view) != 0) {
                    outRect.top = verticalSpacing.toInt()
                    outRect.bottom = verticalSpacing.toInt()
                }
            }
        })
        //绑定移动View
        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback())
        itemTouchHelper.attachToRecyclerView(rvDesktopList)
        iLifeCycle.onViewCreated(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        iLifeCycle.onDestroyView(position)
    }

    override fun onDestroy() {
        super.onDestroy()
        iLifeCycle.onDestroy(position)
    }


    internal fun getAdapter(): DesktopListAdapter? {
        rvDesktopList?.adapter?.let { return it as DesktopListAdapter } ?: let {
            return null
        }
    }

    fun getRecyclerView(): RecyclerView? {
        return rvDesktopList
    }

    fun replaceLocal(fromPosition: Int, toPosition: Int, listData: MutableList<*>) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(listData, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(listData, i, i - 1)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //        Log.d(TAG, "onResume: $position")
        if (::iLifeCycle.isInitialized) {
            iLifeCycle.onResume(position)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {

        if (!isVisibleToUser) {
            //如果隐藏了检测是否存在隐式插入的ItemView
            getAdapter()?.let {
                if (it.isImplicitInset()) {
                    it.implicitRemove()
                    //                    Log.d(TAG, "setUserVisibleHint: 不显示了，并且当前页面存在隐式View，执行删除")
                }
            }
        }
        if (::iLifeCycle.isInitialized) {
            iLifeCycle.onUserVisibleHint(isVisibleToUser, position)
        }
    }


    private inner class ItemTouchHelperCallback : ItemTouchHelper.Callback() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return if (recyclerView.layoutManager is GridLayoutManager) {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                val swipeFlags = 0
                makeMovementFlags(dragFlags, swipeFlags)
            } else {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = 0
                makeMovementFlags(dragFlags, swipeFlags)
            }
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

            /*//得到当拖拽的viewHolder的Position
            val fromPosition = viewHolder.adapterPosition
            //拿到当前拖拽到的item的viewHolder
            val toPosition = target.adapterPosition
            //            replaceLocal(fromPosition, toPosition, listData)
            rvDesktopList?.adapter?.run {
                //                Log.d("FragmentContent", "onMove: fromPosition:$fromPosition,toPosition:$toPosition")
                iItemViewInteractive.onMove(fromPosition, toPosition)
                notifyItemMoved(fromPosition, toPosition)
            }*/
            return true
        }


        /**
         * 滑动删除
         */
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }

        /**
         * 长按选中Item的时候开始调用
         *
         * @param viewHolder
         * @param actionState
         */
        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                viewHolder?.let { vh ->
                    vh.itemView.let { iv ->
                        iItemViewInteractive.selectViewRect(iv, vh.adapterPosition, this@FragmentContent)
                    }
                }
            }
            super.onSelectedChanged(viewHolder, actionState)
        }

        /**
         * 手指松开的时候还原
         * @param recyclerView
         * @param viewHolder
         */
        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            //            iItemViewInteractive.releaseView(viewHolder.itemView)
        }

        /**
         * 重写是否启用拖拽，true：启用
         * @return
         */
        override fun isLongPressDragEnabled(): Boolean {
            return true
        }
    }


    companion object {

        fun newInstance(position: Int, destroyPageAdapterSelPosition: Int, mutableList: MutableList<IBindDataModel>, spanCount: Int, verticalSpacing: Float, appStyle: AppStyle, iItemViewInteractive: IItemViewInteractive, iLifeCycle: ILifeCycle): Fragment {
            val fragmentContent = FragmentContent()
            val bundle = Bundle()
            bundle.putInt(POSITION, position)
            bundle.putInt(DESTROY_PAGE_ADAPTER_SEL_POSITION, destroyPageAdapterSelPosition)
            bundle.putInt(SPAN_COUNT, spanCount)
            bundle.putFloat(VERTICAL_SPACING, verticalSpacing)
            bundle.putSerializable(APP_STYLE, appStyle)
            bundle.putParcelable(I_ITEM_VIEW_INTERACTIVE, iItemViewInteractive)
            bundle.putParcelable(I_Life_Cycle, iLifeCycle)
            bundle.putParcelableArrayList(MUTABLE_LIST, mutableList as ArrayList<IBindDataModel>)
            fragmentContent.arguments = bundle
            return fragmentContent
        }

        private const val TAG = "FragmentContent"
        private const val POSITION = "position"
        private const val DESTROY_PAGE_ADAPTER_SEL_POSITION = "destroyPageAdapterSelPosition"
        private const val MUTABLE_LIST = "mutableList"
        private const val SPAN_COUNT = "spanCount"
        private const val VERTICAL_SPACING = "verticalSpacing"
        private const val APP_STYLE = "appStyle"
        private const val I_ITEM_VIEW_INTERACTIVE = "IItemViewInteractive"
        private const val I_Life_Cycle = "ILifeCycle"

    }
}


