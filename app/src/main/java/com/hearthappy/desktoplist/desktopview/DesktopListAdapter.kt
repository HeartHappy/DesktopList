package com.hearthappy.desktoplist.desktopview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hearthappy.desktoplist.desktopview.appstyle.AppStyle
import com.hearthappy.desktoplist.desktopview.interfaces.IBindDataModel
import com.hearthappy.desktoplist.desktopview.interfaces.IDesktopListAdapter
import kotlinx.android.synthetic.main.item_app_list.view.*

/**
 * Created Date 2020/12/31.
 * @author ChenRui
 * ClassDescription:内部桌面适配器逻辑衔接类，供用户实现
 */
class DesktopListAdapter(
    private val context: Context?,
    private val list: List<IBindDataModel>,
    private val iDesktopListAdapter: IDesktopListAdapter
) : AbsAdapter<DesktopListAdapter.ViewHolder, IBindDataModel>(list) {


    override fun createMyViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(iDesktopListAdapter.onAdapterResId(), parent, false)
        )
    }


    override fun onBindMyViewHolder(holder: ViewHolder, position: Int, appStyle: AppStyle) {
        iDesktopListAdapter.onBindMyViewHolder(context, holder, position, list, appStyle)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvText: TextView = itemView.tvAppName
        val ivAppIcon: ImageView = itemView.ivAppIcon
    }
}
