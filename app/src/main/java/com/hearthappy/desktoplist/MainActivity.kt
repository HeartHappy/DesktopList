package com.hearthappy.desktoplist

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hearthappy.desktoplist.desktopview.appstyle.AppStyle
import com.hearthappy.desktoplist.desktopview.transformpage.PagerTransformer
import com.hearthappy.desktoplist.test.DesktopDataModel
import com.hearthappy.desktoplist.test.DesktopListAdapterImpl
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var styleIndex = 0
    private var transformPagerIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSwitchAppStyle.setOnClickListener {
            when (++styleIndex % 3) {
                1 -> dlv.appStyle(AppStyle.Rounded(16)).notifyChange()
                2 -> dlv.appStyle(AppStyle.Circle).notifyChange()
                else -> dlv.appStyle(AppStyle.NotStyle).notifyChange()
            }
            Toast.makeText(this, "切换成功", Toast.LENGTH_SHORT).show()
        }

        btnSwitchTransformPage.setOnClickListener {
            when (++transformPagerIndex % 3) {
                1 -> dlv.transformAnimation(PagerTransformer.AnimSpecies.Windmill).notifyChange()
                2 -> dlv.transformAnimation(PagerTransformer.AnimSpecies.FloatUp).notifyChange()
                else -> dlv.transformAnimation(PagerTransformer.AnimSpecies.Translate)
                    .notifyChange()
            }
            Toast.makeText(this, "切换成功", Toast.LENGTH_SHORT).show()
        }

        val desktopDataModel = DesktopDataModel()
        val desktopListAdapterImpl = DesktopListAdapterImpl()
        //初始化数据集
        dlv.init(3, 15, desktopDataModel, desktopListAdapterImpl)
    }


    companion object {
        private const val TAG = "MainActivity"
    }

}
