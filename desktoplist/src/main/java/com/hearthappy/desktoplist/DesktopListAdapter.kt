package com.hearthappy.desktoplist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hearthappy.appstyle.AppStyle
import com.hearthappy.desktoplist.databinding.ItemAppListBinding
import com.hearthappy.desktoplist.weiget.JitterImageView
import com.hearthappy.interfaces.IBindDataModel

/**
 * Created Date 2020/12/31.
 * @author ChenRui
 * ClassDescription:内部桌面适配器逻辑处理类，供用户实现
 */
class DesktopListAdapter(private val context: Context?, private val list: List<IBindDataModel>, private val iItemViewInteractive: IItemViewInteractive, private val parent: ViewParent) : AbsOperatorAdapter<DesktopListAdapter.ViewHolder, IBindDataModel>(list) {
    private lateinit var viewBinding:ItemAppListBinding

    override fun createMyViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        viewBinding= ItemAppListBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(viewBinding)
    }


    override fun onBindMyViewHolder(holder: ViewHolder, position: Int) {

        //加载数据
        holder.appText.text = list[position].getAppName()
        context?.let {
            Glide.with(it).load(list[position].getAppUrl()).placeholder(R.mipmap.ic_launcher).error(android.R.drawable.ic_menu_report_image).into(holder.appIcon)
        }
        holder.itemView.setOnClickListener {
            iItemViewInteractive.onClick(position, list)
        }
        if (parent is DesktopListView) {
            //切换样式
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                val appStyle = parent.getAppStyle()
                holder.appIcon.round = when (appStyle) {
                    is AppStyle.Circle -> context?.let { (it.resources.getDimensionPixelSize(R.dimen.dp_52) / 2).toFloat() } ?: let { 0f }
                    is AppStyle.Rounded -> appStyle.radius.toFloat()
                    is AppStyle.NotStyle -> 0f
                }
            }
            holder.appIcon.enableJitter(parent.isExistFloatView())
        }
    }


    inner class ViewHolder(viewBinding: ItemAppListBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        val appText: TextView = viewBinding.appName
        val appIcon: JitterImageView = viewBinding.appIcon
    }

    companion object {
        private const val TAG = "DesktopListAdapter"
    }
}
