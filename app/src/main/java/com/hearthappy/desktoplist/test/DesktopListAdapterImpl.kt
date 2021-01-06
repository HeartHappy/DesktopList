package com.hearthappy.desktoplist.test

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.hearthappy.desktoplist.R
import com.hearthappy.desktoplist.desktopview.DesktopListAdapter
import com.hearthappy.desktoplist.appstyle.AppStyle
import com.hearthappy.desktoplist.interfaces.IBindDataModel
import com.hearthappy.desktoplist.interfaces.IDesktopListAdapter

/**
 * Created Date 2021/1/4.
 * @author ChenRui
 * ClassDescription: 重写桌面列表适配器，自己实现逻辑处理
 *
 */
class DesktopListAdapterImpl() : IDesktopListAdapter {

    constructor(parcel: Parcel) : this() {
    }

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        super.writeToParcel(parcel, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DesktopListAdapterImpl> {
        override fun createFromParcel(parcel: Parcel): DesktopListAdapterImpl {
            return DesktopListAdapterImpl(parcel)
        }

        override fun newArray(size: Int): Array<DesktopListAdapterImpl?> {
            return arrayOfNulls(size)
        }
    }
}