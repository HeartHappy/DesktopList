package com.hearthappy.desktoplist.desktopview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.hearthappy.desktoplist.DataModel
import com.hearthappy.desktoplist.R
import com.hearthappy.desktoplist.desktopview.utils.ViewOperateUtils
import kotlinx.android.synthetic.main.fragment_tab_main.*
import java.util.*


/**
 * Created Date 2020/12/21.
 * @author ChenRui
 * ClassDescription:动态创建的分页Fragment
 */
class FragmentContent(
    private val position: Int,
    private val listData: MutableList<DataModel>,
    private val iDesktopList: IDesktopList,
    private val spanCount: Int,
    private val iItemViewInteractive: IItemViewInteractive
) :
    Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvDesktopList = view.findViewById<RecyclerView>(R.id.rvDesktopList)
        val gridLayoutManager = GridLayoutManager(context, spanCount)
        rvDesktopList.layoutManager = gridLayoutManager
        //涉及数据绑定View的交给用户自定义
        rvDesktopList.adapter = context?.let { DesktopListAdapter(it, listData, iDesktopList) }
        //绑定移动View
        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback())
        itemTouchHelper.attachToRecyclerView(rvDesktopList)
        Log.d(TAG, "onViewCreated: $position")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: $position")
    }

    // TODO: 2020/12/22 因缓存问题，Fragment被销毁，导致无法获取Activity的窗体
    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach: $position")
    }

    inner class ItemTouchHelperCallback : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return if (recyclerView.layoutManager is GridLayoutManager) {
                val dragFlags =
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                val swipeFlags = 0
                makeMovementFlags(
                    dragFlags,
                    swipeFlags
                )
            } else {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = 0
                makeMovementFlags(
                    dragFlags,
                    swipeFlags
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
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(listData, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(listData, i, i - 1)
                }
            }
            rvDesktopList.adapter?.notifyItemMoved(fromPosition, toPosition)
            return true
        }

        override fun onSwiped(
            viewHolder: RecyclerView.ViewHolder,
            direction: Int
        ) {
        }

        /**
         * 长按选中Item的时候开始调用
         *
         * @param viewHolder
         * @param actionState
         */
        override fun onSelectedChanged(
            viewHolder: RecyclerView.ViewHolder?,
            actionState: Int
        ) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                viewHolder?.itemView?.let { iv ->
                    val frameLayout = getDecorView()
                    //将原ImageView属性copy
                    val linearLayout = iv as LinearLayout
                    val imageView = linearLayout.getChildAt(0) as ImageView

                    val tempImageView = ImageView(context)
                    val imageViewLayoutParams =
                        FrameLayout.LayoutParams(imageView.width, imageView.height)
                    tempImageView.setImageDrawable(imageView.drawable)
                    tempImageView.layoutParams = imageViewLayoutParams
                    tempImageView.scaleType = imageView.scaleType
                    tempImageView.id = R.id.createView
                    //设置View位置
                    val moveViewRect = ViewOperateUtils.findViewLocation(imageView)
                    val layoutParams = FrameLayout.LayoutParams(imageView.width, imageView.height)
                    layoutParams.setMargins(
                        moveViewRect.left.toInt(),
                        moveViewRect.top.toInt(),
                        0,
                        0
                    )
                    //添加DecorView到视图
                    frameLayout?.addView(tempImageView, layoutParams)
                    //隐藏原有视图
                    linearLayout.visibility = View.GONE

                    iItemViewInteractive.selectViewRect(moveViewRect)
                    tempImageView.animate().scaleX(1.2f).scaleY(1.2f).start()
                    Log.d(
                        "FragmentContent",
                        "createTempView: 创建临时View${imageView.width},${imageView.height}"
                    )
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
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ) {
            //删除临时View
            val frameLayout = getDecorView()
            val tempView = getTempView(frameLayout)
            tempView?.let {
                iItemViewInteractive.releaseView(it)
                //删除临时视图
                frameLayout?.removeView(it)
                //显示原有视图
                viewHolder.itemView.visibility = View.VISIBLE
                Log.d("FragmentContent", "clearView: 删除临时View")
            } ?: let {
                Log.d(TAG, "clearView: $tempView")
            }
        }

        /**
         * 重写是否启用拖拽，true：启用
         * @return
         */
        override fun isLongPressDragEnabled(): Boolean {
            return true
        }
    }


    private fun getTempView(frameLayout: FrameLayout?): View? {
        return frameLayout?.findViewById(R.id.createView)
    }

    private fun getDecorView(): FrameLayout? {
        activity?.window?.decorView?.let {
            return it as FrameLayout
        }
        return null
    }

    companion object {
        private const val TAG = "FragmentContent"
    }
}