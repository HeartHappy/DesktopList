package com.hearthappy.desktoplist.test

import android.content.Context
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.hearthappy.desktoplist.R
import com.hearthappy.desktoplist.desktopview.DesktopListAdapter
import com.hearthappy.desktoplist.desktopview.appstyle.AppStyle
import com.hearthappy.desktoplist.desktopview.interfaces.IBindDataModel
import com.hearthappy.desktoplist.desktopview.interfaces.IDesktopListAdapter

/**
 * Created Date 2021/1/4.
 * @author ChenRui
 * ClassDescription: 重写桌面列表适配器，自己实现逻辑处理
 *
 */
class DesktopListAdapterImpl : IDesktopListAdapter {

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
                .error(android.R.drawable.ic_menu_report_image).apply(
                    when (appStyle) {
                        is AppStyle.Circle -> RequestOptions.circleCropTransform()
                        is AppStyle.Rounded -> RequestOptions().transform(
                            CenterCrop(), RoundedCorners(appStyle.radius)
                        )
                        else -> {
                            RequestOptions.centerCropTransform()
                        }
                    }
                ).into(holder.ivAppIcon)
        }

        holder.itemView.setOnClickListener {
            Toast.makeText(
                context,
                "position:$position,name:${list[position].getAppName()}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}