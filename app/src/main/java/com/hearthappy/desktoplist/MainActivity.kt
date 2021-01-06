package com.hearthappy.desktoplist

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hearthappy.desktoplist.appstyle.AppStyle
import com.hearthappy.desktoplist.test.DesktopDataModel
import com.hearthappy.desktoplist.test.DesktopListAdapterImpl
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
                1 -> dlv.appStyle(AppStyle.Rounded(24)).notifyChange()
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

        /**
         * 参数分别是：1、每行显示列数  2、实现IDesktopDataModel接口的数据集合  3、实现IDesktopListAdapter接口的适配器
         */
        dlv.init(3, DesktopDataModel(), DesktopListAdapterImpl())
    }

    //1、获取父容器的MeasureSpec，即：大小和模式，通过MeasureSpec.makeMeasureSpec(大小，模式)

    //2、通过父容器MeasureSpec测量孩子，获取子View的MeasureSpec

    //3、通过子View.measure方法child.measure(childWidthMeasureSpec, childHeightMeasureSpec);


    companion object {
        private const val TAG = "MainActivity"
    }

}
