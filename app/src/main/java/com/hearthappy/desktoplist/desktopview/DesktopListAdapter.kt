package com.hearthappy.desktoplist.desktopview

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hearthappy.desktoplist.DataModel
import com.hearthappy.desktoplist.R

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
//        iDesktopList.onBindViewHolder(holder, position, listData)

        Glide.with(context).load(listData[position].url)
            .placeholder(R.mipmap.ic_launcher)
            .error(android.R.drawable.ic_menu_report_image)
            .into(holder.itemView.findViewById(R.id.ivAppIcon))
        val textView = holder.itemView.findViewById(R.id.tvAppName) as TextView

        textView.text = listData[position].appName

        holder.itemView.setOnClickListener {
            Log.d("TAG", "onBindViewHolder:点击： $position,${listData.size}")
            Toast.makeText(
                context,
                "${listData[position].appName},position:$position",
                Toast.LENGTH_SHORT
            ).show()
        }
        Log.d(
            "TAG",
            "onBindViewHolder: $position,${listData.get(position).appName},url:${listData.get(
                position
            ).url}"
        )
    }

    fun inset(dataModel: DataModel, position: Int) {
        listData.add(position, dataModel)
        notifyItemInserted(position)
//        notifyItemRangeChanged(position,itemCount)
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        listData.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, itemCount)
    }

    fun replace(formPosition: Int, toPosition: Int, dataModel: DataModel) {
        remove(dataModel, formPosition)
        listData.add(toPosition, dataModel)
        notifyDataSetChanged()
        Log.d(
            "TAG",
            "replace: 选中DM:${dataModel.appName},url:${dataModel.url},$formPosition,$toPosition"
        )
    }

    fun remove(dataModel: DataModel, position: Int) {
        val listIterator = listData.listIterator()
        while (listIterator.hasNext()) {
            if (listIterator.next() == dataModel) {
                Log.d("remove", "remove: 找到了")
                listIterator.remove()
            }
        }
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, itemCount)
    }

    fun notifyDataChanged(){
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}