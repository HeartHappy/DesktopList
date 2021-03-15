package com.hearthappy.desktoplist

import android.os.Parcelable
import android.view.View
import com.hearthappy.desktoplist.databinding.ItemAppListBinding
import com.hearthappy.interfaces.IBindDataModel

/**
 * Created Date 2020/12/22.
 * @author ChenRui
 * ClassDescription:ItemView交互操作接口
 */
interface IItemViewInteractive : Parcelable {

    /**
     * 选中View位置
     */
    fun selectViewRect(selectView: View?, adapterPosition: Int, fragmentContent: FragmentContent)


    /**
     * 点击事件
     * @param position Int
     * @param list List<IBindDataModel>
     */
    fun onClick(position: Int, list: List<IBindDataModel>)

    /**
     * 长按
     * @param position Int
     * @param list List<IBindDataModel>
     */
    fun onLongClick(position: Int, list: List<IBindDataModel>)


    /**
     * 视图与数据的绑定
     * @param position Int
     * @param list List<IBindDataModel>
     * @param viewBinding ItemAppListBinding
     * @param showAppId 是否显示应用id
     */
    fun onBindView(
        position: Int,
        list: List<IBindDataModel>,
        viewBinding: ItemAppListBinding,
        showAppId: Boolean
    )


}