package com.hearthappy.desktoplist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.RecyclerView
import com.hearthappy.appstyle.AppStyle
import com.hearthappy.desktoplist.databinding.ItemAppListBinding
import com.hearthappy.interfaces.IBindDataModel

/**
 * Created Date 2020/12/31.
 * @author ChenRui
 * ClassDescription:内部桌面适配器逻辑处理类，供用户实现
 */
class DesktopListAdapter(
    private val context: Context?,
    private val list: List<IBindDataModel>,
    private val iItemViewInteractive: IItemViewInteractive,
    private val parent: ViewParent,
) : AbsOperatorAdapter<DesktopListAdapter.ViewHolder, IBindDataModel>(list) {


    override fun createMyViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = ItemAppListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(
            viewBinding
        )
    }


    override fun onBindMyViewHolder(holder: ViewHolder, position: Int) {

        //切换样式
        if (parent is DesktopListView) {
            holder.viewBinding.apply { //视图与数据的绑定交由用户
                iItemViewInteractive.onBindView(
                    position, list, holder.viewBinding, parent.isShowAppId
                )

                root.setOnClickListener {
                    iItemViewInteractive.onClick(position, list)
                }
            }
            holder.bindAppStyle(parent.getAppStyle())
            holder.enableJitter(parent.isDragItemView())
        }
    }


    inner class ViewHolder(val viewBinding: ItemAppListBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bindAppStyle(appStyle: AppStyle) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                viewBinding.appIcon.round = when (appStyle) {
                    is AppStyle.Circle -> context?.let { (it.resources.getDimensionPixelSize(R.dimen.dp_52) / 2).toFloat() } ?: let { 0f }
                    is AppStyle.Rounded -> appStyle.radius.toFloat()
                    is AppStyle.NotStyle -> 0f
                }
            }
        }

        fun enableJitter(isEnabled: Boolean) {
            viewBinding.appIcon.enableJitter(isEnabled)
        }
    }
}
