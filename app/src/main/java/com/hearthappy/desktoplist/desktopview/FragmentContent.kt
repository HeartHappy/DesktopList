package com.hearthappy.desktoplist.desktopview

import android.os.Bundle
import android.util.Log
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
import com.hearthappy.desktoplist.interfaces.IDesktopListAdapter
import kotlinx.android.synthetic.main.fragment_tab_main.*
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KProperty


/**
 * Created Date 2020/12/21.
 * @author ChenRui
 * ClassDescription:动态创建的分页Fragment
 */
class FragmentContent : Fragment() {
    var position by Delegates.notNull<Int>()
    var listData: MutableList<IBindDataModel> by Delegates.notNull()
    var spanCount: Int by Delegates.notNull()
    var appStyle: AppStyle by Delegates.notNull()
    var iDesktopListAdapter: IDesktopListAdapter by Delegates.notNull()
    var iItemViewInteractive: IItemViewInteractive by Delegates.notNull()
    private val desktopListAdapter: DesktopListAdapter by Delegate()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        arguments?.let {
            position = it.getInt(POSITION)
            spanCount = it.getInt(SPANCOUNT)
            appStyle = it.getSerializable(APPSTYLE) as AppStyle
            iDesktopListAdapter =
                it.getParcelable<IDesktopListAdapter>(IDESKTOPLISTADAPTER) as IDesktopListAdapter
            iItemViewInteractive =
                it.getParcelable<IItemViewInteractive>(IITEMVIEWINTERACTIVE) as IItemViewInteractive
            listData =
                it.getParcelableArrayList<IBindDataModel>(MUTABLELIST) as ArrayList<IBindDataModel>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gridLayoutManager = GridLayoutManager(context, spanCount)
        rvDesktopList.layoutManager = gridLayoutManager
        //涉及数据绑定View的交给用户自定义
        rvDesktopList.adapter = desktopListAdapter
        rvDesktopList.itemAnimator?.changeDuration = 0
        //绑定移动View
        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback())
        itemTouchHelper.attachToRecyclerView(rvDesktopList)
    }


    internal fun getAdapter(): DesktopListAdapter? {
        rvDesktopList?.adapter?.let { return it as DesktopListAdapter } ?: let {
            return null
        }
    }

    fun getRecyclerView(): RecyclerView {
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

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (!isVisibleToUser) {
            //如果隐藏了检测是否存在隐式插入的ItemView
            val adapter = getAdapter()
            adapter?.let {
                if (it.isImplicitInset()) {
                    it.implicitRemove()
                    Log.d(TAG, "setUserVisibleHint: 不显示了，并且当前页面存在隐式View，执行删除")
                }
            }
        }
    }

    // 委托的类
    inner class Delegate {
        internal operator fun getValue(thisRef: Any?, property: KProperty<*>): DesktopListAdapter {
            DesktopListAdapter(context, listData, iDesktopListAdapter).run {
                appStyle = this@FragmentContent.appStyle
                return this
            }
        }
    }

    private inner class ItemTouchHelperCallback : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
        ): Int {
            return if (recyclerView.layoutManager is GridLayoutManager) {
                val dragFlags =
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                val swipeFlags = 0
                makeMovementFlags(
                    dragFlags, swipeFlags
                )
            } else {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = 0
                makeMovementFlags(
                    dragFlags, swipeFlags
                )
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            Log.d("FragmentContent", "onMove: ")

            //得到当拖拽的viewHolder的Position
            val fromPosition = viewHolder.adapterPosition
            //拿到当前拖拽到的item的viewHolder
            val toPosition = target.adapterPosition
            //            replaceLocal(fromPosition, toPosition, listData)
            rvDesktopList?.adapter?.run {
                notifyItemMoved(fromPosition, toPosition)
            }
            return true
        }


        /**
         * 滑动删除
         */
        override fun onSwiped(
            viewHolder: RecyclerView.ViewHolder, direction: Int
        ) {
        }

        /**
         * 长按选中Item的时候开始调用
         *
         * @param viewHolder
         * @param actionState
         */
        override fun onSelectedChanged(
            viewHolder: RecyclerView.ViewHolder?, actionState: Int
        ) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                viewHolder?.let { vh ->
                    vh.itemView.let { iv ->
                        iItemViewInteractive.selectViewRect(
                            iv, vh.adapterPosition, this@FragmentContent
                        )
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
        override fun clearView(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
        ) {
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

        fun newInstance(
            position: Int,
            mutableList: MutableList<IBindDataModel>,
            spanCount: Int,
            appStyle: AppStyle,
            iDesktopListAdapter: IDesktopListAdapter,
            iItemViewInteractive: IItemViewInteractive
        ): Fragment {
            val fragmentContent = FragmentContent()
            val bundle = Bundle()
            bundle.putInt(POSITION, position)
            bundle.putInt(SPANCOUNT, spanCount)
            bundle.putSerializable(APPSTYLE, appStyle)
            bundle.putParcelable(IDESKTOPLISTADAPTER, iDesktopListAdapter)
            bundle.putParcelable(IITEMVIEWINTERACTIVE, iItemViewInteractive)
            bundle.putParcelableArrayList(MUTABLELIST, mutableList as ArrayList<IBindDataModel>)
            fragmentContent.arguments = bundle
            return fragmentContent
        }

        private val POSITION = "position"
        private val MUTABLELIST = "mutableList"
        private val SPANCOUNT = "spanCount"
        private val APPSTYLE = "appStyle"
        private val IDESKTOPLISTADAPTER = "iDesktopListAdapter"
        private val IITEMVIEWINTERACTIVE = "IItemViewInteractive"
        private const val TAG = "FragmentContent"
    }
}


