package com.hearthappy.desktoplist.desktopview

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hearthappy.desktoplist.desktopview.appstyle.AppStyle


/**
 * Created Date 2020/12/21.
 * @author ChenRui
 * ClassDescription:
 */
class DesktopListAdapter<T>(
    private val context: Context,
    private val listData: MutableList<T>,
    private val iDesktopList: IDesktopList,
    private val appStyle: AppStyle
) : RecyclerView.Adapter<DesktopListAdapter<T>.ViewHolder>() {

    private var implicitPosition = -1 //隐式插入下标，会发生改变
    private var implicitPositionFirstInset = -1 //首次插入隐式位置，不会发生改变
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(iDesktopList.adapterResId(), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        if (implicitPosition != -1 && implicitPosition == position) {
            holder.itemView.visibility = View.INVISIBLE
            Log.d(TAG, "onBindViewHolder: 存在隐式View：$implicitPosition")
        } else {
            if (holder.itemView.visibility != View.VISIBLE) {
                Log.d(TAG, "onBindViewHolder: 更新时被无缘无故隐藏")
                holder.itemView.visibility = View.VISIBLE
            }
        }
        //已经正常打印出来了
        println("position:$position,${listData[position].toString()}")
//                iDesktopList.onBindViewHolder(holder, position, listData)

        //设置图片圆角角度
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        /* Glide.with(context).load(listData[position].url).placeholder(R.mipmap.ic_launcher)
             .error(android.R.drawable.ic_menu_report_image).apply {
                 when (appStyle) {
                     is AppStyle.Circle -> apply(RequestOptions.circleCropTransform())
                     is AppStyle.Rounded -> apply(
                         RequestOptions().transform(
                             CenterCrop(), RoundedCorners(appStyle.radius)
                         )
                     )
                 }
             }.into(holder.itemView.findViewById(R.id.ivAppIcon))
         val textView = holder.itemView.findViewById(R.id.tvAppName) as TextView

         textView.text = listData[position].appName

         holder.itemView.setOnClickListener {
             Log.d(TAG, "onBindViewHolder:点击： $position,${listData.size}")
             Toast.makeText(
                 context, "${listData[position].appName},position:$position", Toast.LENGTH_SHORT
             ).show()
         }
         Log.d(
             TAG,
             "onBindViewHolder: $position,${listData[position].appName},url:${listData[position].url},是否显示${holder.itemView.visibility == View.VISIBLE},隐式View：$implicitPosition"
         )*/
    }

    fun inset(dataModel: T, position: Int) {
        listData.add(position, dataModel)
        notifyItemInserted(position)
        notifyDataSetChanged()
    }

    /**
     * 隐式插入
     */
    fun implicitInset(dataModel: T, position: Int) {
        listData.add(position, dataModel)
        notifyItemRangeChanged(position, listData.size)
        implicitPosition = position
        implicitPositionFirstInset = position
    }

    fun implicitRemove() {
        listData.removeAt(implicitPositionFirstInset)
        notifyItemRemoved(implicitPositionFirstInset)
        implicitPosition = -1
        implicitPositionFirstInset = -1
        Log.d(TAG, "implicitRemove: 隐式删除$implicitPositionFirstInset")
    }

    fun move(fromPosition: Int, targetPosition: Int) {
        notifyItemMoved(fromPosition, targetPosition)
        implicitPosition = targetPosition
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
            Log.d(
                TAG,
                "getImplicitPositionIsChange: 隐式位置发生改变$implicitPosition,$implicitPositionFirstInset"
            )
            return true
        }
        return false
    }

    fun isImplicitInset(): Boolean {
        return implicitPosition > -1
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private const val TAG = "DesktopListAdapter"
    }
}