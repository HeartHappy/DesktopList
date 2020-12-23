package com.hearthappy.desktoplist.desktopview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hearthappy.desktoplist.DataModel

/**
 * Created Date 2020/12/21.
 * @author ChenRui
 * ClassDescription:
 */
class DesktopListAdapter(
    private val context: Context,
    private val listData: MutableList<DataModel>,
    private val iDesktopList: IDesktopList
) :
    RecyclerView.Adapter<DesktopListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(iDesktopList.adapterResId(), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        iDesktopList.onBindViewHolder(holder, position, listData)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}