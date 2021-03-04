package com.hearthappy.desktoplist

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hearthappy.desktoplist.appstyle.AppStyle
import com.hearthappy.desktoplist.interfaces.IBindDataModel
import com.hearthappy.desktoplist.interfaces.ItemViewListener
import com.hearthappy.desktoplist.test.DesktopDataModel
import com.hearthappy.desktoplist.transformpage.PagerTransformer
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var styleIndex = 0
    private var transformPagerIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnSwitchAppStyle.setOnClickListener {
            when (++styleIndex % 3) {
                1 -> dlv.appStyle(AppStyle.Rounded(24)).notifyChangeStyle()
                2 -> dlv.appStyle(AppStyle.Circle).notifyChangeStyle()
                else -> dlv.appStyle(AppStyle.NotStyle).notifyChangeStyle()
            }
            Toast.makeText(this, "切换成功", Toast.LENGTH_SHORT).show()
        }

        btnSwitchTransformPage.setOnClickListener {
            when (++transformPagerIndex % 3) {
                1 -> dlv.transformAnimation(PagerTransformer.AnimSpecies.Windmill).notifyChangeStyle()
                2 -> dlv.transformAnimation(PagerTransformer.AnimSpecies.FloatUp).notifyChangeStyle()
                else -> dlv.transformAnimation(PagerTransformer.AnimSpecies.Translate).notifyChangeStyle()
            }
            Toast.makeText(this, "切换成功", Toast.LENGTH_SHORT).show()
        }

        btnRefresh.setOnClickListener {
            dlv.notifyUpdateCurrentPage()
        }


        /**
         * 参数分别是：1、每行显示列数  2、实现IDesktopDataModel接口的数据集合
         */
        dlv.init(iDesktopList=DesktopDataModel())
        dlv.setDesktopAdapterListener(object : ItemViewListener {
            override fun onClick(position: Int, list: List<IBindDataModel>) {
                Toast.makeText(this@MainActivity, "position:$position,name:${list[position].getAppName()}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    companion object {
        private const val TAG = "MainActivity"
    }

}
