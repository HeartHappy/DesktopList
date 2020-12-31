package com.hearthappy.desktoplist.desktopview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.hearthappy.desktoplist.DataModel
import com.hearthappy.desktoplist.R
import com.hearthappy.desktoplist.desktopview.appstyle.AppStyle
import kotlinx.android.synthetic.main.fragment_tab_main.*
import java.util.*


/**
 * Created Date 2020/12/21.
 * @author ChenRui
 * ClassDescription:动态创建的分页Fragment
 */
class FragmentContent(
    private val position: Int,
    private val listData: MutableList<Any>,
    private val iDesktopList: IDesktopList,
    private val spanCount: Int,
    private val appStyle: AppStyle,
    private val iItemViewInteractive: IItemViewInteractive
) : Fragment() {

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
        rvDesktopList.adapter =
            context?.let { DesktopListAdapter(it, listData, iDesktopList, appStyle) }
        rvDesktopList.itemAnimator?.changeDuration = 0
        //绑定移动View
        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback())
        itemTouchHelper.attachToRecyclerView(rvDesktopList)
        Log.d(TAG, "onViewCreated: $position")
    }


    fun getAdapter(): DesktopListAdapter<Any>? {
        rvDesktopList?.adapter?.let { return it as DesktopListAdapter<Any> } ?: let {
            return null
        }
    }

    fun getRecyclerView(): RecyclerView {
        return rvDesktopList
    }

    fun replaceLocal(fromPosition: Int, toPosition: Int, listData: MutableList<Any>) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ")
    }


    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach: ")
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


    private fun getDecorView(): FrameLayout? {
        activity?.window?.decorView?.let {
            return it as FrameLayout
        }
        Log.d(TAG, "getDecorView: 返回空了$activity")
        return null
    }

    companion object {
        private const val TAG = "FragmentContent"
    }
}

