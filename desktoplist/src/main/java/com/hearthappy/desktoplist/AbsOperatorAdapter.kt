package com.hearthappy.desktoplist

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.hearthappy.interfaces.IBindDataModel
import java.util.*


/**
 * Created Date 2020/12/31.
 * @author ChenRui
 * ClassDescription:抽象的适配器
 */
abstract class AbsOperatorAdapter<VH : RecyclerView.ViewHolder, in DB : IBindDataModel>(private val dataModels: List<DB>) : RecyclerView.Adapter<VH>() {
    private var implicitPosition = -1 //隐式插入下标，会发生改变
    private var implicitPositionFirstInset = -1 //首次插入隐式位置，不会发生改变
    var fromPosition: Int = -1
    private var isJitterAnimator = false
    private val jitterCache = mutableMapOf<Int, ObjectAnimator>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return createMyViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (!(hideImplicitPosition(position, holder) || hideFromPosition(position, holder))) {
            showItemView(holder)
        }
        onBindMyViewHolder(holder, position)
    }


    /**
     * 隐藏隐式插入的position
     * @param position Int
     * @param holder VH
     */
    private fun hideImplicitPosition(position: Int, holder: VH): Boolean {
        if (implicitPosition != -1 && implicitPosition == position) {
            holder.itemView.visibility = View.INVISIBLE
            Log.d(TAG, "onBindViewHolder: 存在隐式View：$implicitPosition")
            return true
        }
        return false
    }

    /**
     * 隐藏销毁页面的选中position
     * @param position Int
     * @param holder VH
     */
    private fun hideFromPosition(position: Int, holder: VH): Boolean {
        if (fromPosition != -1 && fromPosition == position) {
            holder.itemView.visibility = View.INVISIBLE
            Log.d(TAG, "onBindViewHolder: 隐藏来自销毁页面，经重创后的position:$fromPosition")
            return true
        }
        return false
    }

    /**
     * 显示ItemView
     * @param holder VH
     */
    private fun showItemView(holder: VH) {
        if (holder.itemView.visibility != View.VISIBLE) {
            Log.d(TAG, "onBindViewHolder: not fromPosition and implicitPosition")
            holder.itemView.visibility = View.VISIBLE
        }
    }


    /**
     * 设置抖动动画
     * @param view View
     * @param position Int 下标
     * @param isStart Boolean true：开始抖动   false：停止抖动
     */
    internal fun setJitterAnimator(view: View, position: Int, isStart: Boolean) {
        if (isStart) {
            val ofFloat = ObjectAnimator.ofFloat(view, "rotation", 0f, 10f, 0f, -10f, 0f)
            startJitterAnim(ofFloat)
            jitterCache[position] = ofFloat
            Log.d(TAG, "setJitterAnimator: 创建抖动:$position")
        } else {

            jitterCache[position]?.let {
                it.repeatCount = 0
                it.end()
                Log.d(TAG, "setJitterAnimator: 停止抖动:$position")
            }
            jitterCache.remove(position)
        }
    }

    /**
     * 开始抖动
     * @param it ObjectAnimator
     */
    private fun startJitterAnim(it: ObjectAnimator) {
        it.apply {
            interpolator = LinearInterpolator()
            duration = 700
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            start()
        }
    }

    override fun getItemCount(): Int = dataModels.size


    fun inset(position: Int, dataModel: DB) {
        addDataModel(position, dataModel)
        notifyItemInserted(position)
        notifyDataSetChanged()
    }

    fun implicitInset(position: Int, dataModel: DB) {
        addDataModel(position, dataModel)
        notifyItemRangeChanged(position, dataModels.size)
        implicitPosition = position
        implicitPositionFirstInset = position
    }

    fun implicitRemove() {
        removeDataModel(implicitPositionFirstInset)
        notifyItemRemoved(implicitPositionFirstInset)
        implicitPosition = -1
        implicitPositionFirstInset = -1
    }

    /**
     * 移动跨界面position,存在隐式插入
     * @param fromPosition Int
     * @param targetPosition Int
     */
    fun moveCrossPosition(fromPosition: Int, targetPosition: Int) {
        notifyItemMoved(fromPosition, targetPosition)
        implicitPosition = targetPosition
    }

    /**
     * 移动来自页面position
     * @param fromPosition Int
     * @param targetPosition Int
     */
    fun moveFromPosition(fromPosition: Int, targetPosition: Int) {
        this.fromPosition = targetPosition
        notifyItemMoved(fromPosition, targetPosition)
    }

    /**
     * 隐藏来自页面拖动Position
     * @param position Int
     */
    fun hideFromPosition(position: Int) {
        this.fromPosition = position
        notifyItemChanged(fromPosition)
    }

    /**
     * 显示来自页面拖动position
     */
    fun showFromPosition() {
        if (fromPosition == -1) return
        val tempPosition = fromPosition
        this.fromPosition = -1
        notifyItemChanged(tempPosition)
    }


    fun notifyDataChanged() {
        notifyDataSetChanged()
    }

    fun getImplicitPosition(): Int {
        return implicitPosition
    }

    fun getImplicitPositionInFirstInset(): Int {
        return implicitPositionFirstInset
    }


    fun resetImplicitPosition() {
        implicitPosition = -1
        implicitPositionFirstInset = -1
    }

    fun getImplicitPositionIsChange(): Boolean {
        if (implicitPosition != -1 && implicitPosition != implicitPositionFirstInset) {
            Log.d(TAG, "getImplicitPositionIsChange: 隐式位置发生改变$implicitPosition,$implicitPositionFirstInset")
            return true
        }
        return false
    }

    fun isImplicitInset(): Boolean {
        return implicitPosition > -1
    }

    abstract fun createMyViewHolder(parent: ViewGroup, viewType: Int): VH


    abstract fun onBindMyViewHolder(holder: VH, position: Int)


    /**
     * 移动数据索引位置
     * @param fromPosition Int
     * @param toPosition Int
     * @param listData MutableList<*>
     */
    fun moveDataModel(fromPosition: Int, toPosition: Int, listData: MutableList<*>) {
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

    /**
     * 添加数据
     * @param position Int
     * @param dataModel DB
     */
    private fun addDataModel(position: Int, dataModel: DB) {
        if (dataModels is ArrayList<DB>) {
            dataModels.add(position, dataModel)
        } else if (dataModels is MutableList<DB>) {
            dataModels.add(position, dataModel)
        }
    }

    /**
     * 删除数据
     * @param position Int
     */
    private fun removeDataModel(position: Int) {
        if (dataModels is ArrayList<DB>) {
            dataModels.removeAt(position)
        } else if (dataModels is MutableList<DB>) {
            dataModels.removeAt(position)
        }
    }

    companion object {
        private const val TAG = "AbsAdapter"
    }
}