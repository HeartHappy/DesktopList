package com.hearthappy.desktoplist.test

import android.content.Context
import android.widget.Toast
import com.bumptech.glide.Glide
import com.hearthappy.desktoplist.R
import com.hearthappy.desktoplist.appstyle.AppStyle
import com.hearthappy.desktoplist.desktopview.DesktopListAdapter
import com.hearthappy.desktoplist.interfaces.IBindDataModel
import com.hearthappy.desktoplist.interfaces.IDesktopListAdapter
import kotlinx.android.parcel.Parcelize

/**
 * Created Date 2021/1/4.
 * @author ChenRui
 * ClassDescription: 重写桌面列表适配器，自己实现逻辑处理
 *
 */
@Parcelize class DesktopListAdapterImpl : IDesktopListAdapter {


    /**
     * 适配器的布局
     * @return Int
     */
    override fun onAdapterResId(): Int {
        return R.layout.item_app_list
    }

    override fun onBindMyViewHolder(
        context: Context?,
        holder: DesktopListAdapter.ViewHolder,
        position: Int,
        list: List<IBindDataModel>,
        appStyle: AppStyle
    ) {
        holder.tvText.text = list[position].getAppName()
        context?.let {
            Glide.with(it).load(list[position].getAppUrl()).placeholder(R.mipmap.ic_launcher)
                .error(android.R.drawable.ic_menu_report_image).into(holder.ivAppIcon)
        }
        holder.itemView.setOnClickListener {
            Toast.makeText(
                context,
                "position:$position,name:${list[position].getAppName()}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * 注意：该返回值同你的布局高度填写，用于计算每个页面最多显示数量时使用
     * @return Int  R.dimen.dp_~
     */
    override fun onItemViewHeight(): Int {
        return R.dimen.dp_110
    }
}