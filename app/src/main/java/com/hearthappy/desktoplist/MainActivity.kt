package com.hearthappy.desktoplist

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
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
                1 -> dlv.setAppStyle(AppStyle.Rounded(24)).notifyChangeStyle()
                2 -> dlv.setAppStyle(AppStyle.Circle).notifyChangeStyle()
                else -> dlv.setAppStyle(AppStyle.NotStyle).notifyChangeStyle()
            }
            Toast.makeText(this, "切换成功", Toast.LENGTH_SHORT).show()
        }

        btnSwitchTransformPage.setOnClickListener {
            when (++transformPagerIndex % 3) {
                1 -> dlv.setTransformAnimation(PagerTransformer.AnimSpecies.Windmill).notifyChangeStyle()
                2 -> dlv.setTransformAnimation(PagerTransformer.AnimSpecies.FloatUp).notifyChangeStyle()
                else -> dlv.setTransformAnimation(PagerTransformer.AnimSpecies.Translate).notifyChangeStyle()
            }
            Toast.makeText(this, "切换成功", Toast.LENGTH_SHORT).show()
        }

        btnRefresh.setOnClickListener {
            dlv.notifyUpdateCurrentPage()
        }

        /**
         * 参数分别是：1、每行显示列数  2、实现IDesktopDataModel接口的数据集合
         */
        dlv.init(iDesktopList = DesktopDataModel(), 4)
        dlv.setDesktopAdapterListener(object : ItemViewListener {
            override fun onClick(position: Int, list: List<IBindDataModel>) {
                Toast.makeText(this@MainActivity, "position:$position,name:${list[position].getAppName()}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    /**
     * 支持横竖屏方向下，显示不同列数
     * 注意：需要在清单文件中。在当前Activity中增加 android:configChanges="orientation|keyboardHidden|screenSize"
     * @param newConfig Configuration
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, "onConfigurationChanged: 竖屏")
            dlv.init(iDesktopList = DesktopDataModel(), 4)
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "onConfigurationChanged: 横屏")
            dlv.init(iDesktopList = DesktopDataModel(), 6)
        }
    }


    companion object {
        private const val TAG = "MainActivity"
    }

}
