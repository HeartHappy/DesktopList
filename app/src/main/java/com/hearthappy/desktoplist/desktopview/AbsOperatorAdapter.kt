package com.hearthappy.desktoplist.desktopview

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hearthappy.desktoplist.appstyle.AppStyle
import com.hearthappy.desktoplist.interfaces.IBindDataModel


/**
 * Created Date 2020/12/31.
 * @author ChenRui
 * ClassDescription:抽象的适配器
 */
abstract class AbsOperatorAdapter<VH : RecyclerView.ViewHolder, in DB : IBindDataModel>(private val dataModels: List<DB>) : RecyclerView.Adapter<VH>() {
    private var implicitPosition = -1 //隐式插入下标，会发生改变
    private var implicitPositionFirstInset = -1 //首次插入隐式位置，不会发生改变
    var destroyPageAdapterSelPosition: Int = -1
    lateinit var appStyle: AppStyle
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return createMyViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (!(hideImplicitPosition(position, holder) || hideDestroyPageSelPosition(position, holder))) {
            showItemView(holder)
        }
        onBindMyViewHolder(holder, position, appStyle)
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
    private fun hideDestroyPageSelPosition(position: Int, holder: VH): Boolean {
        if (destroyPageAdapterSelPosition != -1 && destroyPageAdapterSelPosition == position) {
            holder.itemView.visibility = View.INVISIBLE
            Log.d(TAG, "onBindViewHolder: 隐藏来自销毁页面，经重创后的position:$destroyPageAdapterSelPosition")
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
            Log.d(TAG, "onBindViewHolder: 更新时被无缘无故隐藏")
            holder.itemView.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = dataModels.size


    fun inset(position: Int, dataModel: DB) {
        addDataModel(position, dataModel)
        notifyItemInserted(position)
        notifyDataSetChanged()
    }

    /**
     * 隐式插入
     */
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
        Log.d(TAG, "implicitRemove: 隐式删除$implicitPositionFirstInset")
    }

    fun move(fromPosition: Int, targetPosition: Int) {
        notifyItemMoved(fromPosition, targetPosition)
        implicitPosition = targetPosition
    }

    fun moveDestroyPageAdapterSelPosition(fromPosition: Int, targetPosition: Int) {
        this.destroyPageAdapterSelPosition = targetPosition
        notifyItemMoved(fromPosition, targetPosition)
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

    fun resetFromPosition() {
        if (destroyPageAdapterSelPosition == -1) return
        val tempPosition = destroyPageAdapterSelPosition
        destroyPageAdapterSelPosition = -1
        notifyItemChanged(tempPosition)
        Log.d(TAG, "resetFromPosition: $tempPosition")
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


    abstract fun onBindMyViewHolder(holder: VH, position: Int, appStyle: AppStyle)

    private fun addDataModel(position: Int, dataModel: DB) {
        if (dataModels is ArrayList<DB>) {
            dataModels.add(position, dataModel)
        } else if (dataModels is MutableList<DB>) {
            dataModels.add(position, dataModel)
        }
    }

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